package brotherdetjr.gama;

import java.util.function.BiConsumer;

public final class PropelledItemMoveHandler implements BiConsumer<PropelledItem, MoveRequest> {

    private final World world;

    public PropelledItemMoveHandler(World world) {
        this.world = world;
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
        if (world.embraces(r, c) && world.getAt(r, c).stream().noneMatch(Item::isObstacle)) {
            world.detach(propelledItem);
            propelledItem.place(r, c);
            world.attach(propelledItem);
            propelledItem.setJustMoved(true);
        } else {
            propelledItem.setJustMoved(false);
        }
        propelledItem.setDirection(moveRequest.getDirection());
    }
}
