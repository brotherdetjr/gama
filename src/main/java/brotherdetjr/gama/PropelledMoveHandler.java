package brotherdetjr.gama;

import java.util.function.BiConsumer;

public final class PropelledMoveHandler implements BiConsumer<Propelled, MoveRequest> {

    private final World world;

    public PropelledMoveHandler(World world) {
        this.world = world;
    }

    @Override
    public void accept(Propelled obj, MoveRequest moveRequest) {
        int r = obj.getRow();
        int c = obj.getColumn();
        switch (moveRequest.getDirection()) {
            case UP: r--; break;
            case DOWN: r++; break;
            case LEFT: c--; break;
            case RIGHT: c++;
        }
        if (world.embraces(r, c) && world.getAt(r, c).stream().noneMatch(o -> o instanceof Obstacle)) {
            world.detach(obj);
            obj.place(r, c);
            world.attach(obj);
            obj.setJustMoved(true);
        } else {
            obj.setJustMoved(false);
        }
        obj.setDirection(moveRequest.getDirection());
    }
}
