package Pathfinding;

public class PathNodeComparator implements java.util.Comparator<PathNode> {
    private PathNode source;

    public PathNodeComparator(PathNode source) {
        this.source = source;
    }

    @Override
    public int compare(PathNode o1, PathNode o2) {
        if (o1.getPathDistance() < o2.getPathDistance())
            return -1;
        if (o1.getPathDistance() > o2.getPathDistance())
            return 1;
        // if they are equal distance from source, calculate their distance from target
        // close to the target first
        return Double.compare(calculateDistanceToTarget(o1), calculateDistanceToTarget(o2));

    }

    private double calculateDistanceToTarget(PathNode pathNode) {
        return Math.sqrt(
                ((source.getField().getPosition().getCol() - pathNode.getField().getPosition().getCol()) ^ 2) +
                        ((source.getField().getPosition().getRow() - pathNode.getField().getPosition().getRow()) ^ 2)
        );
    }
}