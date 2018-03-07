package brotherdetjr.gama.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmxMap {
    private final int height;
    private final int width;
    private final Layer layer;

    @JsonCreator
    public TmxMap(@JsonProperty("height") int height,
                  @JsonProperty("width") int width,
                  @JsonProperty("layer") Layer layer) {
        this.height = height;
        this.width = width;
        this.layer = layer;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Layer getLayer() {
        return layer;
    }
}
