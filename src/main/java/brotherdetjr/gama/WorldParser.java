package brotherdetjr.gama;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

public final class WorldParser {

    private final static XmlMapper xmlMapper = new XmlMapper();

    private  WorldParser() {
        throw new AssertionError();
    }

    public static World parse(String tmxXml, String tsxXml, String compositionJson) {
        try {
            Tmx tmx = xmlMapper.readValue(tmxXml, Tmx.class);
            return new World(tmx.getHeight(), tmx.getWidth());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
