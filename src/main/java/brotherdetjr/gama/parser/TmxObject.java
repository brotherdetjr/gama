package brotherdetjr.gama.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmxObject {
    private final String type;
    private final int y;
    private final int x;
    private final List<TmxProperty> properties;

    @JsonCreator
    public TmxObject(@JsonProperty("type") String type,
                     @JsonProperty("y") int y,
                     @JsonProperty("x") int x,
                     @JsonProperty("properties") List<TmxProperty> properties) {
        this.type = type;
        this.y = y;
        this.x = x;
        this.properties = properties != null ? copyOf(properties) : null;
    }

    public String getType() {
        return type;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public List<TmxProperty> getProperties() {
        return properties;
    }
}
