package brotherdetjr.gama;

import brotherdetjr.gama.parser.WorldParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.javalin.Javalin;
import io.javalin.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;

import static brotherdetjr.gama.Direction.DOWN;
import static brotherdetjr.gama.PropelledItem.newPropelledItem;
import static brotherdetjr.gama.ResourceUtils.asString;
import static brotherdetjr.gama.UserRole.PLAYER;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newConcurrentMap;
import static com.google.common.collect.Sets.intersection;
import static io.javalin.ApiBuilder.get;
import static io.javalin.security.Role.roles;
import static java.lang.Math.abs;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        int httpPort = 8080;
        int framePeriodInMillis = 2000;
        ObjectMapper objectMapper = new ObjectMapper();
        AuthService authService = new AuthServiceImpl(new Random().nextLong());
        ImmutableMap<Integer, String> gidToSprite =
                ImmutableMap.of(18, "sand_0", 19, "sand_1", 20, "sand_2", 21, "sand_3");
        World world = WorldParser.parse(
                asString("map.tmx"),
                gidToSprite,
                asString("static/composition.json"),
                true
        );
        PropelledItemMoveHandler propelledItemMoveHandler = new PropelledItemMoveHandler(world);
        List<PropelledItem> bants = newArrayList();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int idx = world.nthFreeCellIndex(abs(random.nextInt()));
            PropelledItem it = newPropelledItem("bant", true)
                    .place(world.indexToRow(idx), world.indexToColumn(idx), 100).pointTo(DOWN);
            bants.add(it);
            world.attach(it);
        }
        PropelledItem pov = bants.get(0);
        Renderer renderer = new Renderer(32, 32, 2, 2, world);
        Supplier<Long> timestampSupplier = System::currentTimeMillis;
        Map<String, UserSession> sessions = newConcurrentMap();
        newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                () -> sessions.forEach((token, session) -> {
                    try {
                        Perception perception = renderer.render(pov, 7, 7, 2);
                        String json = objectMapper.writeValueAsString(perception);
                        session.timestampedWsSessions()
                                .forEach(entry -> {
                                    try {
                                        entry.getKey().getRemote().sendString(json);
                                        entry.getValue().set(timestampSupplier.get());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    Object lastRequest = session.takeLastRequest();
                    if (lastRequest != null) {
                        if (lastRequest instanceof MoveRequest) {
                            propelledItemMoveHandler.accept(pov, (MoveRequest) lastRequest);
                        }
                    }
                    for (PropelledItem it : bants) {
                        if (it != pov) {
                            MoveRequest r = new MoveRequest(Direction.values()[random.nextInt(Direction.values().length)]);
                            propelledItemMoveHandler.accept(it, r);
                        }
                    }
                    world.nextTick();
                }),
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
                            String token = authService.getToken(ctx);
                            String username = authService.extractUserName(ctx);
                            sessions.computeIfAbsent(token, ignore -> {
                                log.debug("Starting new session for user {} with token: {}", username, token);
                                return new UserSession(username);
                            });
                            ctx.renderFreemarker("main.html", ImmutableMap.of("token", token));
                        },
                        roles(PLAYER)
                ))
                .ws("/websocket", ws -> {
                            ws.onConnect(session -> {
                                String token = session.queryParam("token");
                                UserSession userSession = sessions.get(token);
                                if (userSession != null) {
                                    log.debug("New WebSocket for session with token: {}", token);
                                    userSession.addWsSession(session);
                                } else {
                                    log.warn("Requested new WebSocket for unknown token: {}. Disconnecting.", token);
                                    session.disconnect();
                                }
                            });
                            ws.onMessage((session, msg) -> {
                                String token = session.queryParam("token");
                                UserSession userSession = sessions.get(token);
                                MoveRequest moveRequest = objectMapper.readValue(msg, MoveRequest.class);
                                userSession.offerLastRequest(moveRequest);
                            });
                            ws.onClose((session, statusCode, reason) -> {
                                String token = session.queryParam("token");
                                log.debug("Closing WebSocket for session with token: {}", token);
                                sessions.get(token).removeWsSession(session);
                            });
                        }
                )
                .port(httpPort)
                .start();
    }

}