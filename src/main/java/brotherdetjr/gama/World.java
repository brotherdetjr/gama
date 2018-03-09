package brotherdetjr.gama;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.lang.Math.abs;
import static java.util.Arrays.setAll;
import static java.util.Collections.emptyMap;

public final class World {

    private final int height;
    private final int width;
    private final Map<Integer, Item>[] items;
    private final boolean torus;

    public World(int height, int width, boolean torus) {
        this.height = height;
        this.width = width;
        //noinspection unchecked
        items = new Map[height * width];
        setAll(items, ignore -> newHashMap());
        this.torus = torus;
    }

    public void attach(Item item) {
        getAt(item.getRow(), item.getColumn()).put(item.getzIndex(), item);
    }

    public void detach(Item item) {
        getAt(item.getRow(), item.getColumn()).remove(item.getzIndex());
    }

    public Map<Integer, Item> getAt(int row, int column) {
        if (torus) {
            return items[abs(row % height) * width + abs(column % width)];
        } else {
            if (embraces(row, column)) {
                return items[row * width + column];
            } else {
                return emptyMap();
            }
        }
    }

    public boolean embraces(int row, int column) {
        return torus || row >= 0 && row < height && column >= 0 && column < width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean isTorus() {
        return torus;
    }
}
