package brotherdetjr.gama.parser;

import brotherdetjr.gama.Direction;
import brotherdetjr.gama.DirectionalItem;
import brotherdetjr.gama.Item;
import brotherdetjr.gama.PropelledItem;
import brotherdetjr.gama.World;
import brotherdetjr.gama.WorldImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static brotherdetjr.gama.DirectionalItem.newDirectionalItem;
import static brotherdetjr.gama.Item.newItem;
import static brotherdetjr.gama.PropelledItem.newPropelledItem;
import static java.lang.Integer.parseInt;

public final class WorldParser {

    private final static XmlMapper xmlMapper = new XmlMapper();
    private final static ObjectMapper jsonMapper = new ObjectMapper();

    private WorldParser() {
        throw new AssertionError();
    }

    public static World parse(String tmxXml, Map<Integer, String> gidToSprite, String compositionJson, boolean torus) {
        try {
            TmxMap tmxMap = xmlMapper.readValue(tmxXml, TmxMap.class);
            WorldImpl world = new WorldImpl(tmxMap.getHeight(), tmxMap.getWidth(), torus);
            Layer layer = tmxMap.getLayer();
            if (layer != null && layer.getData() != null) {
                parseGround(layer, world, gidToSprite);
            }
            List<TmxObject> objectgroup = tmxMap.getObjectgroup();
            Map<String, CompositionEntry> composition = parseComposition(compositionJson);
            if (objectgroup != null) {
                objectgroup.forEach(obj ->
                        world.attach(parseItem(obj, composition, tmxMap.getTileheight(), tmxMap.getTilewidth()))
                );
            }
            return world;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Item parseItem(TmxObject obj,
                                  Map<String, CompositionEntry> composition,
                                  int tileHeight,
                                  int tileWidth) {
        String type = obj.getType();
        String sprite = obj.prop("sprite");
        boolean obstacle = obj.prop("obstacle", false);
        Item item;
        switch (type) {
            case "item":
                item = newItem(sprite, obstacle);
                break;
            case "directionalItem":
                item = newDirectionalItem(sprite, obstacle);
                break;
            case "propelledItem":
                item = newPropelledItem(sprite, obstacle);
                break;
            default:
                throw new IllegalArgumentException("Unknown object type: " + type);
        }
        String key;
        if (item instanceof DirectionalItem) {
            DirectionalItem directionalItem = (DirectionalItem) item;
            directionalItem.pointTo(Direction.parse(obj.prop("direction", "down")));
            key = sprite + (item instanceof PropelledItem ? "_idle" : "") + "_" +
                    directionalItem.getDirection().name().toLowerCase();
        } else {
            key = sprite;
        }
        int row = obj.getY() / tileHeight - 1;
        int column = obj.getX() / tileWidth;
        int zIndex = obj.prop("zIndex", () -> composition.get(key).getzIndex());
        item.place(row, column, zIndex);
        return item;
    }

    private static Map<String, CompositionEntry> parseComposition(String compositionJson) throws IOException {
        TypeReference<Map<String, CompositionEntry>> typeRef =
                new TypeReference<Map<String, CompositionEntry>>() {
                    // nothing
                };
        return jsonMapper.readValue(compositionJson, typeRef);
    }

    private static void parseGround(Layer layer, World world, Map<Integer, String> gidToSprite) {
        StringTokenizer tokenizer = new StringTokenizer(layer.getData(), "\n\t ,");
        int r = 0;
        int c = 0;
        while (tokenizer.hasMoreElements()) {
            String sprite = gidToSprite.get(parseInt(tokenizer.nextToken()));
            world.attach(newItem(sprite, false).place(r, c++, 0));
            if (c >= layer.getWidth()) {
                c = 0;
                r++;
            }
        }
    }
}
