package brotherdetjr.gama;

import io.javalin.embeddedserver.jetty.websocket.WsSession;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static com.google.common.collect.Maps.newConcurrentMap;

public final class UserSession {
    private final String username;
    private final Map<WsSession, AtomicLong> wsSessions;
    private final PropelledItem pov;
    private volatile Object lastRequest;

    public UserSession(String username, PropelledItem pov) {
        this.username = username;
        this.pov = pov;
        wsSessions = newConcurrentMap();
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

    public boolean removeWsSession(WsSession wsSession) {
        wsSessions.remove(wsSession);
        return wsSessions.isEmpty();
    }

    public boolean hasWsSessions() {
        return !wsSessions.isEmpty();
    }

    public PropelledItem getPov() {
        return pov;
    }

    public <T> T takeLastRequest() {
        @SuppressWarnings("unchecked") T result = (T) lastRequest;
        lastRequest = null;
        return result;
    }

    public <T> void offerLastRequest(T lastRequest) {
        if (lastRequest == null) {
            throw new IllegalArgumentException();
        }
        if (this.lastRequest == null) {
            this.lastRequest = lastRequest;
        }
    }
}
