package brotherdetjr.gama;

import brotherdetjr.gama.parser.WorldParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.javalin.Javalin;
import io.javalin.security.Role;
import org.eclipse.jetty.websocket.api.BatchMode;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.LongSupplier;

import static brotherdetjr.gama.Direction.DOWN;
import static brotherdetjr.gama.PropelledItem.newPropelledItem;
import static brotherdetjr.gama.ResourceUtils.asString;
import static brotherdetjr.gama.UserRole.PLAYER;
import static brotherdetjr.gama.Utils.nextLong;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newConcurrentMap;
import static com.google.common.collect.Maps.newTreeMap;
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
        long framePeriodInMillis = 2000;
        int seedSalt = 5537208;
        boolean keepDisconnectedUser = false;
        ObjectMapper objectMapper = new ObjectMapper();
        AuthService authService = new AuthServiceImpl(seedSalt);
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
        Random random = new Random(seedSalt);
        for (int i = 0; i < 100; i++) {
            bants.add(randomlyPlacedPropelledItem(world, random));
        }
        Renderer renderer = new Renderer(32, 32, 2, 2, world);
        LongSupplier timestampSupplier = System::nanoTime;
        Map<String, UserSession> sessions = newConcurrentMap();
        newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                () -> {
                    Map<Long, Map.Entry<PropelledItem, ?>> requests = newTreeMap();
                    for (PropelledItem it : bants) {
                        int idx = random.nextInt(Direction.values().length + 1);
                        if (idx < Direction.values().length) {
                            long reactionTime = nextLong(random, framePeriodInMillis * 1_000_000L);
                            Map.Entry<PropelledItem, ?> itemAndRequest =
                                    new SimpleEntry<>(it, new MoveRequest(Direction.values()[idx]));
                            requests.put(reactionTime, itemAndRequest);
                        }
                    }
                    sessions.forEach((token, session) -> {
                        Object lastRequest = session.takeRequest();
                        if (lastRequest != null) {
                            if (log.isDebugEnabled()) {
                                if (lastRequest instanceof MoveRequest) {
                                    log.debug("{} is moving {}",
                                            session.getUsername(),
                                            ((MoveRequest) lastRequest).getDirection().toString().toLowerCase()
                                    );
                                }
                            }
                            requests.put(session.getReactionTime(), new SimpleEntry<>(session.getPov(), lastRequest));
                        }
                    });
                    requests.forEach((reactionTime, it) ->
                            propelledItemMoveHandler.accept(it.getKey(), (MoveRequest) it.getValue())
                    );
                    sessions.forEach((token, session) -> {
                        try {
                            Perception perception = renderer.render(session.getPov(), 7, 7, 2);
                            String json = objectMapper.writeValueAsString(perception);
                            log.trace("Sending perception to {}: {}", session.getUsername(), json);
                            session.resetLastSentTimestamp();
                            session.wsSessions()
                                    .forEach(ws ->
                                            ws.getRemote().sendString(json, new WriteCallback() {
                                                @Override
                                                public void writeFailed(Throwable x) {
                                                    log.error("Failed to write WebSocket for {}",
                                                            session.getUsername(), x);
                                                }

                                                @Override
                                                public void writeSuccess() {
                                                    long timestamp = session.updateLastSentTimestamp();
                                                    log.debug("Sent perception to {} / {} at {} nanosec",
                                                            session.getUsername(), ws.getId(), timestamp);
                                                }
                                            })
                                    );
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    world.nextTick();
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
                            String token = authService.getToken(ctx);
                            String username = authService.extractUserName(ctx);
                            sessions.computeIfAbsent(token, ignore -> {
                                log.info("Starting new session for user {} with token {}", username, token);
                                return new UserSession(
                                        username,
                                        randomlyPlacedPropelledItem(world, random),
                                        timestampSupplier
                                );
                            });
                            ctx.renderFreemarker("main.html", ImmutableMap.of("token", token));
                        },
                        roles(PLAYER)
                ))
                .ws("/websocket", ws -> {
                            ws.onConnect(session -> {
                                session.getRemote().setBatchMode(BatchMode.OFF);
                                String token = session.queryParam("token");
                                UserSession userSession = sessions.get(token);
                                if (userSession != null) {
                                    log.info("New WebSocket for {}", userSession.getUsername());
                                    userSession.addWsSession(session);
                                } else {
                                    log.warn("Requested new WebSocket for unknown token {}. Disconnecting.", token);
                                    session.disconnect();
                                }
                            });
                            ws.onMessage((session, msg) -> {
                                String token = session.queryParam("token");
                                UserSession userSession = sessions.get(token);
                                MoveRequest moveRequest = objectMapper.readValue(msg, MoveRequest.class);
                                userSession.offerRequest(moveRequest);
                            });
                            ws.onClose((session, statusCode, reason) -> {
                                String token = session.queryParam("token");
                                UserSession userSession = sessions.get(token);
                                log.debug("Closing WebSocket for {}", userSession.getUsername());
                                userSession.removeWsSession(session);
                                if (!keepDisconnectedUser && !userSession.hasWsSessions()) {
                                    log.info("Last WebSocket closed for {}", userSession.getUsername());
                                    world.detach(userSession.getPov());
                                    sessions.remove(token);
                                }
                            });
                        }
                )
                .port(httpPort)
                .start();
    }

    private static PropelledItem randomlyPlacedPropelledItem(World world, Random random) {
        int idx = world.nthFreeCellIndex(abs(random.nextInt()));
        PropelledItem item = newPropelledItem("bant", true)
                .place(world.indexToRow(idx), world.indexToColumn(idx), 100)
                .setLastMoveTick(-1)
                .pointTo(DOWN);
        world.attach(item);
        return item;
    }

}