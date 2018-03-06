package brotherdetjr.gama;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;

public class CompositionValue {
    private final List<String> frames;
    private final int zIndex;
    private final List<? extends Transformation> transitions;
    private final List<? extends Transformation> filters;

    @JsonCreator
    public CompositionValue(
            @JsonProperty("frames") List<String> frames,
            @JsonProperty("zIndex") int zIndex,
            @JsonProperty("transitions") List<? extends Transformation> transitions,
            @JsonProperty("filters") List<? extends Transformation> filters
    ) {
        this.frames = copyOf(frames);
        this.zIndex = zIndex;
        this.transitions = copyOf(transitions);
        this.filters = copyOf(filters);
    }

    public List<String> getFrames() {
        return frames;
    }

    public int getzIndex() {
        return zIndex;
    }

    public List<? extends Transformation> getTransitions() {
        return transitions;
    }

    public List<? extends Transformation> getFilters() {
        return filters;
    }
}
