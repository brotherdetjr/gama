import brotherdetjr.gama.Item
import brotherdetjr.gama.parser.WorldParser
import spock.lang.Specification

import static java.lang.Thread.currentThread
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals

class WorldParserTest extends Specification {
    def 'parses ground layer correctly'() {
        given:
        def world = WorldParser.parse(
                currentThread().contextClassLoader.getResourceAsStream('WorldParserTest1.xml').text,
                [18: 'sand_0', 19: 'sand_1', 20: 'sand_2', 21: 'sand_3'],
                null
        )
        expect:
        with(world) {
            verifyAll {
                height == 2
                width == 3
                assertReflectionEquals(
                        [0: new Item('sand_0', false).place(0, 0, 0)],
                        getAt(0, 0)
                )
                assertReflectionEquals(
                        [0: new Item('sand_1', false).place(0, 1, 0)],
                        getAt(0, 1)
                )
                assertReflectionEquals(
                        [0: new Item('sand_2', false).place(0, 2, 0)],
                        getAt(0, 2)
                )
                assertReflectionEquals(
                        [0: new Item('sand_3', false).place(1, 0, 0)],
                        getAt(1, 0)
                )
                assertReflectionEquals(
                        [0: new Item('sand_0', false).place(1, 1, 0)],
                        getAt(1, 1)
                )
                assertReflectionEquals(
                        [0: new Item('sand_1', false).place(1, 2, 0)],
                        getAt(1, 2)
                )
            }
        }
    }

    def 'parses object layer correctly'() {
        given:
        def world = WorldParser.parse(
                currentThread().contextClassLoader.getResourceAsStream('WorldParserTest2.xml').text,
                null,
                currentThread().contextClassLoader.getResourceAsStream('WorldParserTest2.json').text
        )
        expect:
        with(world) {
            verifyAll {

            }
        }
    }
}