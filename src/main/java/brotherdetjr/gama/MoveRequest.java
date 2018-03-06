package brotherdetjr.gama;

public final class MoveRequest {
    private final Direction direction;

    public MoveRequest(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
