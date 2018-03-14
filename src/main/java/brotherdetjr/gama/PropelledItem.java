package brotherdetjr.gama;

import com.google.common.base.MoreObjects.ToStringHelper;

public final class PropelledItem extends DirectionalItem<PropelledItem> {

    private long currentTick;
    private long lastMoveTick;
    private int lastRow;
    private int lastColumn;

    private PropelledItem(String sprite, boolean obstacle) {
        super(sprite, obstacle);
    }

    public static PropelledItem newPropelledItem(String sprite, boolean obstacle) {
        return new PropelledItem(sprite, obstacle);
    }

    public boolean isJustMoved() {
        return currentTick - lastMoveTick < 2;
    }

    public PropelledItem currentTick(long currentTick) {
        this.currentTick = currentTick;
        return this;
    }

    @Override
    public PropelledItem place(int row, int column, Integer zIndex) {
        lastMoveTick = currentTick;
        lastRow = getRow();
        lastColumn = getColumn();
        return super.place(row, column, zIndex);
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("currentTick", currentTick)
                .add("lastMoveTick", lastMoveTick)
                .add("lastRow", lastRow)
                .add("lastColumn", lastColumn);
    }

}
