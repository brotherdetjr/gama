package brotherdetjr.gama;

import com.google.common.base.MoreObjects.ToStringHelper;

public final class PropelledItem extends DirectionalItem<PropelledItem> {

    private long lastMoveTick;
    private int previousRow;
    private int previousColumn;

    private PropelledItem(String sprite, boolean obstacle) {
        super(sprite, obstacle);
    }

    public static PropelledItem newPropelledItem(String sprite, boolean obstacle) {
        return new PropelledItem(sprite, obstacle);
    }

    public PropelledItem setLastMoveTick(long lastMoveTick) {
        this.lastMoveTick = lastMoveTick;
        return this;
    }

    public PropelledItem previousRow(int previousRow) {
        this.previousRow = previousRow;
        return this;
    }

    public PropelledItem setPreviousPos(int row, int column) {
        previousRow = row;
        previousColumn = column;
        return this;
    }

    public long getLastMoveTick() {
        return lastMoveTick;
    }

    public int getPreviousRow() {
        return previousRow;
    }

    public int getPreviousColumn() {
        return previousColumn;
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("lastMoveTick", lastMoveTick)
                .add("previousRow", previousRow)
                .add("previousColumn", previousColumn);
    }

}
