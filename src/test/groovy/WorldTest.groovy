import spock.lang.Specification
import spock.lang.Unroll

import static brotherdetjr.gama.World.torify


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
}