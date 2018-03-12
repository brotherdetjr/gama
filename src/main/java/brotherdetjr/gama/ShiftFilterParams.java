package brotherdetjr.gama;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ShiftFilterParams {
    public static final String FILTER_NAME = "shift";
    private final String direction;
    private final double distancePx;

    @JsonCreator
    public ShiftFilterParams(@JsonProperty("direction") String direction,
                             @JsonProperty("distancePx") double distancePx) {
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
