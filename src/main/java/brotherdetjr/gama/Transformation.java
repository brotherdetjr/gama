package brotherdetjr.gama;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Transformation<P> {
    private final String name;
    private final P params;

    @JsonCreator
    public Transformation(@JsonProperty("name") String name, @JsonProperty("params") P params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public P getParams() {
        return params;
    }
}
