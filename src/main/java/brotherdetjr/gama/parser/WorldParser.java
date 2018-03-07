package brotherdetjr.gama.parser;

import brotherdetjr.gama.Item;
import brotherdetjr.gama.World;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;

public final class WorldParser {

    private final static XmlMapper xmlMapper = new XmlMapper();

    private  WorldParser() {
        throw new AssertionError();
    }

    public static World parse(String tmxXml, Map<Integer, String> gidToSprite) {
        try {
            TmxMap tmxMap = xmlMapper.readValue(tmxXml, TmxMap.class);
            World world = new World(tmxMap.getHeight(), tmxMap.getWidth());
            Layer layer = tmxMap.getLayer();
            StringTokenizer tokenizer = new StringTokenizer(layer.getData(), "\n\t ,");
            int r = 0;
            int c = 0;
            while (tokenizer.hasMoreElements()) {
                Item item = new Item(gidToSprite.get(parseInt(tokenizer.nextToken())), false);
                item.place(r, c++, 0);
                if (c >= layer.getWidth()) {
                    c = 0;
                    r++;
                }
                world.attach(item);
            }
            return world;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
