package brotherdetjr.gama;

public final class PropelledItem extends Item implements Propelled {

    private Direction direction;
    private boolean justMoved;

    public PropelledItem(String sprite, boolean obstacle) {
        super(sprite, obstacle);
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
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
