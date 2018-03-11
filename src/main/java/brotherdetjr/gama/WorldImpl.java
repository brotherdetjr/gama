package brotherdetjr.gama;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.setAll;
import static java.util.Collections.emptyMap;

public final class WorldImpl implements World {

    private final int height;
    private final int width;
    private final Map<Integer, Item>[] items;
    private final boolean torus;

    public WorldImpl(int height, int width, boolean torus) {
        this.height = height;
        this.width = width;
        //noinspection unchecked
        items = new Map[height * width];
        setAll(items, ignore -> newHashMap());
        this.torus = torus;
    }

    @Override
    public void attach(Item item) {
        int row = item.getRow();
        int column = item.getColumn();
        getAt(row, column).put(item.getzIndex(), item);
    }

    @Override
    public void detach(Item item) {
        int row = item.getRow();
        int column = item.getColumn();
        getAt(row, column).remove(item.getzIndex());
    }

    @Override
    public Map<Integer, Item> getAt(int row, int column) {
        if (embraces(row, column)) {
            return items[toLinearIndex(row, column)];
        } else {
            return emptyMap();
        }
    }

    @Override
    public boolean embraces(int row, int column) {
        return torus || row >= 0 && row < height && column >= 0 && column < width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public boolean isTorus() {
        return torus;
    }

    @Override
    public boolean isOccupied(int row, int column) {
        return getAt(row, column).values().stream().anyMatch(Item::isObstacle);
    }

    @Override
    public Map.Entry<Integer, Integer> nthFreeCell(int n) {
        throw new UnsupportedOperationException();
    }

    public static int torify(int value, int period) {
        return (period + value % period) % period;
    }

    private int toLinearIndex(int row, int column) {
        return torify(row, height) * width + torify(column, width);
    }
}
