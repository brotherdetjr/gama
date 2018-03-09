package brotherdetjr.gama;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class CellEntry {
    private final String sprite;
    private final List<Transformation<?>> transitions;
    private final List<Transformation<?>> filters;
    private final int zIndex;

    @JsonCreator
    public CellEntry(@JsonProperty("sprite") String sprite,
                     @JsonProperty("transitions") List<Transformation<?>> transitions,
                     @JsonProperty("filters") List<Transformation<?>> filters,
                     @JsonProperty("zIndex") int zIndex) {
        this.sprite = sprite;
        this.transitions = transitions;
        this.filters = filters;
        this.zIndex = zIndex;
    }

    public String getSprite() {
        return sprite;
    }

    public List<Transformation<?>> getTransitions() {
        return transitions;
    }

    public List<Transformation<?>> getFilters() {
        return filters;
    }

    public int getzIndex() {
        return zIndex;
    }
}
