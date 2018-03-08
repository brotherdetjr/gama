package brotherdetjr.gama.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.collect.ImmutableList.copyOf;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class TmxObject {
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

    @SuppressWarnings("unchecked")
    public <T> T prop(String name) {
        return (T) properties.stream()
                .filter(p -> name.equals(p.getName()))
                .map(TmxProperty::typedValue)
                .findFirst()
                .orElse(null);
    }

    public <T> T prop(String name, T defaultValue) {
        return Optional.<T>ofNullable(prop(name)).orElse(defaultValue);
    }

    public <T> T prop(String name, Supplier<T> defaultSupplier) {
        return Optional.<T>ofNullable(prop(name)).orElseGet(defaultSupplier);
    }
}
