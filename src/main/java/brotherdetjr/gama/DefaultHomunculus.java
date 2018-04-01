package brotherdetjr.gama;

import java.util.ArrayList;
import java.util.List;

import static brotherdetjr.gama.Direction.DOWN;
import static brotherdetjr.gama.Direction.LEFT;
import static brotherdetjr.gama.Direction.RIGHT;
import static brotherdetjr.gama.Direction.UP;
import static java.util.Arrays.fill;

public final class DefaultHomunculus implements Homunculus {

    private int row = -1;
    private int column = -1;

    @Override
    public Object poll(Perception perception) {
        MoveRequest request = getMoveRequest(perception);
        if (request != null) {
            switch (request.getDirection()) {
                case UP:
                    row++;
                    break;
                case DOWN:
                    row--;
                    break;
                case LEFT:
                    column++;
                    break;
                case RIGHT:
                    column--;
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
        return request;
    }

    private MoveRequest getMoveRequest(Perception p) {
        int height = p.getScreenHeightInSprites();
        int width = p.getScreenWidthInSprites();
        if (row == height / 2 && column == width / 2 || row == -1 || column == -1) {
            return null;
        }
        boolean[] map = new boolean[height * width];
        p.getCellEntries().forEach(e -> {
            if (isObstacle(e.getSprite())) {
                map[e.getRow() * width + e.getColumn()]  = true;
            }
        });
        Direction direction = new Wave(height, width, map).firstStep(row, column);
        if (direction != null) {
            return new MoveRequest(direction);
        } else {
            return null;
        }
    }

    private static final class Wave {
        private final int height;
        private final int width;
        private final boolean[] map;

        public Wave(int height, int width, boolean[] map) {
            this.height = height;
            this.width = width;
            this.map = map;
        }

        public Direction firstStep(int row, int column) {
            int target = getTargetIndex(row, column);
            if (target != -1) {
                int[] wave = new int[map.length];
                fill(wave, -1);
                int centerIdx = toIndex(height / 2, width / 2);
                wave[centerIdx] = 0;
                List<Integer> front = new ArrayList<>(map.length);
                front.add(centerIdx);
                boolean advance = true;
                int step = 1;
                while (advance) {
                    List<Integer> newFront = new ArrayList<>(map.length);
                    for (int c : front) {
                        tryToCover(c - 1, step, wave, newFront);
                        tryToCover(c + 1, step, wave, newFront);
                        tryToCover(c - width, step, wave, newFront);
                        tryToCover(c + width, step, wave, newFront);
                    }
                    advance = !newFront.isEmpty() && !newFront.contains(target);
                    if (advance) {
                        front = newFront;
                        step++;
                    }
                }
                if (safe(wave, centerIdx - 1) == 1) {
                    return LEFT;
                } else if (safe(wave, centerIdx + 1) == 1) {
                    return RIGHT;
                } else if (safe(wave, centerIdx - width) == 1) {
                    return UP;
                } else if (safe(wave, centerIdx + width) == 1) {
                    return DOWN;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        private int safe(int[] wave, int idx) {
            return idx >= 0 && idx < map.length ? wave[idx] : -1;
        }

        private void tryToCover(int idx, int step, int[] wave, List<Integer> newFront) {
            if (idx >= 0 && idx < map.length && !map[idx] && wave[idx] == -1) {
                wave[idx] = step;
                newFront.add(idx);
            }
        }

        private int getTargetIndex(int row, int column) {
            // TODO if row/column not reachable, give the closest reachable
            if (!map[toIndex(row, column)]) {
                return toIndex(row, column);
            } else {
                return -1;
            }
        }

        private int toIndex(int row, int column) {
            return row * width + column;
        }
    }

    private static boolean isObstacle(String sprite) {
        // TODO parse composition.json
        return sprite.startsWith("cactus") || sprite.startsWith("sign") || sprite.startsWith("bant");
    }

    @Override
    public <T> void handle(T command) {
        if (command instanceof WalkCommand) {
            WalkCommand c = (WalkCommand) command;
            row = c.getRow();
            column = c.getColumn();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
