package brotherdetjr.gama;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class MoveRequest {
    private final Direction direction;

    @JsonCreator
    public MoveRequest(@JsonProperty("direction") Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
