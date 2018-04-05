package brotherdetjr.gama;

import io.javalin.embeddedserver.jetty.websocket.WsSession;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import java.util.stream.Stream;

import static java.util.concurrent.ConcurrentHashMap.newKeySet;

public final class UserSession {

    private final String username;
    private final PropelledItem pov;
    private final LongSupplier timestampSupplier;
    private final Set<WsSession> wsSessions;
    private final AtomicLong lastSentTimestamp;
    private volatile Object request;
    private volatile long lastRequestTimestamp;

    public UserSession(String username, PropelledItem pov, LongSupplier timestampSupplier) {
        this.username = username;
        this.pov = pov;
        this.timestampSupplier = timestampSupplier;
        wsSessions = newKeySet();
        lastSentTimestamp = new AtomicLong();
    }

    public String getUsername() {
        return username;
    }

    public Stream<WsSession> wsSessions() {
        return wsSessions.stream();
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

    public void resetLastSentTimestamp() {
        lastSentTimestamp.set(0);
    }

    public long updateLastSentTimestamp() {
        long timestamp = timestampSupplier.getAsLong();
        lastSentTimestamp.compareAndSet(0, timestamp);
        return timestamp;
    }

    public PropelledItem getPov() {
        return pov;
    }

    public <T> T takeRequest() {
        @SuppressWarnings("unchecked") T result = (T) request;
        request = null;
        return result;
    }

    public <T> void offerRequest(T lastRequest) {
        if (this.request == null) {
            lastRequestTimestamp = timestampSupplier.getAsLong();
            this.request = lastRequest;
        }
    }

    public long getReactionTime() {
        return lastRequestTimestamp - lastSentTimestamp.get();
    }
}
