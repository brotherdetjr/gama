package brotherdetjr.gama;

import com.google.common.base.MoreObjects.ToStringHelper;

public final class PropelledItem extends DirectionalItem<PropelledItem> {

    private boolean justMoved;

    private PropelledItem(String sprite, boolean obstacle) {
        super(sprite, obstacle);
    }

    public static PropelledItem newPropelledItem(String sprite, boolean obstacle) {
        return new PropelledItem(sprite, obstacle);
    }

    public boolean isJustMoved() {
        return justMoved;
    }

    public PropelledItem markJustMoved() {
        this.justMoved = true;
        return this;
    }

    public PropelledItem unmarkJustMoved() {
        this.justMoved = false;
        return this;
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper().add("justMoved", justMoved);
    }

}
