package brotherdetjr.gama;

import com.google.common.base.MoreObjects.ToStringHelper;

public class DirectionalItem<T extends DirectionalItem> extends Item<T> {

    private Direction direction;

    protected DirectionalItem(String sprite, boolean obstacle) {
        super(sprite, obstacle);
    }

    public static DirectionalItem<? extends DirectionalItem> newDirectionalItem(String sprite, boolean obstacle) {
        return new DirectionalItem<>(sprite, obstacle);
    }

    public Direction getDirection() {
        return direction;
    }

    @SuppressWarnings("unchecked")
    public T pointTo(Direction direction) {
        this.direction = direction;
        return (T) this;
    }

    @Override
    protected ToStringHelper toStringHelper() {
        return super.toStringHelper().add("direction", direction);
    }
}
