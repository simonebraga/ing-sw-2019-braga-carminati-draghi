package it.polimi.ingsw;

/**
 * Represents the effects that can be caused by a Player.
 */
@FunctionalInterface
interface FunctionalEffect {
    void doAction() throws FullPocketException;
}
