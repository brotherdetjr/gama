package brotherdetjr.gama;

import io.javalin.embeddedserver.jetty.websocket.WsSession;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Sets.newConcurrentHashSet;

public final class UserSession {
    private final String username;
    private final Set<WsSession> wsSessions;
    private final AtomicInteger frame;

    public UserSession(String username) {
        this.username = username;
        wsSessions = newConcurrentHashSet();
        frame = new AtomicInteger(0);
    }

    public String getUsername() {
        return username;
    }

    public Set<WsSession> getWsSessions() {
        return wsSessions;
    }

    public void addWsSession(WsSession wsSession) {
        wsSessions.add(wsSession);
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
