package brotherdetjr.gama;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.setAll;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

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
        getAt(item.getRow(), item.getColumn()).put(item.getzIndex(), item);
    }

    @Override
    public void detach(Item item) {
        getAt(item.getRow(), item.getColumn()).remove(item.getzIndex());
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
    public Map.Entry<Integer, Integer> nthFreeCellRowColumn(int n) {
        // TODO can be seriously optimized and work even for totally empty cells
        List<Map<Integer, Item>> freeCells = stream(items)
                .filter(cell -> cell.values().stream().noneMatch(Item::isObstacle))
                .collect(toList());
        Item it = freeCells.get(n % freeCells.size()).get(0);
        return new AbstractMap.SimpleEntry<>(it.getRow(), it.getColumn());
    }

    public static int torify(int value, int period) {
        return (period + value % period) % period;
    }

    private int toLinearIndex(int row, int column) {
        return torify(row, height) * width + torify(column, width);
    }
}
