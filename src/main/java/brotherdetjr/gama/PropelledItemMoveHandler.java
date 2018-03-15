package brotherdetjr.gama;

import java.util.function.BiConsumer;

public final class PropelledItemMoveHandler implements BiConsumer<PropelledItem, MoveRequest> {

    private final World world;

    public PropelledItemMoveHandler(World world) {
        this.world = world;
    }

    @Override
    public void accept(PropelledItem item, MoveRequest moveRequest) {
        int r = item.getRow();
        int c = item.getColumn();
        switch (moveRequest.getDirection()) {
            case UP: r--; break;
            case DOWN: r++; break;
            case LEFT: c--; break;
            case RIGHT: c++;
        }
        if (world.embraces(r, c) && !world.isOccupied(r, c)) {
            world.detach(item);
            item.setPreviousPos(item.getRow(), item.getPreviousColumn())
                    .place(r, c)
                    .setLastMoveTick(world.getTick());
            world.attach(item);
        }
        item.pointTo(moveRequest.getDirection());
    }
}
