import brotherdetjr.gama.CellEntry
import brotherdetjr.gama.MoveTransitionParams
import brotherdetjr.gama.Renderer
import brotherdetjr.gama.ShiftFilterParams
import brotherdetjr.gama.Transformation
import brotherdetjr.gama.World
import spock.lang.Specification

import static brotherdetjr.gama.Direction.DOWN
import static brotherdetjr.gama.Direction.UP
import static brotherdetjr.gama.Item.newItem
import static brotherdetjr.gama.PropelledItem.newPropelledItem
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals

class RendererTest extends Specification {
    def 'world scrolls down, when pov character goes up'() {
        given:
        def povItem = newPropelledItem('pov', true).place(11, 11, 100).pointTo(UP).markJustMoved()
        def world = Mock(World) {
            getAt(10, 10) >> [0: newItem('ground', false).place(10, 10, 0)]
            getAt(10, 11) >> [0: newItem('ground', false).place(10, 11, 0), 1: newItem('rock', true).place(10, 11, 1)]
            getAt(10, 12) >> [0: newItem('ground', false).place(10, 12, 0)]
            getAt(11, 10) >> [0: newItem('ground', false).place(11, 10, 0)]
            getAt(11, 11) >> [0: newItem('ground', false).place(11, 11, 0), 100: povItem]
            getAt(11, 12) >> [0: newItem('ground', false).place(11, 12, 0)]
            getAt(12, 10) >> [0: newItem('ground', false).place(12, 10, 0), 100: newPropelledItem('boy', true).place(12, 12, 100).pointTo(DOWN).markJustMoved()]
            getAt(12, 11) >> [0: newItem('ground', false).place(12, 11, 0)]
            getAt(12, 12) >> [0: newItem('ground', false).place(12, 12, 0), 100: newPropelledItem('girl', true).place(12, 12, 100).pointTo(UP).markJustMoved()]
        }
        def renderer = new Renderer(1, 1, 32, 32, 1, world)
        def renderedView = renderer.render(povItem)
        def shift = new Transformation<>('shift', new ShiftFilterParams('up', 32))
        def move = new Transformation<>('move', new MoveTransitionParams('down', 32, 2))
        def ground = new CellEntry('ground', [move], [shift], 0)
        expect:
        assertLenientEquals(
                [
                        [
                                [ground],
                                [ground, new CellEntry('rock', [move], [shift], 1)],
                                [ground]
                        ],
                        [
                                [ground],
                                [ground, new CellEntry('pov_move_up', [], [], 100)],
                                [ground]
                        ],
                        [
                                [ground, new CellEntry('boy_move_down', [move], [new Transformation<>('shift', new ShiftFilterParams('up', 64))], 100)],
                                [ground],
                                [ground, new CellEntry('girl_move_up', [move], [], 100)]
                        ]
                ],
                renderedView
        )
    }
}