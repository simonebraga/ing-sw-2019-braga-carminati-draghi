package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;

public class MoveAnotherPlayerCreator extends MoveCreator {

    /**
     * The player that decides where the other player must move.
     */
    private Player decisionMaker;

    public MoveAnotherPlayerCreator(Player target, Integer distance, Boolean sameDirection, Player decisionMaker) {
        this.player = target;
        this.distance = distance;
        this.sameDirection = sameDirection;
        this.decisionMaker = decisionMaker;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Server server, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException{
        ArrayList<FunctionalEffect> effects = new ArrayList<>();
        ArrayList<Square> squares;
        Square destination;

        squares = findSquares(table);

        destination = server.chooseSquare(decisionMaker, squares);

        new FunctionalFactory().createMove(player, destination).doAction();
        return effects;
    }

}
