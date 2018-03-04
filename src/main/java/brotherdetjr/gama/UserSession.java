package brotherdetjr.gama;

import io.javalin.embeddedserver.jetty.websocket.WsSession;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static com.google.common.collect.Maps.newConcurrentMap;

public final class UserSession {
    private final String username;
    private final Map<WsSession, AtomicLong> wsSessions;
    private final AtomicInteger frame;

    public UserSession(String username) {
        this.username = username;
        wsSessions = newConcurrentMap();
        frame = new AtomicInteger(0);
    }

    public String getUsername() {
        return username;
    }

    public Stream<Map.Entry<WsSession, AtomicLong>> timestampedWsSessions() {
        return wsSessions.entrySet().stream();
    }

    public void addWsSession(WsSession wsSession) {
        wsSessions.put(wsSession, new AtomicLong(0));
    }

    public void removeWsSession(WsSession wsSession) {
        wsSessions.remove(wsSession);
    }

    public boolean hasWsSessions() {
        return !wsSessions.isEmpty();
    }

    public int nextFrame() {
        return frame.getAndIncrement() % 4;
    }
}
