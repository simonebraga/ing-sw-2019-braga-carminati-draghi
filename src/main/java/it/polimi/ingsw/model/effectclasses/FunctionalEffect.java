package it.polimi.ingsw.model.effectclasses;

import it.polimi.ingsw.model.exceptionclasses.FullPocketException;
import it.polimi.ingsw.model.exceptionclasses.KilledPlayerException;
import it.polimi.ingsw.model.exceptionclasses.KilledSpawnSquareException;
import it.polimi.ingsw.model.exceptionclasses.OverKilledPlayerException;

/**
 * Represents the effects that can be caused by a Player.
 */
@FunctionalInterface
public interface FunctionalEffect {
    void doAction() throws FullPocketException, KilledPlayerException, OverKilledPlayerException, KilledSpawnSquareException;
}
