package brotherdetjr.gama;

public class DirectionalItem extends Item {

    private Direction direction;

    public DirectionalItem(String sprite, boolean obstacle) {
        super(sprite, obstacle);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
