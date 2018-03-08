package brotherdetjr.gama.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class TmxProperty {
    private final String name;
    private final String value;
    private final String type;

    @JsonCreator
    public TmxProperty(@JsonProperty("name") String name,
                       @JsonProperty("value") String value,
                       @JsonProperty("type") String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public Object typedValue() {
        if ("int".equals(type)) {
            return parseInt(value);
        } else if ("bool".equals(type)) {
            return parseBoolean(value);
        } else {
            return value;
        }
    }
}
