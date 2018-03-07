import brotherdetjr.gama.Item
import brotherdetjr.gama.parser.WorldParser
import spock.lang.Specification

import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals

class WorldParserTest extends Specification {
    def 'parses ground layer correctly'() {
        given:
        def world = WorldParser.parse(
                """<?xml version="1.0" encoding="UTF-8"?>
<map version="1.0" tiledversion="1.1.2" orientation="orthogonal" renderorder="right-up" width="3" height="2" tilewidth="32" tileheight="32" infinite="0" nextobjectid="140">
 <tileset firstgid="1" source="desert.tsx"/>
 <layer name="ground" width="3" height="2" locked="1">
  <data encoding="csv">
18,19,20,
21,18,19
  </data>
 </layer>  
</map>
""",
                [18: 'sand_0', 19: 'sand_1', 20: 'sand_2', 21: 'sand_3']
        )
        expect:
        with(world) {
            verifyAll {
                height == 2
                width == 3
                def item00 = new Item('sand_0', false).place(0, 0, 0)
                assertReflectionEquals([0: item00], getAt(0, 0))
                def item01 = new Item('sand_1', false).place(0, 1, 0)
                assertReflectionEquals([0: item01], getAt(0, 1))
                def item02 = new Item('sand_2', false).place(0, 2, 0)
                assertReflectionEquals([0: item02], getAt(0, 2))
                def item10 = new Item('sand_3', false).place(1, 0, 0)
                assertReflectionEquals([0: item10], getAt(1, 0))
                def item11 = new Item('sand_0', false).place(1, 1, 0)
                assertReflectionEquals([0: item11], getAt(1, 1))
                def item12 = new Item('sand_1', false).place(1, 2, 0)
                assertReflectionEquals([0: item12], getAt(1, 2))
            }
        }
    }
}