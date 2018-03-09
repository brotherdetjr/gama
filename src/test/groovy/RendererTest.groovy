import brotherdetjr.gama.CellEntry
import brotherdetjr.gama.Direction
import brotherdetjr.gama.MoveTransitionParams
import brotherdetjr.gama.Renderer
import brotherdetjr.gama.Transformation
import brotherdetjr.gama.World
import spock.lang.Specification

import static brotherdetjr.gama.Item.newItem
import static brotherdetjr.gama.PropelledItem.newPropelledItem
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals

class RendererTest extends Specification {
    def 'world scrolls down, when pov character goes up'() {
        given:
        def povItem = newPropelledItem('pov', true).place(11, 11, 100).pointTo(Direction.UP).markJustMoved()
        def world = Mock(World) {
            getAt(10, 10) >> [0: newItem('ground', false).place(10, 10, 0)]
            getAt(10, 11) >> [0: newItem('ground', false).place(10, 11, 0), 1: newItem('rock', true).place(10, 11, 1)]
            getAt(10, 12) >> [0: newItem('ground', false).place(10, 12, 0)]
            getAt(11, 10) >> [0: newItem('ground', false).place(11, 10, 0)]
            getAt(11, 11) >> [0: newItem('ground', false).place(11, 11, 0), 100: povItem]
            getAt(11, 12) >> [0: newItem('ground', false).place(11, 12, 0)]
            getAt(12, 10) >> [0: newItem('ground', false).place(12, 10, 0)]
            getAt(12, 11) >> [0: newItem('ground', false).place(12, 11, 0)]
            getAt(12, 12) >> [0: newItem('ground', false).place(12, 12, 0)]
        }
        def renderer = new Renderer(1, 1, 32, 32, 1, world)
        expect:
        assertLenientEquals(
                [
                        [
                                [new CellEntry('ground', [new Transformation<>('move', new MoveTransitionParams('down', 32, 2))], [], 0)],
                                [
                                        new CellEntry('ground', [new Transformation<>('move', new MoveTransitionParams('down', 32, 2))], [], 0),
                                        new CellEntry('rock', [new Transformation<>('move', new MoveTransitionParams('down', 32, 2))], [], 1)
                                ],
                                [new CellEntry('ground', [new Transformation<>('move', new MoveTransitionParams('down', 32, 2))], [], 0)]
                        ],
                        [
                                [new CellEntry('ground', [new Transformation<>('move', new MoveTransitionParams('down', 32, 2))], [], 0)],
                                [
                                        new CellEntry('ground', [new Transformation<>('move', new MoveTransitionParams('down', 32, 2))], [], 0),
                                        new CellEntry('pov_move_up', [], [], 100)
                                ],
                                [new CellEntry('ground', [new Transformation<>('move', new MoveTransitionParams('down', 32, 2))], [], 0)]
                        ],
                        [
                                [new CellEntry('ground', [new Transformation<>('move', new MoveTransitionParams('down', 32, 2))], [], 0)],
                                [new CellEntry('ground', [new Transformation<>('move', new MoveTransitionParams('down', 32, 2))], [], 0)],
                                [new CellEntry('ground', [new Transformation<>('move', new MoveTransitionParams('down', 32, 2))], [], 0)]
                        ]
                ],
                renderer.render(povItem)
        )
    }
}