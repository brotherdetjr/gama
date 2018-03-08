package brotherdetjr.gama.parser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class TmxMap {
    private final int height;
    private final int width;
    private final int tileheight;
    private final int tilewidth;
    private final Layer layer;
    private final List<TmxObject> objectgroup;

    @JsonCreator
    public TmxMap(@JsonProperty("height") int height,
                  @JsonProperty("width") int width,
                  @JsonProperty("tileheight") int tileheight,
                  @JsonProperty("tilewidth") int tilewidth,
                  @JsonProperty("layer") Layer layer,
                  @JsonProperty("objectgroup") List<TmxObject> objectgroup) {
        this.height = height;
        this.width = width;
        this.tileheight = tileheight;
        this.tilewidth = tilewidth;
        this.layer = layer;
        this.objectgroup = objectgroup != null ? copyOf(objectgroup) : null;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getTileheight() {
        return tileheight;
    }

    public int getTilewidth() {
        return tilewidth;
    }

    public Layer getLayer() {
        return layer;
    }

    public List<TmxObject> getObjectgroup() {
        return objectgroup;
    }
}
