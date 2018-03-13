package brotherdetjr.gama;

import java.util.List;

public final class CellEntry {
    private final int row;
    private final int column;
    private final String sprite;
    private final List<? extends Transformation> transitions;
    private final List<? extends Transformation> filters;
    private final int zIndex;

    public CellEntry(int row,
                     int column,
                     String sprite,
                     List<? extends Transformation> transitions,
                     List<? extends Transformation> filters,
                     int zIndex) {
        this.row = row;
        this.column = column;
        this.sprite = sprite;
        this.transitions = transitions;
        this.filters = filters;
        this.zIndex = zIndex;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String getSprite() {
        return sprite;
    }

    public List<? extends Transformation> getTransitions() {
        return transitions;
    }

    public List<? extends Transformation> getFilters() {
        return filters;
    }

    public int getzIndex() {
        return zIndex;
    }
}
