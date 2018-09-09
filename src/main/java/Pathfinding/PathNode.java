package Pathfinding;

import pl.joegreen.sergeants.framework.model.Field;
import pl.joegreen.sergeants.framework.model.VisibleField;

public class PathNode {
    private PathNode parent = null;
    private Integer pathDistance = Integer.MAX_VALUE;
    private boolean settled = false;
    private Field field;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public PathNode(Field field) {
        this.field = field;
    }

    public PathNode getParent() {
        return parent;
    }

    public void setParent(PathNode parent) {
        this.parent = parent;
    }

    public Integer getPathDistance() {
        return pathDistance;
    }

    public void setPathDistance(Integer pathDistance) {
        this.pathDistance = pathDistance;
    }

    public boolean isEnemy() {
        return this.field.isVisible() && this.field.asVisibleField().isOwnedByEnemy();
    }

    public boolean isGeneral() {
        if (this.field.isVisible()) {
            VisibleField visibleField = this.field.asVisibleField();
            return visibleField.isGeneral() && visibleField.isOwnedByEnemy();
        }
        return false;
    }

    public boolean isSettled() {
        return settled;
    }

    public void setSettled(boolean settled) {
        this.settled = settled;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this.getClass().isInstance(obj)) {
            return ((PathNode)obj).getField().getPosition().getRow() == this.field.getPosition().getRow() &&
                    ((PathNode)obj).getField().getPosition().getCol() == this.field.getPosition().getCol();
        }
        return false;
    }

    @Override
    public String toString() {
        return field.getPosition() + String.format(" (Distance: %s)", this.pathDistance);
    }
}
