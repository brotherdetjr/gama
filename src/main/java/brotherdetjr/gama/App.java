package brotherdetjr.gama;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.javalin.Javalin;
import io.javalin.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import static brotherdetjr.gama.UserRole.GAMER;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Maps.newConcurrentMap;
import static com.google.common.collect.Sets.intersection;
import static io.javalin.ApiBuilder.get;
import static io.javalin.security.Role.roles;
import static java.lang.Thread.currentThread;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final int framePeriodInMillis = 2000;

    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        AuthService authService = new AuthServiceImpl();
        InputStream mapAnimationJson =
                currentThread().getContextClassLoader().getResourceAsStream("map_animation.json");
        List<List<List<List<CellEntry>>>> mapAnimation =
                objectMapper.readValue(mapAnimationJson, new TypeReference<List<List<List<List<CellEntry>>>>>() {
                });
        Map<String, UserSession> sessions = newConcurrentMap();
        ScheduledExecutorService scheduler = newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                () -> {
                    log.debug("Sending pixoterm content to the clients...");
                    sessions.forEach((token, session) -> {
                        try {
                            log.debug("Sending pixoterm content to WebSocket of session: {}", token);
                            String json = objectMapper.writeValueAsString(mapAnimation.get(session.nextFrame()));
                            session.getWsSession().getRemote().sendStringByFuture(json);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
                },
                framePeriodInMillis,
                framePeriodInMillis,
                MILLISECONDS
        );
        Javalin.create()
                .accessManager((handler, ctx, permittedRoles) -> {
                    String username = authService.extractUserName(ctx);
                    Set<UserRole> userRoles = authService.extractRoles(ctx);
                    Set<Role> accepted = intersection(copyOf(permittedRoles), userRoles);
                    log.debug("Resource accessed: {}, user name: {}, accepted roles: {}",
                            ctx.path(), username, accepted);
                    if (!accepted.isEmpty()) {
                        handler.handle(ctx);
                    } else {
                        ctx.status(401)
                                .header("WWW-Authenticate", "Basic realm=\"gama\"")
                                .result("Unauthorized");
                    }
                })
                .enableStaticFiles("/static")
                .routes(() -> get(
                        "/",
                        ctx -> {
                            String token = randomUUID().toString();
                            String username = authService.extractUserName(ctx);
                            log.debug("Starting new session for user {} with token: {}", username, token);
                            sessions.put(token, new UserSession(username));
                            ctx.renderFreemarker("main.html", ImmutableMap.of("token", token));
                        },
                        roles(GAMER)
                ))
                .ws("/websocket", ws -> {
                            ws.onConnect(session -> {
                                String token = session.queryParam("token");
                                log.debug("New WebSocket for session with token: {}", token);
                                sessions.get(token).setWsSession(session);
                            });
                            ws.onClose((session, statusCode, reason) -> {
                                String token = session.queryParam("token");
                                log.debug("Closing session with token: {}", token);
                                sessions.remove(token);
                            });
                        }
                )
                .port(8080)
                .start();
    }


}