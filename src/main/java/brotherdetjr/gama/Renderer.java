package brotherdetjr.gama;

import java.util.List;

import static brotherdetjr.gama.Direction.DOWN;
import static brotherdetjr.gama.Direction.RIGHT;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.signum;
import static java.util.Collections.emptyList;

public final class Renderer {

    private final int spriteHeightPx;
    private final int spriteWidthPx;
    private final int basicYStepPx;
    private final int basicXStepPx;
    private final World world;

    public Renderer(int spriteHeightPx, int spriteWidthPx, int basicYStepPx, int basicXStepPx, World world) {
        this.spriteHeightPx = spriteHeightPx;
        this.spriteWidthPx = spriteWidthPx;
        this.basicYStepPx = basicYStepPx;
        this.basicXStepPx = basicXStepPx;
        this.world = world;
    }

    public Perception render(PropelledItem povItem, int screenHeight, int screenWidth, int border) {
        if (screenHeight % 2 == 0 || screenWidth % 2 == 0) {
            throw new IllegalArgumentException("screenHeight and screenWidth should be odd");
        }
        List<CellEntry> cellEntries = newArrayList();
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
                            List<? extends Transformation> transitions = emptyList();
                            boolean needToRender;
                            int ir = r1 - row + halfHeight;
                            int itemRow1 = ir + velocity(povItem, DOWN) - velocity(item, DOWN);
                            int ic = c1 - column + halfWidth;
                            int itemColumn1 = ic + velocity(povItem, RIGHT) - velocity(item, RIGHT);
                            if (item != povItem) {
                                needToRender = needToRender(itemRow1, itemColumn1, ir, ic,
                                        screenHeight, screenWidth, basicYStepPx, basicXStepPx);
                                if (needToRender) {
                                    transitions = transitions(item, povItem);
                                }
                            } else {
                                needToRender = true;
                            }
                            if (needToRender) {
                                cellEntries.add(
                                        new CellEntry(
                                                itemRow1,
                                                itemColumn1,
                                                toSpriteName(item),
                                                transitions,
                                                emptyList(),
                                                item.getzIndex()
                                        )
                                );
                            }
                        });
            }
        }
        return new Perception(screenHeight, screenWidth, cellEntries);
    }

    private String toSpriteName(Item item) {
        String sprite = item.getSprite();
        if (item instanceof DirectionalItem) {
            DirectionalItem directionalItem = (DirectionalItem) item;
            if (item instanceof PropelledItem) {
                if (isJustMoved((PropelledItem) item)) {
                    sprite += "_move";
                } else {
                    sprite += "_idle";
                }
            }
            sprite += "_" + directionalItem.getDirection().name().toLowerCase();
        }
        return sprite;
    }

    private List<? extends Transformation> transitions(Item item, PropelledItem povItem) {
        Transformation<MoveTransitionParams> t = moveTransition(item, povItem, DOWN);
        List<Transformation<MoveTransitionParams>> result = null;
        if (t != null) {
            result = newArrayList();
            result.add(t);
        }
        t = moveTransition(item, povItem, RIGHT);
        if (t != null) {
            result = result == null ? newArrayList() : result;
            result.add(t);
        }
        return result != null ? result : emptyList();
    }

    private Transformation<MoveTransitionParams> moveTransition(
            Item item, PropelledItem povItem, Direction positiveDirection) {
        int velocity = velocity(item, positiveDirection) - velocity(povItem, positiveDirection);
        if (velocity != 0) {
            return new Transformation<>(
                    MoveTransitionParams.TRANSITION_NAME,
                    new MoveTransitionParams(
                            velocity > 0 ? positiveDirection.name().toLowerCase() : positiveDirection.getOpposite().name().toLowerCase(),
                            abs(velocity) * (positiveDirection.isVertical() ? spriteHeightPx : spriteWidthPx),
                            abs(velocity) * (positiveDirection.isVertical() ? basicYStepPx : basicXStepPx)
                    )
            );
        } else {
            return null;
        }
    }

    private int velocity(Item item, Direction positiveDirection) {
        if (item instanceof PropelledItem) {
            PropelledItem pi = (PropelledItem) item;
            if (!isJustMoved(pi)) {
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

    private boolean isJustMoved(PropelledItem item) {
        return world.getTick() == item.getLastMoveTick();
    }

    private boolean needToRender(int itemRow1, int itemColumn1, int itemRow2, int itemColumn2,
                                 int screenHeight, int screenWidth, int basicYStepPx, int basicXStepPx) {
        if (itemRow1 < 0 && itemRow2 < 0 ||
                itemRow1 >= screenHeight && itemRow2 >= screenHeight ||
                itemColumn1 < 0 && itemColumn2 < 0 ||
                itemColumn1 >= screenWidth && itemColumn2 >= screenWidth) {
            return false;
        }
        if (itemRow1 >= 0 && itemRow1 < screenHeight &&
                itemRow2 >= 0 && itemRow2 < screenHeight &&
                itemColumn1 >= 0 && itemColumn1 < screenWidth &&
                itemColumn2 >= 0 && itemColumn2 < screenWidth) {
            return true;
        }
        int posY = 0;
        int posX = 0;
        int targetPosY = abs(itemRow2 - itemRow1) * spriteHeightPx;
        int targetPosX = abs(itemColumn2 - itemColumn1) * spriteWidthPx;
        int yStepPx = abs(itemRow2 - itemRow1) * basicXStepPx;
        int xStepPx = abs(itemColumn2 - itemColumn1) * basicYStepPx;
        int kY = (int) signum(itemRow2 - itemRow1);
        int kX = (int) signum(itemColumn2 - itemColumn1);
        while (posX < targetPosX || posY < targetPosY) {
            int y1 = spriteHeightPx * itemRow1 + posY * kY;
            int y2 = y1 + spriteHeightPx - 1;
            int x1 = spriteWidthPx * itemColumn1 + posX * kX;
            int x2 = x1 + spriteWidthPx - 1;
            if (isInScreenBorders(y1, x1, screenHeight, screenWidth) ||
                    isInScreenBorders(y1, x2, screenHeight, screenWidth) ||
                    isInScreenBorders(y2, x1, screenHeight, screenWidth) ||
                    isInScreenBorders(y2, x2, screenHeight, screenWidth)) {
                return true;
            }
            if (posY < targetPosY) {
                posY += min(yStepPx, targetPosY - posY);
            }
            if (posX < targetPosX) {
                posX += min(xStepPx, targetPosX - posX);
            }
        }
        int y1 = spriteHeightPx * itemRow1 + posY * kY;
        int y2 = y1 + (spriteHeightPx - 1) * kY;
        int x1 = spriteWidthPx * itemColumn1 + posX * kX;
        int x2 = x1 + (spriteWidthPx - 1) * kX;
        return isInScreenBorders(y1, x1, screenHeight, screenWidth) ||
                isInScreenBorders(y1, x2, screenHeight, screenWidth) ||
                isInScreenBorders(y2, x1, screenHeight, screenWidth) ||
                isInScreenBorders(y2, x2, screenHeight, screenWidth);
    }

    private boolean isInScreenBorders(int y, int x, int screenHeight, int screenWidth) {
        return y >= 0 && y < screenHeight * spriteHeightPx && x >= 0 && x < screenWidth * spriteWidthPx;
    }

}
