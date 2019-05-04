package it.polimi.ingsw.Model.EffectClasses;

import it.polimi.ingsw.Model.ExceptionClasses.FullPocketException;
import it.polimi.ingsw.Model.ExceptionClasses.KilledPlayerException;
import it.polimi.ingsw.Model.ExceptionClasses.KilledSpawnSquareException;
import it.polimi.ingsw.Model.ExceptionClasses.OverKilledPlayerException;

/**
 * Represents the effects that can be caused by a Player.
 */
@FunctionalInterface
public interface FunctionalEffect {
    void doAction() throws FullPocketException, KilledPlayerException, OverKilledPlayerException, KilledSpawnSquareException;
}
