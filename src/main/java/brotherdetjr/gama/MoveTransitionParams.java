package brotherdetjr.gama;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class MoveTransitionParams {
    public static final String TRANSITION_NAME = "move";

    private final String direction;
    private final double distancePx;
    private final double stepPx;

    @JsonCreator
    public MoveTransitionParams(
            @JsonProperty("direction") String direction,
            @JsonProperty("distancePx") double distancePx,
            @JsonProperty("stepPx") double stepPx) {
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
