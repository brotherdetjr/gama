package brotherdetjr.gama;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.setAll;
import static java.util.Collections.emptySet;

public final class World {

    private final int height;
    private final int width;
    private final Set<Placed>[] map;

    public World(int height, int width) {
        this.height = height;
        this.width = width;
        //noinspection unchecked
        map = new Set[height * width];
        setAll(map, ignore -> newHashSet());
    }

    public void attach(Placed obj) {
        getAt(obj.getRow(), obj.getColumn()).add(obj);
    }

    public void detach(Placed obj) {
        getAt(obj.getRow(), obj.getColumn()).remove(obj);
    }

    public Set<Placed> getAt(int row, int column) {
        if (embraces(row, column)) {
            return map[row * width + column];
        } else {
            return emptySet();
        }
    }

    public boolean embraces(int row, int column) {
        return row >= 0 && row < height && column >= 0 && column < width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
