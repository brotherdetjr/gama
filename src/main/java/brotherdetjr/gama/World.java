package brotherdetjr.gama;

import java.util.Map;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.setAll;
import static java.util.Collections.emptyMap;

public final class World {

    private final int height;
    private final int width;
    private final Map<Integer, Item>[] items;

    public World(int height, int width) {
        this.height = height;
        this.width = width;
        //noinspection unchecked
        items = new Map[height * width];
        setAll(items, ignore -> newHashSet());
    }

    public void attach(Item item) {
        getAt(item.getRow(), item.getColumn()).put(item.getzIndex(), item);
    }

    public void detach(Item item) {
        getAt(item.getRow(), item.getColumn()).remove(item.getzIndex());
    }

    public Map<Integer, Item> getAt(int row, int column) {
        if (embraces(row, column)) {
            return items[row * width + column];
        } else {
            return emptyMap();
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
