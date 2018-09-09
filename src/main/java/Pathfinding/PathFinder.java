package Pathfinding;
import pl.joegreen.sergeants.framework.model.*;
import java.util.*;

public class PathFinder {
    private PriorityQueue<PathNode> queue;
    private Map<Position, PathNode> pathNodes = new HashMap<>();

    private GameState gameState;
    private PathNode source;

    public PathFinder(GameState gameState, Field source) {

        this.gameState = gameState;
        this.source = new PathNode(source);

        //  using a priority queue gets a better way to choose the right neighbor to select next
        this.queue = new PriorityQueue<>(10, new PathNodeComparator(this.source));
        loadGameNodes();
        calculateDistances();
    }

    private void loadGameNodes() {
        for (Map.Entry<Position, Field> entry : gameState.getFieldsMap().entrySet()) {
            this.pathNodes.put(entry.getKey(), new PathNode(entry.getValue()));
        }
    }

    private void calculateDistances() {
        this.source.setPathDistance(0);
        this.queue.offer(this.source);

        while (this.queue.size() != 0) {
            PathNode vertex = this.queue.poll();
            addDistanceToAdjacentNodes(vertex);
        }
    }

    private void addDistanceToAdjacentNodes(PathNode vertex) {
        Field workingField = vertex.getField();
        for (Field neighborField : workingField.getNeighbours()) {
            PathNode neighborNode = this.pathNodes.get(neighborField.getPosition());
            if (neighborNode.isSettled()) {
                continue;
            }

            neighborNode.setSettled(true);
            if (neighborField.isVisible() && neighborField.asVisibleField().isCity()) {
                continue;
            }

            if (neighborField.isVisible() && neighborField.isObstacle())
                continue;

            neighborNode.setParent(vertex);
            neighborNode.setPathDistance(vertex.getPathDistance() + 1);
            this.queue.offer(neighborNode);

        }
    }

    private PathNode findPathToSourceFirstStep(PathNode targetNode) {
        while (targetNode.getPathDistance() + 1 > this.source.getPathDistance()) {
            if (targetNode.getParent().equals(this.source)) {
                return targetNode;
            }
            targetNode = targetNode.getParent();
        }
        return targetNode;
    }

    private boolean isBorderNode(Field field) {
        if (!field.isVisible())
            return false;

        VisibleField visibleField = field.asVisibleField();
        if (!visibleField.isBlank())
            return false;

        return visibleField.getVisibleNeighbours().stream().anyMatch(
                VisibleField::isOwnedByMe
        );
    }

    private Field findTargetsFirstStepByField(Optional<Field> targetField) {
        if (!targetField.isPresent())
            return null;

        Field borderField = targetField.get();

        PathNode pathNode = pathNodes.get(borderField.getPosition());
        PathNode stepNode = findPathToSourceFirstStep(pathNode);
        if (stepNode == null)
            return null;

        return stepNode.getField();
    }

    private Field findTargetsFirstStepByPathNode(Optional<PathNode> targetField) {
        if (!targetField.isPresent())
            return null;

        PathNode pathNode = targetField.get();
        PathNode stepNode = findPathToSourceFirstStep(pathNode);
        if (stepNode == null)
            return null;

        return stepNode.getField();
    }

    public Field getRandomNeighborToEmptyField() {
        // filter on ANY border node
        Optional<Field> borderNode = gameState.getFields().stream()
                .filter(this::isBorderNode)
                .findAny();

        return findTargetsFirstStepByField(borderNode);
    }

    public Field getNearestEnemy() {
        Optional<PathNode> nearestEnemy = pathNodes.values().stream()
                .filter(PathNode::isEnemy).min(Comparator.comparing(PathNode::getPathDistance));

        return findTargetsFirstStepByPathNode(nearestEnemy);
    }

    public Field getNearestGeneral() {
        Optional<PathNode> nearestGeneral = pathNodes.values().stream()
                .filter(PathNode::isGeneral).min(Comparator.comparing(PathNode::getPathDistance));

        return findTargetsFirstStepByPathNode(nearestGeneral);
    }
}
