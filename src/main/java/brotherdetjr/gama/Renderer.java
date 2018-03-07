package brotherdetjr.gama;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

public final class Renderer {

    private final int screenHeight;
    private final int screenWidth;
    private final int spriteHeightPx;
    private final int spriteWidthPx;
    private final World world;

    public Renderer(int screenHeight, int screenWidth, int spriteHeightPx, int spriteWidthPx, World world) {
        if (screenHeight % 2 == 0 || screenWidth % 2 == 0) {
            throw new IllegalArgumentException("screenHeight and screenWidth should be odd");
        }
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.spriteHeightPx = spriteHeightPx;
        this.spriteWidthPx = spriteWidthPx;
        this.world = world;
    }

    public List<List<List<CellEntry>>> render(PropelledItem propelledItem) {
        List<List<List<CellEntry>>> result = newArrayList();
        int halfHeight = screenHeight / 2;
        int row = propelledItem.getRow();
        for (int r = row - halfHeight - 1; r < row + halfHeight + 1; r++) {
            ArrayList<List<CellEntry>> rowCells = newArrayList();
            result.add(rowCells);
            int halfWidth = screenWidth / 2;
            int column = propelledItem.getColumn();
            for (int c = column - halfWidth - 1; c < column + halfWidth + 1; c++) {
                List<CellEntry> cell = world.getAt(r, c)
                        .values()
                        .stream()
                        .map(item -> {
                            String sprite = item.getSprite();
                            List<Transformation<?>> transitions = newArrayList();
                            List<Transformation<?>> filters = newArrayList();
                            if (item instanceof DirectionalItem) {
                                DirectionalItem directionalItem = (DirectionalItem) item;
                                Direction direction = directionalItem.getDirection();
                                if (item instanceof PropelledItem) {
                                    sprite += "_";
                                    if (((PropelledItem) item).isJustMoved()) {
                                        sprite += "move";
                                        ShiftFilterParams shiftParams = new ShiftFilterParams(
                                                direction.getOpposite().toString().toLowerCase(),
                                                distancePx(direction)
                                        );
                                        filters.add(new Transformation<>(ShiftFilterParams.FILTER_NAME, shiftParams));
                                    } else {
                                        sprite += "idle";
                                    }
                                }
                                sprite += "_" + directionalItem.getDirection().name().toLowerCase();
                            }
                            if (propelledItem.isJustMoved()) {
                                Direction direction = propelledItem.getDirection();
                                MoveTransitionParams moveParams = new MoveTransitionParams(
                                        direction.getOpposite().toString().toLowerCase(),
                                        distancePx(direction)
                                );
                                transitions.add(
                                        new Transformation<>(
                                                MoveTransitionParams.TRANSITION_NAME,
                                                moveParams
                                        )
                                );
                            }
                            return new CellEntry(sprite, transitions, filters, item.getzIndex());
                        }).collect(toList());
                rowCells.add(cell);
            }
        }
        return result;
    }

    private int distancePx(Direction direction) {
        return direction.isVertical() ? spriteHeightPx : spriteWidthPx;
    }
}
