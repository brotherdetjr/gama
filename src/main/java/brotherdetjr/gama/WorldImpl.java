package brotherdetjr.gama;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Arrays.setAll;
import static java.util.Collections.emptyMap;

public final class WorldImpl implements World {

    private final int height;
    private final int width;
    private final Cell[] cells;
    private final boolean torus;
    private int freeCellCount;

    public WorldImpl(int height, int width, boolean torus) {
        this.height = height;
        this.width = width;
        cells = new Cell[height * width];
        setAll(cells, ignore -> new Cell());
        this.torus = torus;
        freeCellCount = height * width;
    }

    @Override
    public void attach(Item item) {
        Cell cell = getCell(item.getRow(), item.getColumn());
        if (cell != null) {
            cell.items.put(item.getzIndex(), item);
            if (item.isObstacle()) {
                cell.obstacleCount++;
                if (cell.obstacleCount == 1) {
                    freeCellCount--;
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void detach(Item item) {
        Cell cell = getCell(item.getRow(), item.getColumn());
        if (cell != null) {
            cell.items.remove(item.getzIndex(), item);
            if (item.isObstacle()) {
                cell.obstacleCount--;
                if (cell.obstacleCount == 0) {
                    freeCellCount++;
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Map<Integer, Item> getAt(int row, int column) {
        Cell cell = getCell(row, column);
        return cell != null ? cell.items : emptyMap();
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
        Cell cell = getCell(row, column);
        if (cell != null) {
            return cell.obstacleCount > 0;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int nthFreeCellIndex(int n) {
        if (freeCellCount > 0) {
            n %= freeCellCount;
            for (int i = 0, j = 0; i < cells.length; i++) {
                if (cells[i].obstacleCount == 0) {
                    j++;
                }
                if (j > n) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int torify(int value, int period) {
        return (period + value % period) % period;
    }

    @Override
    public int indexToRow(int index) {
        checkIndexInBounds(index);
        return index / width;
    }

    @Override
    public int indexToColumn(int index) {
        checkIndexInBounds(index);
        return index % width;
    }

    @Override
    public int getFreeCellCount() {
        return freeCellCount;
    }

    private void checkIndexInBounds(int index) {
        if (index >= cells.length || index < 0) {
            throw new IllegalArgumentException();
        }
    }

    private Cell getCell(int row, int column) {
        if (embraces(row, column)) {
            return cells[toLinearIndex(row, column)];
        } else {
            return null;
        }
    }

    private int toLinearIndex(int row, int column) {
        return torify(row, height) * width + torify(column, width);
    }

    private static class Cell {
        int obstacleCount;
        Map<Integer, Item> items = newHashMap();
    }
}
