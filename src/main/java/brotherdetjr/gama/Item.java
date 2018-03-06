package brotherdetjr.gama;

public class Item {

    private final String sprite;
    private final boolean obstacle;

    private int row;
    private int column;
    private int zIndex;

    public Item(String sprite, boolean obstacle) {
        this.sprite = sprite;
        this.obstacle = obstacle;
    }

    public String getSprite() {
        return sprite;
    }

    public boolean isObstacle() {
        return obstacle;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getzIndex() {
        return zIndex;
    }

    public void place(int row, int column, int zIndex) {
        this.row = row;
        this.column = column;
        this.zIndex = zIndex;
    }

    public void place(int row, int column) {
        place(row, column, zIndex);
    }
}