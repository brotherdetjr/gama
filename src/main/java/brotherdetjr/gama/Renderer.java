package brotherdetjr.gama;

import java.util.ArrayList;
import java.util.List;

import static brotherdetjr.gama.Direction.DOWN;
import static brotherdetjr.gama.Direction.LEFT;
import static brotherdetjr.gama.Direction.RIGHT;
import static brotherdetjr.gama.Direction.UP;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;

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

    public List<List<List<CellEntry>>> render(PropelledItem povItem) {
        List<List<List<CellEntry>>> result = newArrayList();
        int halfHeight = screenHeight / 2;
        int row = povItem.getRow();
        for (int r = row - halfHeight - border; r <= row + halfHeight + border; r++) {
            ArrayList<List<CellEntry>> rowCells = newArrayList();
            result.add(rowCells);
            int halfWidth = screenWidth / 2;
            int column = povItem.getColumn();
            for (int c = column - halfWidth - border; c <= column + halfWidth + border; c++) {
                List<CellEntry> cell = world.getAt(r, c)
                        .values()
                        .stream()
                        .map(item -> {
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
                                int verticalShift = verticalVelocity(povItem) - verticalVelocity(item);
                                if (verticalShift != 0) {
                                    Direction verticalDirection = verticalShift > 0 ? DOWN : UP;
                                    ShiftFilterParams shiftParams = new ShiftFilterParams(
                                            verticalDirection.name().toLowerCase(),
                                            abs(verticalShift) * spriteHeightPx
                                    );
                                    filters.add(new Transformation<>(ShiftFilterParams.FILTER_NAME, shiftParams));
                                }
                                int horizontalShift = horizontalVelocity(povItem) - horizontalVelocity(item);
                                if (horizontalShift != 0) {
                                    Direction horizontalDirection = horizontalShift > 0 ? RIGHT : LEFT;
                                    ShiftFilterParams shiftParams = new ShiftFilterParams(
                                            horizontalDirection.name().toLowerCase(),
                                            abs(horizontalShift) * spriteWidthPx
                                    );
                                    filters.add(new Transformation<>(ShiftFilterParams.FILTER_NAME, shiftParams));
                                }
                            }
                            if (item != povItem) {
                                int verticalVelocity = verticalVelocity(item) - verticalVelocity(povItem);
                                if (verticalVelocity != 0) {
                                    MoveTransitionParams moveParams = new MoveTransitionParams(
                                            (verticalVelocity > 0 ? DOWN : UP).name().toLowerCase(),
                                            abs(verticalVelocity) * spriteHeightPx,
                                            abs(verticalVelocity) * 2 // TODO
                                    );
                                    transitions.add(
                                            new Transformation<>(
                                                    MoveTransitionParams.TRANSITION_NAME,
                                                    moveParams
                                            )
                                    );
                                }
                                int horizontalVelocity = horizontalVelocity(item) - horizontalVelocity(povItem);
                                if (horizontalVelocity != 0) {
                                    MoveTransitionParams moveParams = new MoveTransitionParams(
                                            (horizontalVelocity > 0 ? RIGHT : LEFT).name().toLowerCase(),
                                            abs(horizontalVelocity) * spriteWidthPx,
                                            abs(horizontalVelocity) * 2 // TODO
                                    );
                                    transitions.add(
                                            new Transformation<>(
                                                    MoveTransitionParams.TRANSITION_NAME,
                                                    moveParams
                                            )
                                    );
                                }
                            }
                            return new CellEntry(sprite, transitions, filters, item.getzIndex());
                        }).collect(toList());
                rowCells.add(cell);
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
