package brotherdetjr.gama;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static brotherdetjr.gama.Direction.DOWN;
import static brotherdetjr.gama.Direction.RIGHT;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;

public final class Renderer {

    private final int spriteHeightPx;
    private final int spriteWidthPx;
    private final World world;

    public Renderer(int spriteHeightPx, int spriteWidthPx, World world) {
        this.spriteHeightPx = spriteHeightPx;
        this.spriteWidthPx = spriteWidthPx;
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
                            List<? extends Transformation> filters = emptyList();
                            if (item != povItem) {
                                transitions = transitions(item, povItem);
                                filters = filters(item, povItem);
                            }
                            cellEntries.add(
                                    new CellEntry(
                                            r1 - row + halfHeight,
                                            c1 - column + halfWidth,
                                            toSpriteName(item),
                                            transitions,
                                            filters,
                                            item.getzIndex()
                                    )
                            );
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
        return transformations(direction -> moveTransition(item, povItem, direction));
    }

    private List<? extends Transformation> filters(Item item, PropelledItem povItem) {
        return transformations(direction -> shiftFilter(item, povItem, direction));
    }

    private <P> List<Transformation<P>> transformations(Function<Direction, Transformation<P>> factory) {
        Transformation<P> t = factory.apply(DOWN);
        List<Transformation<P>> result = null;
        if (t != null) {
            result = newArrayList();
            result.add(t);
        }
        t = factory.apply(RIGHT);
        if (t != null) {
            result = result == null ? newArrayList() : result;
            result.add(t);
        }
        return result != null ? result : emptyList();
    }

    private <P> Transformation<P> transformation(
            Item item,
            PropelledItem povItem,
            Direction positiveDirection,
            BiFunction<Integer, Integer, Transformation<P>> factory) {
        int velocity = velocity(item, positiveDirection) - velocity(povItem, positiveDirection);
        if (velocity != 0) {
            return factory.apply(velocity, positiveDirection.isVertical() ? spriteHeightPx : spriteWidthPx);
        } else {
            return null;
        }
    }

    private Transformation<MoveTransitionParams> moveTransition(
            Item item, PropelledItem povItem, Direction positiveDirection) {
        return transformation(
                item,
                povItem,
                positiveDirection,
                (velocity, spriteDimPx) ->
                        new Transformation<>(
                                MoveTransitionParams.TRANSITION_NAME,
                                new MoveTransitionParams(
                                        positiveDirection.name().toLowerCase(),
                                        velocity * spriteDimPx,
                                        velocity * 2 // TODO
                                )
                        )
        );

    }

    private Transformation<ShiftFilterParams> shiftFilter(
            Item item, PropelledItem povItem, Direction positiveDirection) {
        return transformation(
                item,
                povItem,
                positiveDirection,
                (velocity, spriteDimPx) ->
                        new Transformation<>(
                                ShiftFilterParams.FILTER_NAME,
                                new ShiftFilterParams(
                                        positiveDirection.getOpposite().name().toLowerCase(),
                                        velocity * spriteDimPx
                                )
                        )
        );

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
        return world.getTick() - item.getLastMoveTick() < 2;
    }
}
