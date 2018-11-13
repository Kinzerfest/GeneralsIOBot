package Bots;
import Pathfinding.PathFinder;
import pl.joegreen.sergeants.framework.Actions;
import pl.joegreen.sergeants.framework.Bot;
import pl.joegreen.sergeants.framework.model.Field;
import pl.joegreen.sergeants.framework.model.GameState;
import pl.joegreen.sergeants.framework.model.Position;
import pl.joegreen.sergeants.framework.model.VisibleField;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ZestyBot implements Bot{
    private final Actions actions;
    private int totalArmies = 0;
    private int generalArmySize = 0;
    private long totalLand = 0;
    private Map<Integer, Position> generals = new HashMap<>();

    public ZestyBot(Actions actions) {
        this.actions = actions;
    }

    @Override
    public void onGameStateUpdate(GameState newGameState) {

        this.totalArmies = newGameState.getVisibleFields().stream()
                .filter(VisibleField::isOwnedByMe)
                .mapToInt(VisibleField::getArmy)
                .sum();

        this.generalArmySize = newGameState.getVisibleFields().stream()
                .filter(this::isGeneralMe)
                .findFirst().get().getArmy();

        this.totalLand = newGameState.getVisibleFields().stream()
                .filter(VisibleField::isOwnedByMe)
                .count();

        System.out.println(this);

        if (!battlesToWin(newGameState)) {
            advanceArmiesTowardsEnemy(newGameState);
        }
    }

    private void advanceArmiesTowardsEnemy(GameState newGameState) {
        VisibleField largestArmy = newGameState.getVisibleFields().stream()
                .filter(this::isOwnedByMe)
                .sorted(Comparator.comparing(VisibleField::getArmy).reversed())
                .findFirst().get();
        if (largestArmy.getArmy() <= 2)
            return;

        PathFinder pathFinder = new PathFinder(newGameState, largestArmy);

        Field generalToAttack = pathFinder.getNearestGeneral();
        if (generalToAttack != null) {
            System.out.println("STRATEGY - MOVE LARGEST ARMY to nearest GENERAL");
            makeMove(largestArmy, generalToAttack.asVisibleField());
            return;
        }

        Field fieldToAttack = pathFinder.getNearestEnemy();
        if (fieldToAttack != null) {
            System.out.println("STRATEGY - MOVE LARGEST ARMY to nearest enemy");
            makeMove(largestArmy, fieldToAttack.asVisibleField());
            return;
        }

        Field fieldToMove = pathFinder.getRandomNeighborToEmptyField();
        if (fieldToMove != null) {
            System.out.println("STRATEGY - MOVE LARGEST ARMY");
            makeMove(largestArmy, fieldToMove.asVisibleField());
        }
    }

    private boolean battlesToWin(GameState newGameState) {
        Optional<VisibleField> maybeFieldToAttack = newGameState.getVisibleFields().stream()
                .filter(this::canBeAttacked)
                .sorted(Comparator.comparing(this::enemyPriority))
                .findFirst();

        if (maybeFieldToAttack.isPresent()) {
            System.out.println("STRATEGY - EXPAND/ATTACK");
            VisibleField attackFrom = maybeFieldToAttack.get().getVisibleNeighbours().stream()
                    .filter(VisibleField::isOwnedByMe)
                    .sorted(Comparator.comparing(VisibleField::getArmy).reversed())
                    .findFirst().get();

            makeMove(attackFrom, maybeFieldToAttack.get());
            return true;
        }
        return false;
    }

    private Integer enemyPriority(VisibleField potentialTarget) {
        if (potentialTarget.isOwnedByEnemy()) {
            if (potentialTarget.isGeneral()) return -10;
            if (potentialTarget.getArmy() == 1) return -1;
            return potentialTarget.getArmy();
        }
        return 1;
    }

    private boolean canBeAttacked(VisibleField potentialTarget) {

        if (potentialTarget.isObstacle())
            return false;

        if (potentialTarget.isOwnedByMyTeam())
            return false;

        return (potentialTarget.getVisibleNeighbours().stream().anyMatch(
                neighbour -> neighbour.isOwnedByMe() && neighbour.getArmy() > potentialTarget.getArmy() + 1
        ));
    }

    private boolean isGeneralMe(VisibleField potential) {
        return potential.isGeneral() && potential.isOwnedByMe();
    }

    private boolean isGeneralNotMe(VisibleField potential) {
        return potential.isGeneral() && !potential.isOwnedByMe();
    }

    private boolean isOwnedByMe(VisibleField potential) {
        boolean includeGeneral = false;
        if (generalArmySize > totalArmies * 0.2)
            includeGeneral = true;

        if (this.isGeneralMe(potential))
            return includeGeneral;

        return potential.isOwnedByMe();
    }

    private void makeMove(VisibleField attackFrom, VisibleField fieldToAttack) {
        System.out.println("MOVE :  " + attackFrom);
        System.out.println("  TO :  " + fieldToAttack);
        actions.move(attackFrom, fieldToAttack);
    }

    @Override
    public String toString() {
        return String.format("Armies: %s, Land: %s, General: %s", this.totalArmies, this.totalLand, this.generalArmySize);
    }
}
