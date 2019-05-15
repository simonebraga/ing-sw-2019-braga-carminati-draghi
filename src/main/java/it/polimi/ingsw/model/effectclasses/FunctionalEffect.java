package it.polimi.ingsw.model.effectclasses;

import it.polimi.ingsw.model.exceptionclasses.KilledPlayerException;
import it.polimi.ingsw.model.exceptionclasses.KilledSpawnSquareException;

/**
 * Represents the effects that can be caused by a Player.
 */
@FunctionalInterface
public interface FunctionalEffect {
    void doAction() throws KilledPlayerException, KilledSpawnSquareException;
}
