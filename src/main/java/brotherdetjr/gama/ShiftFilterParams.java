package brotherdetjr.gama;

public final class ShiftFilterParams {
    public static final String FILTER_NAME = "shift";
    private final String direction;
    private final double distancePx;

    public ShiftFilterParams(String direction, double distancePx) {
        Direction.valueOf(direction.toUpperCase()); // simple validation
        this.direction = direction;
        this.distancePx = distancePx;
    }

    public String getDirection() {
        return direction;
    }

    public double getDistancePx() {
        return distancePx;
    }
}
