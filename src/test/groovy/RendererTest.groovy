import brotherdetjr.gama.CellEntry
import brotherdetjr.gama.MoveTransitionParams
import brotherdetjr.gama.Perception
import brotherdetjr.gama.Renderer
import brotherdetjr.gama.ShiftFilterParams
import brotherdetjr.gama.Transformation
import brotherdetjr.gama.World
import spock.lang.Specification

import static brotherdetjr.gama.Direction.DOWN
import static brotherdetjr.gama.Direction.RIGHT
import static brotherdetjr.gama.Direction.UP
import static brotherdetjr.gama.Item.newItem
import static brotherdetjr.gama.PropelledItem.newPropelledItem
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals

class RendererTest extends Specification {
    def 'world scrolls down, when pov character goes up'() {
        given:
        def povItem = newPropelledItem('pov', true).place(11, 11, 100).pointTo(UP).setLastMoveTick(1L)
        def world = Mock(World) {
            getAt(9, 9) >> [0: newItem('ground', false).place(9, 9, 0)]
            getAt(9, 10) >> [0: newItem('ground', false).place(9, 10, 0)]
            getAt(9, 11) >> [0: newItem('ground', false).place(9, 11, 0)]
            getAt(9, 12) >> [0: newItem('ground', false).place(9, 12, 0)]
            getAt(9, 13) >> [0: newItem('ground', false).place(9, 13, 0)]
            getAt(10, 9) >> [0: newItem('ground', false).place(10, 9, 0)]
            getAt(10, 10) >> [0: newItem('ground', false).place(10, 10, 0)]
            getAt(10, 11) >> [0: newItem('ground', false).place(10, 11, 0), 1: newItem('rock', true).place(10, 11, 1)]
            getAt(10, 12) >> [0: newItem('ground', false).place(10, 12, 0)]
            getAt(10, 13) >> [0: newItem('ground', false).place(10, 13, 0)]
            getAt(11, 9) >> [0: newItem('ground', false).place(11, 9, 0)]
            getAt(11, 10) >> [0: newItem('ground', false).place(11, 10, 0)]
            getAt(11, 11) >> [0: newItem('ground', false).place(11, 11, 0), 100: povItem]
            getAt(11, 12) >> [0: newItem('ground', false).place(11, 12, 0)]
            getAt(11, 13) >> [0: newItem('ground', false).place(11, 13, 0)]
            getAt(12, 9) >> [0: newItem('ground', false).place(12, 9, 0)]
            getAt(12, 10) >> [0: newItem('ground', false).place(12, 10, 0), 100: newPropelledItem('boy', true).place(12, 10, 100).pointTo(DOWN).setLastMoveTick(1L)]
            getAt(12, 11) >> [0: newItem('ground', false).place(12, 11, 0)]
            getAt(12, 12) >> [0: newItem('ground', false).place(12, 12, 0), 100: newPropelledItem('girl', true).place(12, 12, 100).pointTo(UP).setLastMoveTick(1L)]
            getAt(12, 13) >> [0: newItem('ground', false).place(12, 13, 0)]
            getAt(13, 9) >> [0: newItem('ground', false).place(13, 9, 0)]
            getAt(13, 10) >> [0: newItem('ground', false).place(13, 10, 0)]
            getAt(13, 11) >> [0: newItem('ground', false).place(13, 11, 0)]
            getAt(13, 12) >> [0: newItem('ground', false).place(13, 12, 0)]
            getAt(13, 13) >> [0: newItem('ground', false).place(13, 13, 0)]
            getTick() >> 2L
        }
        def renderer = new Renderer(32, 32, 2, 2, world)
        def renderedPerception = renderer.render(povItem, 3, 3, 1)
        def shift = new Transformation<>('shift', new ShiftFilterParams('up', 32))
        def move = new Transformation<>('move', new MoveTransitionParams('down', 32, 2))
        expect:
        assertLenientEquals(
                new Perception(
                        3,
                        3,
                        [
                                new CellEntry(-1, 0, 'ground', [move], [shift], 0),
                                new CellEntry(-1, 1, 'ground', [move], [shift], 0),
                                new CellEntry(-1, 2, 'ground', [move], [shift], 0),

                                new CellEntry(0, 0, 'ground', [move], [shift], 0),

                                new CellEntry(0, 1, 'ground', [move], [shift], 0),
                                new CellEntry(0, 1, 'rock', [move], [shift], 1),

                                new CellEntry(0, 2, 'ground', [move], [shift], 0),

                                new CellEntry(1, 0, 'ground', [move], [shift], 0),

                                new CellEntry(1, 1, 'ground', [move], [shift], 0),
                                new CellEntry(1, 1, 'pov_move_up', [], [], 100),

                                new CellEntry(1, 2, 'ground', [move], [shift], 0),

                                new CellEntry(2, 0, 'ground', [move], [shift], 0),
                                new CellEntry(2, 0, 'boy_move_down', [new Transformation<>('move', new MoveTransitionParams('down', 64, 4))], [new Transformation<>('shift', new ShiftFilterParams('up', 64))], 100),

                                new CellEntry(2, 1, 'ground', [move], [shift], 0),

                                new CellEntry(2, 2, 'ground', [move], [shift], 0),
                                new CellEntry(2, 2, 'girl_move_up', [], [], 100)
                        ]
                ),
                renderedPerception
        )
    }

    def 'no scroll, when pov character not moving, invisible items are not rendered'() {
        given:
        def povItem = newPropelledItem('pov', true).place(11, 11, 100).pointTo(UP) /* item has been still for a while */
        def world = Mock(World) {
            getAt(10, 10) >> [0: newItem('ground', false).place(10, 10, 0)]
            getAt(10, 11) >> [0: newItem('ground', false).place(10, 11, 0), 1: newItem('rock', true).place(10, 11, 1)]
            getAt(10, 12) >> [0: newItem('ground', false).place(10, 12, 0)]
            getAt(11, 10) >> [0: newItem('ground', false).place(11, 10, 0)]
            getAt(11, 11) >> [0: newItem('ground', false).place(11, 11, 0), 100: povItem]
            getAt(11, 12) >> [0: newItem('ground', false).place(11, 12, 0)]
            getAt(12, 10) >> [0: newItem('ground', false).place(12, 10, 0), 100: newPropelledItem('boy', true).place(12, 10, 100).pointTo(DOWN).setLastMoveTick(1L)]
            getAt(12, 11) >> [0: newItem('ground', false).place(12, 11, 0)]
            getAt(12, 12) >> [0: newItem('ground', false).place(12, 12, 0), 100: newPropelledItem('girl', true).place(12, 12, 100).pointTo(UP).setLastMoveTick(1L)]
            getTick() >> 2L
        }
        def renderer = new Renderer(32, 32, 2, 2, world)
        def renderedPerception = renderer.render(povItem, 1, 1, 1)
        expect:
        assertLenientEquals(
                new Perception(
                        1,
                        1,
                        [
                                new CellEntry(0, 0, 'ground', [], [], 0),
                                new CellEntry(0, 0, 'pov_idle_up', [], [], 100)
                        ]
                ),
                renderedPerception
        )
    }

    def 'corner case, PoV item goes down'() {
        given:
        def povItem = newPropelledItem('pov', true).place(11, 11, 100).pointTo(DOWN).setLastMoveTick(1L)
        def world = Mock(World) {
            getAt(10, 10) >> [0: newItem('ground', false).place(10, 10, 0)]
            getAt(10, 11) >> [0: newItem('ground', false).place(10, 11, 0), 1: newItem('rock', true).place(10, 11, 1)]
            getAt(10, 12) >> [0: newItem('ground', false).place(10, 12, 0)]
            getAt(11, 10) >> [0: newItem('ground', false).place(11, 10, 0), 100: newPropelledItem('cat', true).place(11, 10, 100).pointTo(RIGHT).setLastMoveTick(1L)]
            getAt(11, 11) >> [0: newItem('ground', false).place(11, 11, 0), 100: povItem]
            getAt(11, 12) >> [0: newItem('ground', false).place(11, 12, 0)]
            getAt(12, 10) >> [0: newItem('ground', false).place(12, 10, 0), 100: newPropelledItem('boy', true).place(12, 10, 100).pointTo(DOWN).setLastMoveTick(1L)]
            getAt(12, 11) >> [0: newItem('ground', false).place(12, 11, 0)]
            getAt(12, 12) >> [0: newItem('ground', false).place(12, 12, 0), 100: newPropelledItem('girl', true).place(12, 12, 100).pointTo(UP).setLastMoveTick(1L)]
            getTick() >> 2L
        }
        def renderer = new Renderer(32, 32, 2, 2, world)
        def renderedPerception = renderer.render(povItem, 1, 1, 1)
        def shiftDown = new Transformation<>('shift', new ShiftFilterParams('up', -32))
        def shiftLeft = new Transformation<>('shift', new ShiftFilterParams('left', 32))
        def moveUp = new Transformation<>('move', new MoveTransitionParams('down', -32, -2))
        def moveRight = new Transformation<>('move', new MoveTransitionParams('right', 32, 2))
        expect:
        assertLenientEquals(
                new Perception(
                        1,
                        1,
                        [
                                new CellEntry(0, -1, 'cat_move_right', [moveUp, moveRight], [shiftDown, shiftLeft], 0),
                                new CellEntry(0, 0, 'ground', [moveUp], [shiftDown], 0),
                                new CellEntry(0, 0, 'pov_move_down', [], [], 100),
                                new CellEntry(1, 0, 'ground', [moveUp], [shiftDown], 0)
                        ]
                ),
                renderedPerception
        )
    }

    def 'corner case, PoV item goes down, step is too big'() {
        given:
        def povItem = newPropelledItem('pov', true).place(11, 11, 100).pointTo(DOWN).setLastMoveTick(1L)
        def world = Mock(World) {
            getAt(10, 10) >> [0: newItem('ground', false).place(10, 10, 0)]
            getAt(10, 11) >> [0: newItem('ground', false).place(10, 11, 0), 1: newItem('rock', true).place(10, 11, 1)]
            getAt(10, 12) >> [0: newItem('ground', false).place(10, 12, 0)]
            getAt(11, 10) >> [0: newItem('ground', false).place(11, 10, 0), 100: newPropelledItem('cat', true).place(11, 10, 100).pointTo(RIGHT).setLastMoveTick(1L)]
            getAt(11, 11) >> [0: newItem('ground', false).place(11, 11, 0), 100: povItem]
            getAt(11, 12) >> [0: newItem('ground', false).place(11, 12, 0)]
            getAt(12, 10) >> [0: newItem('ground', false).place(12, 10, 0), 100: newPropelledItem('boy', true).place(12, 10, 100).pointTo(DOWN).setLastMoveTick(1L)]
            getAt(12, 11) >> [0: newItem('ground', false).place(12, 11, 0)]
            getAt(12, 12) >> [0: newItem('ground', false).place(12, 12, 0), 100: newPropelledItem('girl', true).place(12, 12, 100).pointTo(UP).setLastMoveTick(1L)]
            getTick() >> 2L
        }
        def renderer = new Renderer(32, 32, 32, 32, world)
        def renderedPerception = renderer.render(povItem, 1, 1, 1)
        def shiftDown = new Transformation<>('shift', new ShiftFilterParams('up', -32))
        def moveUp = new Transformation<>('move', new MoveTransitionParams('down', -32, -32))
        expect:
        assertLenientEquals(
                new Perception(
                        1,
                        1,
                        [
                                new CellEntry(0, 0, 'ground', [moveUp], [shiftDown], 0),
                                new CellEntry(0, 0, 'pov_move_down', [], [], 100),
                                new CellEntry(1, 0, 'ground', [moveUp], [shiftDown], 0)
                        ]
                ),
                renderedPerception
        )
    }
}