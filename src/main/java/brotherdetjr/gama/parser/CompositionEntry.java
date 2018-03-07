package brotherdetjr.gama.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompositionEntry {
    private final int zIndex;

    @JsonCreator
    public CompositionEntry(@JsonProperty("zIndex") int zIndex) {
        this.zIndex = zIndex;
    }

    public int getzIndex() {
        return zIndex;
    }
}
