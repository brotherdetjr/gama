import brotherdetjr.gama.parser.WorldParser
import spock.lang.Specification

import static brotherdetjr.gama.Direction.DOWN
import static brotherdetjr.gama.Direction.RIGHT
import static brotherdetjr.gama.DirectionalItem.newDirectionalItem
import static brotherdetjr.gama.Item.newItem
import static brotherdetjr.gama.PropelledItem.newPropelledItem
import static java.lang.Thread.currentThread
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals

class WorldParserTest extends Specification {
    def 'parses one ground layer and one object group'() {
        given:
        def world = WorldParser.parse(
                currentThread().contextClassLoader.getResourceAsStream('WorldParserTest.xml').text,
                [18: 'sand_0', 19: 'sand_1', 20: 'sand_2', 21: 'sand_3'],
                currentThread().contextClassLoader.getResourceAsStream('WorldParserTest.json').text
        )
        expect:
        with(world) {
            verifyAll {
                height == 2
                width == 3
                assertReflectionEquals(
                        [0: newItem('sand_0', false).place(0, 0, 0)],
                        getAt(0, 0)
                )
                assertReflectionEquals(
                        [
                                0: newItem('sand_1', false).place(0, 1, 0),
                                1: newDirectionalItem('arrow', false).place(0, 1, 1).pointTo(DOWN)
                        ],
                        getAt(0, 1)
                )
                assertReflectionEquals(
                        [
                                0: newItem('sand_2', false).place(0, 2, 0),
                                1: newItem('grass_multiple', false).place(0, 2, 1),
                                2: newItem('cactus_tall', true).place(0, 2, 2)
                        ],
                        getAt(0, 2)
                )
                assertReflectionEquals(
                        [
                                0: newItem('sand_3', false).place(1, 0, 0),
                                1: newPropelledItem('girl', true).place(1, 0, 1).pointTo(RIGHT)
                        ],
                        getAt(1, 0)
                )
                assertReflectionEquals(
                        [0: newItem('sand_0', false).place(1, 1, 0)],
                        getAt(1, 1)
                )
                assertReflectionEquals(
                        [0: newItem('sand_1', false).place(1, 2, 0)],
                        getAt(1, 2)
                )
            }
        }
    }

}