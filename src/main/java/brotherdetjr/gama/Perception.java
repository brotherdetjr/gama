package brotherdetjr.gama;

import java.util.List;

public final class Perception {
    private final int screenHeightInSprites;
    private final int screenWidthInSprites;
    private final List<CellEntry> cellEntries;

    public Perception(int screenHeightInSprites, int screenWidthInSprites, List<CellEntry> cellEntries) {
        this.screenHeightInSprites = screenHeightInSprites;
        this.screenWidthInSprites = screenWidthInSprites;
        this.cellEntries = cellEntries;
    }

    public int getScreenHeightInSprites() {
        return screenHeightInSprites;
    }

    public int getScreenWidthInSprites() {
        return screenWidthInSprites;
    }

    public List<CellEntry> getCellEntries() {
        return cellEntries;
    }
}
