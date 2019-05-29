package it.polimi.ingsw.model.gamelogic.actions;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.gamelogic.effectscreator.MoveCreator;
import it.polimi.ingsw.model.gamelogic.effectscreator.Targets;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.List;

/**
 * The class that builds the action of moving.
 */
public class MoveAction implements Action{

    /**
     * The maximum number of moves that can be done.
     */
    private Integer numberOfMoves;

    public MoveAction(Integer numberOfMoves) {
        this.numberOfMoves = numberOfMoves;
    }

    @Override
    public List<FunctionalEffect> run (Server server, GameTable table, Player player, Targets targets) throws IllegalActionException, UnavailableUserException {
        String question = "Do you want to move?";
        ArrayList<FunctionalEffect> effects = new ArrayList<>();

        Boolean answer = server.booleanQuestion(player, question);

        if(answer){
            effects.addAll(new MoveCreator(player, numberOfMoves, false).run(server, table, targets));
        }
        return effects;
    }
}
