package it.polimi.ingsw.model.gamelogic.actions;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.gamelogic.effectscreator.Targets;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.List;

/**
 * Represents all the possible actions that can be done in a turn.
 */
public interface Action {
    List<FunctionalEffect> run(Server server, GameTable table, Player player, Targets targets) throws IllegalActionException, UnavailableUserException;
}
