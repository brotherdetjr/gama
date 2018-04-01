package brotherdetjr.gama;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public final class MoveRequest {
    private final Direction direction;

    @JsonCreator
    public MoveRequest(@JsonProperty("direction") Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("direction", direction)
                .toString();
    }
}
