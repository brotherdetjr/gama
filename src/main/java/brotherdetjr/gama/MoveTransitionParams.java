package brotherdetjr.gama;

public final class MoveTransitionParams {
    public static final String TRANSITION_NAME = "move";

    private final String direction;
    private final double distancePx;
    private final double stepPx;

    public MoveTransitionParams(String direction, double distancePx, double stepPx) {
        Direction.parse(direction); // simple validation
        this.direction = direction;
        this.distancePx = distancePx;
        this.stepPx = stepPx;
    }

    public String getDirection() {
        return direction;
    }

    public double getDistancePx() {
        return distancePx;
    }

    public double getStepPx() {
        return stepPx;
    }
}
