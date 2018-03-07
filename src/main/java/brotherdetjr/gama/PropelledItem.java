package brotherdetjr.gama;

public final class PropelledItem extends DirectionalItem {

    private boolean justMoved;

    public PropelledItem(String sprite, boolean obstacle) {
        super(sprite, obstacle);
    }

    public boolean isJustMoved() {
        return justMoved;
    }

    public void setJustMoved(boolean justMoved) {
        this.justMoved = justMoved;
    }
}
