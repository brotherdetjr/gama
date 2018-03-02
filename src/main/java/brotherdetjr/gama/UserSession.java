package brotherdetjr.gama;

import io.javalin.embeddedserver.jetty.websocket.WsSession;

import java.util.concurrent.atomic.AtomicInteger;

public final class UserSession {
    private final String username;
    private volatile WsSession wsSession;
    private final AtomicInteger frame;

    public UserSession(String username) {
        this.username = username;
        frame = new AtomicInteger(0);
    }

    public String getUsername() {
        return username;
    }

    public WsSession getWsSession() {
        return wsSession;
    }

    public void setWsSession(WsSession wsSession) {
        this.wsSession = wsSession;
    }

    public int nextFrame() {
        return frame.getAndIncrement() % 4;
    }
}
