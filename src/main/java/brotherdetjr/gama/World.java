package brotherdetjr.gama;

import java.util.Map;

public interface World {
    void attach(Item item);

    void detach(Item item);

    Map<Integer, Item> getAt(int row, int column);

    boolean embraces(int row, int column);

    int getHeight();

    int getWidth();

    boolean isTorus();

    boolean isOccupied(int row, int column);

    int nthFreeCellIndex(int n);

    int indexToRow(int index);

    int indexToColumn(int index);

    int getFreeCellCount();

    void nextTick();

    long getTick();
}
