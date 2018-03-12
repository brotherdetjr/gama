package brotherdetjr.gama;

import java.util.List;

import static brotherdetjr.gama.Direction.DOWN;
import static brotherdetjr.gama.Direction.LEFT;
import static brotherdetjr.gama.Direction.RIGHT;
import static brotherdetjr.gama.Direction.UP;
import static com.google.common.collect.Lists.newArrayList;

public final class Renderer {

    private final int screenHeight;
    private final int screenWidth;
    private final int spriteHeightPx;
    private final int spriteWidthPx;
    private final int border;
    private final World world;

    public Renderer(int screenHeight, int screenWidth, int spriteHeightPx, int spriteWidthPx, int border, World world) {
        if (screenHeight % 2 == 0 || screenWidth % 2 == 0) {
            throw new IllegalArgumentException("screenHeight and screenWidth should be odd");
        }
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.spriteHeightPx = spriteHeightPx;
        this.spriteWidthPx = spriteWidthPx;
        this.border = border;
        this.world = world;
    }

    public List<CellEntry> render(PropelledItem povItem) {
        List<CellEntry> result = newArrayList();
        int halfHeight = screenHeight / 2;
        int row = povItem.getRow();
        for (int r = row - halfHeight - border; r <= row + halfHeight + border; r++) {
            int halfWidth = screenWidth / 2;
            int column = povItem.getColumn();
            for (int c = column - halfWidth - border; c <= column + halfWidth + border; c++) {
                int r1 = r;
                int c1 = c;
                world.getAt(r, c)
                        .values()
                        .forEach(item -> {
                            String sprite = item.getSprite();
                            List<Transformation<?>> transitions = newArrayList();
                            List<Transformation<?>> filters = newArrayList();
                            if (item instanceof DirectionalItem) {
                                DirectionalItem directionalItem = (DirectionalItem) item;
                                if (item instanceof PropelledItem) {
                                    sprite += "_";
                                    PropelledItem propelledItem = (PropelledItem) item;
                                    if (propelledItem.isJustMoved()) {
                                        sprite += "move";
                                    } else {
                                        sprite += "idle";
                                    }
                                }
                                sprite += "_" + directionalItem.getDirection().name().toLowerCase();
                            }
                            if (item != povItem) {
                                int verticalVelocity = verticalVelocity(item) - verticalVelocity(povItem);
                                if (verticalVelocity != 0) {
                                    MoveTransitionParams moveParams = new MoveTransitionParams(
                                            DOWN.name().toLowerCase(),
                                            verticalVelocity * spriteHeightPx,
                                            verticalVelocity * 2 // TODO
                                    );
                                    transitions.add(
                                            new Transformation<>(
                                                    MoveTransitionParams.TRANSITION_NAME,
                                                    moveParams
                                            )
                                    );
                                    ShiftFilterParams shiftParams = new ShiftFilterParams(
                                            UP.name().toLowerCase(),
                                            verticalVelocity * spriteHeightPx
                                    );
                                    filters.add(new Transformation<>(ShiftFilterParams.FILTER_NAME, shiftParams));
                                }
                                int horizontalVelocity = horizontalVelocity(item) - horizontalVelocity(povItem);
                                if (horizontalVelocity != 0) {
                                    MoveTransitionParams moveParams = new MoveTransitionParams(
                                            RIGHT.name().toLowerCase(),
                                            horizontalVelocity * spriteWidthPx,
                                            horizontalVelocity * 2 // TODO
                                    );
                                    transitions.add(
                                            new Transformation<>(
                                                    MoveTransitionParams.TRANSITION_NAME,
                                                    moveParams
                                            )
                                    );
                                    ShiftFilterParams shiftParams = new ShiftFilterParams(
                                            LEFT.name().toLowerCase(),
                                            horizontalVelocity * spriteWidthPx
                                    );
                                    filters.add(new Transformation<>(ShiftFilterParams.FILTER_NAME, shiftParams));
                                }
                            }
                            result.add(new CellEntry(r1 - row + halfHeight, c1 - column + halfWidth, sprite, transitions, filters, item.getzIndex()));
                        });
            }
        }
        return result;
    }

    private int verticalVelocity(Item item) {
        return velocity(item, DOWN);
    }

    private int horizontalVelocity(Item item) {
        return velocity(item, RIGHT);
    }

    private int velocity(Item item, Direction positiveDirection) {
        if (item instanceof PropelledItem) {
            PropelledItem pi = (PropelledItem) item;
            if (!pi.isJustMoved()) {
                return 0;
            } else if (pi.getDirection() == positiveDirection) {
                return 1;
            } else if (pi.getDirection() == positiveDirection.getOpposite()) {
                return -1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}
