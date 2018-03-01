package brotherdetjr.gama;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import static com.google.common.collect.Maps.newConcurrentMap;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;

public final class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final int framePeriodInSeconds = 2;

    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
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
                            log.debug("Sending pixoterm content to: {}", token);
                            String json = objectMapper.writeValueAsString(mapAnimation.get(session.nextFrame()));
                            session.getWsSession().getRemote().sendStringByFuture(json);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
                },
                framePeriodInSeconds,
                framePeriodInSeconds,
                SECONDS
        );
        Javalin.create()
                .accessManager((handler, ctx, permittedRoles) -> {
                    log.debug("Permitted roles: {}", permittedRoles);
                    handler.handle(ctx);
                })
                .enableStaticFiles("/static")
                .ws("/websocket", ws -> {
                            ws.onConnect(session -> {
                                log.debug("Starting new session with ID: {}", session.getId());
                                sessions.put(session.getId(), new UserSession(session));
                            });
                            ws.onClose((session, statusCode, reason) -> {
                                log.debug("Closing session with ID: {}", session.getId());
                                sessions.remove(session.getId());
                            });
                        }
                )
                .port(8080)
                .start();
    }


}