package Bots;

import pl.joegreen.sergeants.framework.Actions;
import pl.joegreen.sergeants.framework.Bot;
import pl.joegreen.sergeants.framework.model.GameState;
import pl.joegreen.sergeants.framework.model.VisibleField;

import java.util.Comparator;
import java.util.Optional;

public class DegenerateBot implements Bot {
    private final Actions actions;

    public DegenerateBot(Actions actions) {
        this.actions = actions;
    }

    @Override
    public void onGameStateUpdate(GameState newGameState) {
        Optional<VisibleField> maybeFieldToAttack = newGameState.getVisibleFields().stream()
                .filter(this::canBeAttacked)
                .findFirst();

        maybeFieldToAttack.ifPresent(fieldToAttack -> {
            VisibleField attackFrom = fieldToAttack.getVisibleNeighbours().stream()
                    .filter(VisibleField::isOwnedByMe)
                    .sorted(Comparator.comparing(VisibleField::getArmy).reversed())
                    .findFirst().get();

            actions.move(attackFrom, fieldToAttack);
        });


    }

    private boolean canBeAttacked(VisibleField potentialTarget) {
        return !potentialTarget.isObstacle() && !potentialTarget.isOwnedByMyTeam() &&
                potentialTarget.getVisibleNeighbours().stream().anyMatch(
                        neighbour -> neighbour.isOwnedByMe() &&
                                neighbour.getArmy() > potentialTarget.getArmy() + 1
                );
    }

}
