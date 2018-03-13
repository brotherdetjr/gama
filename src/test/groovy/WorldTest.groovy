import brotherdetjr.gama.WorldImpl
import spock.lang.Specification
import spock.lang.Unroll

import static brotherdetjr.gama.Item.newItem
import static brotherdetjr.gama.WorldImpl.torify

class WorldTest extends Specification {
    @Unroll
    def "torify() ain't just a division reminder: torify(#value, #period) == #result"() {
        expect:
        torify(value, period) == result
        where:
        value | period | result
        -8    | 4      | 0
        -7    | 4      | 1
        -6    | 4      | 2
        -5    | 4      | 3
        -4    | 4      | 0
        -3    | 4      | 1
        -2    | 4      | 2
        -1    | 4      | 3
        0     | 4      | 0
        1     | 4      | 1
        2     | 4      | 2
        3     | 4      | 3
        4     | 4      | 0
        5     | 4      | 1
        6     | 4      | 2
        7     | 4      | 3
        -6    | 3      | 0
        -5    | 3      | 1
        -4    | 3      | 2
        -3    | 3      | 0
        -2    | 3      | 1
        -1    | 3      | 2
        0     | 3      | 0
        1     | 3      | 1
        2     | 3      | 2
        3     | 3      | 0
        4     | 3      | 1
        5     | 3      | 2
        -2    | 2      | 0
        -1    | 2      | 1
        0     | 2      | 0
        1     | 2      | 1
        2     | 2      | 0
        3     | 2      | 1
        -2    | 1      | 0
        -1    | 1      | 0
        0     | 1      | 0
        1     | 1      | 0
        2     | 1      | 0
    }

    @Unroll
    def 'indexToRow(#idx) == #row, indexToColumn(#idx) == #column'() {
        given:
        def world = new WorldImpl(2, 3, true)
        expect:
        world.indexToRow(idx) == row
        world.indexToColumn(idx) == column
        where:
        idx | row | column
        0   | 0   | 0
        1   | 0   | 1
        2   | 0   | 2
        3   | 1   | 0
        4   | 1   | 1
        5   | 1   | 2
    }

    def 'indexToRow() and indexToColumn() throw IllegalArgumentException on out of bounds'() {
        given:
        def world = new WorldImpl(2, 3, true)

        when:
        world.indexToRow(6)
        then:
        thrown IllegalArgumentException

        when:
        world.indexToColumn(6)
        then:
        thrown IllegalArgumentException

        when:
        world.indexToRow(-1)
        then:
        thrown IllegalArgumentException

        when:
        world.indexToColumn(-1)
        then:
        thrown IllegalArgumentException
    }

    def 'nthFreeCellIndex(), isOccupied() and getFreeCellCount() work correctly'() {
        given:
        def world = new WorldImpl(2, 2, true)
        def nonObstacle00 = newItem('sprite', false).place(0, 0)
        def obstacle00 = newItem('sprite', true).place(0, 0)
        def obstacle01 = newItem('sprite', true).place(0, 1)
        def obstacle10 = newItem('sprite', true).place(1, 0)
        def obstacle11 = newItem('sprite', true).place(1, 1)
        def obstacle11a = newItem('sprite', true).place(1, 1)

        expect:
        world.nthFreeCellIndex(0) == 0
        world.nthFreeCellIndex(1) == 1
        world.nthFreeCellIndex(2) == 2
        world.nthFreeCellIndex(3) == 3
        world.nthFreeCellIndex(4) == 0 /* round robin */
        world.nthFreeCellIndex(5) == 1
        world.nthFreeCellIndex(6) == 2
        world.nthFreeCellIndex(7) == 3
        !world.isOccupied(0, 0)
        !world.isOccupied(0, 1)
        !world.isOccupied(1, 0)
        !world.isOccupied(1, 1)
        world.freeCellCount == 4

        when: 'added non-obstacle'
        world.attach nonObstacle00
        then: 'nothing changed'
        world.nthFreeCellIndex(0) == 0
        !world.isOccupied(0, 0)
        !world.isOccupied(0, 1)
        !world.isOccupied(1, 0)
        !world.isOccupied(1, 1)
        world.freeCellCount == 4

        when: 'added an obstacle to the first cell'
        world.attach obstacle00
        then: 'the cell is not free anymore'
        world.nthFreeCellIndex(0) == 1
        world.nthFreeCellIndex(1) == 2
        world.nthFreeCellIndex(2) == 3
        world.nthFreeCellIndex(3) == 1
        world.nthFreeCellIndex(4) == 2
        world.nthFreeCellIndex(5) == 3
        world.isOccupied(0, 0)
        !world.isOccupied(0, 1)
        !world.isOccupied(1, 0)
        !world.isOccupied(1, 1)
        world.freeCellCount == 3

        when: 'adding an obstacle to the last cell'
        world.attach obstacle11
        then: 'the cell is not free anymore'
        world.nthFreeCellIndex(0) == 1
        world.nthFreeCellIndex(1) == 2
        world.nthFreeCellIndex(2) == 1
        world.nthFreeCellIndex(3) == 2
        world.isOccupied(0, 0)
        !world.isOccupied(0, 1)
        !world.isOccupied(1, 0)
        world.isOccupied(1, 1)
        world.freeCellCount == 2

        when: 'all cells are occupied with obstacles'
        world.attach obstacle01
        world.attach obstacle10
        world.attach obstacle11a /* and even more to the last cell */
        then: '-1 returned'
        world.nthFreeCellIndex(0) == -1
        world.nthFreeCellIndex(1) == -1
        world.nthFreeCellIndex(2) == -1
        world.nthFreeCellIndex(3) == -1
        world.isOccupied(0, 0)
        world.isOccupied(0, 1)
        world.isOccupied(1, 0)
        world.isOccupied(1, 1)
        world.freeCellCount == 0

        when: 'removed an obstacle from the cell, but the still one remains'
        world.detach obstacle11
        then: 'still everything is considered occupied'
        world.nthFreeCellIndex(0) == -1
        world.nthFreeCellIndex(1) == -1
        world.nthFreeCellIndex(2) == -1
        world.nthFreeCellIndex(3) == -1
        world.isOccupied(0, 0)
        world.isOccupied(0, 1)
        world.isOccupied(1, 0)
        world.isOccupied(1, 1)
        world.freeCellCount == 0

        when: 'freed some cell'
        world.detach obstacle11a
        then: 'its index returned'
        world.nthFreeCellIndex(0) == 3
        world.nthFreeCellIndex(1) == 3
        world.nthFreeCellIndex(2) == 3
        world.nthFreeCellIndex(3) == 3
        world.isOccupied(0, 0)
        world.isOccupied(0, 1)
        world.isOccupied(1, 0)
        !world.isOccupied(1, 1)
        world.freeCellCount == 1

        when: 'freed one more cell'
        world.detach obstacle10
        then: 'its index returned as well'
        world.nthFreeCellIndex(0) == 2
        world.nthFreeCellIndex(1) == 3
        world.nthFreeCellIndex(2) == 2
        world.nthFreeCellIndex(3) == 3
        world.isOccupied(0, 0)
        world.isOccupied(0, 1)
        !world.isOccupied(1, 0)
        !world.isOccupied(1, 1)
        world.freeCellCount == 2
    }
}