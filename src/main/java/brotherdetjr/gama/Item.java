package brotherdetjr.gama;

public class Item {

    private final String sprite;
    private final boolean obstacle;

    private int row;
    private int column;
    private Integer zIndex;

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

    public Integer getzIndex() {
        return zIndex;
    }

    public Item place(int row, int column, Integer zIndex) {
        this.row = row;
        this.column = column;
        this.zIndex = zIndex;
        return this;
    }

    public Item place(int row, int column) {
        return place(row, column, zIndex);
    }
}
