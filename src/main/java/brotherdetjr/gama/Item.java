package brotherdetjr.gama;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public class Item<T extends Item> {

    private final String sprite;
    private final boolean obstacle;

    private int row;
    private int column;
    private Integer zIndex;

    protected Item(String sprite, boolean obstacle) {
        this.sprite = sprite.intern();
        this.obstacle = obstacle;
    }

    public static Item<? extends Item> newItem(String sprite, boolean obstacle) {
        return new Item<>(sprite, obstacle);
    }

    public final String getSprite() {
        return sprite;
    }

    public final boolean isObstacle() {
        return obstacle;
    }

    public final int getRow() {
        return row;
    }

    public final int getColumn() {
        return column;
    }

    public final Integer getzIndex() {
        return zIndex;
    }

    @SuppressWarnings("unchecked")
    public final T place(int row, int column, Integer zIndex) {
        this.row = row;
        this.column = column;
        this.zIndex = zIndex;
        return (T) this;
    }

    public final T place(int row, int column) {
        return place(row, column, zIndex);
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("sprite", sprite)
                .add("obstacle", obstacle)
                .add("row", row)
                .add("column", column)
                .add("zIndex", zIndex);
    }
}
