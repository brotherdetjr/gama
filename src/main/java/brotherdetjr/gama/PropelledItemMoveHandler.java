package brotherdetjr.gama;

import java.util.function.BiConsumer;

public final class PropelledItemMoveHandler implements BiConsumer<PropelledItem, MoveRequest> {

    private final World world;
    private final boolean torus;

    public PropelledItemMoveHandler(World world, boolean torus) {
        this.world = world;
        this.torus = torus;
    }

    @Override
    public void accept(PropelledItem propelledItem, MoveRequest moveRequest) {
        int r = propelledItem.getRow();
        int c = propelledItem.getColumn();
        switch (moveRequest.getDirection()) {
            case UP: r--; break;
            case DOWN: r++; break;
            case LEFT: c--; break;
            case RIGHT: c++;
        }
        if (torus && !world.embraces(r, c)) {
            switch (moveRequest.getDirection()) {
                case UP: r = world.getHeight() - 1; break;
                case DOWN: r = 0; break;
                case LEFT: c = world.getWidth() - 1; break;
                case RIGHT: c = 0;
            }
        }
        if (world.embraces(r, c) && world.getAt(r, c).values().stream().noneMatch(Item::isObstacle)) {
            world.detach(propelledItem);
            propelledItem.place(r, c);
            world.attach(propelledItem);
            propelledItem.markJustMoved();
        } else {
            propelledItem.unmarkJustMoved();
        }
        propelledItem.pointTo(moveRequest.getDirection());
    }
}
