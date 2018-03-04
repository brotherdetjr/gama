package brotherdetjr.gama;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class MoveTransitionParams {
    public static final String TRANSITION_NAME = "move";

    private final String direction;
    private final int distancePx;

    @JsonCreator
    public MoveTransitionParams(
            @JsonProperty("direction") String direction,
            @JsonProperty("distancePx") int distancePx) {
        Direction.valueOf(direction.toUpperCase()); // simple validation
        this.direction = direction;
        this.distancePx = distancePx;
    }

    public String getDirection() {
        return direction;
    }

    public int getDistancePx() {
        return distancePx;
    }
}
