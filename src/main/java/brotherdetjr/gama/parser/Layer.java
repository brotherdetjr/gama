package brotherdetjr.gama.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Layer {
    private String data;
    private int height;
    private int width;

    @JsonCreator
    public Layer(@JsonProperty("data") String data,
                 @JsonProperty("height") int height,
                 @JsonProperty("width") int width) {
        this.data = data;
        this.height = height;
        this.width = width;
    }

    public String getData() {
        return data;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
