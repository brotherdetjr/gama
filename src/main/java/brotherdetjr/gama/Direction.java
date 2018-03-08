package brotherdetjr.gama;

public enum Direction {
    UP("DOWN", true), DOWN("UP", true), LEFT("RIGHT", false), RIGHT("LEFT", false);

    private final String opposite;
    private final boolean vertical;

    Direction(String opposite, boolean vertical) {
        this.opposite = opposite;
        this.vertical = vertical;
    }

    public static Direction parse(String text) {
        return valueOf(text.toUpperCase());
    }

    public Direction getOpposite() {
        return valueOf(opposite);
    }

    public boolean isVertical() {
        return vertical;
    }
}
