import brotherdetjr.gama.WorldParser
import spock.lang.Specification

class WorldParserTest extends Specification {
    def 'does my day'() {
        given:
        def world = WorldParser.parse("""<?xml version="1.0" encoding="UTF-8"?>
<map version="1.0" tiledversion="1.1.2" orientation="orthogonal" renderorder="right-up" width="128" height="64" tilewidth="32" tileheight="32" infinite="0" nextobjectid="140">
 <tileset firstgid="1" source="desert.tsx"/>
</map>
""", null, null)
        expect:
        with(world) {
            verifyAll {
                height == 64
                width == 128
            }
        }
    }
}