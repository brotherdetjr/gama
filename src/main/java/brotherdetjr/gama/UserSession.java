package brotherdetjr.gama;

import io.javalin.embeddedserver.jetty.websocket.WsSession;

import java.util.concurrent.atomic.AtomicInteger;

public final class UserSession {
    private final WsSession wsSession;
    private final AtomicInteger frame;

    public UserSession(WsSession wsSession) {
        this.wsSession = wsSession;
        frame = new AtomicInteger(0);
    }

    public WsSession getWsSession() {
        return wsSession;
    }

    public int nextFrame() {
        return frame.getAndIncrement() % 4;
    }
}
