package brotherdetjr.gama;

public final class Person implements SpriteAware, Directional, Propelled, Obstacle {

    private int row;
    private int column;
    private Direction direction;
    private boolean justMoved;
    private final String sprite;

    public Person(String sprite) {
        this.sprite = sprite;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public void place(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean isJustMoved() {
        return justMoved;
    }

    @Override
    public void setJustMoved(boolean justMoved) {
        this.justMoved = justMoved;
    }

    @Override
    public String getSprite() {
        return sprite;
    }
}
