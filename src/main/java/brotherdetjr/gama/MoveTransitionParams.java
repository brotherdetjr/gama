package brotherdetjr.gama;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class MoveTransitionParams {
    public static final String TRANSITION_NAME = "move";

    private final String direction;
    private final int distancePx;
    private final int stepPx;

    @JsonCreator
    public MoveTransitionParams(
            @JsonProperty("direction") String direction,
            @JsonProperty("distancePx") int distancePx,
            @JsonProperty("stepPx") int stepPx) {
        Direction.parse(direction); // simple validation
        this.direction = direction;
        this.distancePx = distancePx;
        this.stepPx = stepPx;
    }

    public String getDirection() {
        return direction;
    }

    public int getDistancePx() {
        return distancePx;
    }

    public int getStepPx() {
        return stepPx;
    }
}
