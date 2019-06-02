package it.polimi.ingsw.model.gamelogic.actions;

import it.polimi.ingsw.model.gamelogic.turn.MessageRetriever;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.gamelogic.effectscreator.Targets;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represent one action in a turn.
 */
public class ActionManager {

    /**
     * The player that is doing the action
     */
    private Player player;

    /**
     * Represents if the action happens during the final frenzy.
     */
    private Boolean finalFrenzy;

    /**
     * Represents if the action happens before or after the final player.
     */
    private Boolean beforeFirstPlayer;

    public ActionManager(Player player, Boolean finalFrenzy, Boolean beforeFirstPlayer) {
        this.player = player;
        this.finalFrenzy = finalFrenzy;
        this.beforeFirstPlayer = beforeFirstPlayer;
    }

    /**
     * The method that creates and executes an action.
     * @return A boolean value that represents if the action has been successfully completed or not. If the player disconnects during the action, the method will return true because the action has been completed.
     */
    public Boolean runAction(Server server, GameTable table, Targets targets) {


        final String MOVE = new MessageRetriever().retrieveMessage("move");
        final String SHOOT = new MessageRetriever().retrieveMessage("shoot");
        final String MOVE_AND_SHOOT = new MessageRetriever().retrieveMessage("moveAndShoot");
        final String MOVE_RELOAD_AND_SHOOT = new MessageRetriever().retrieveMessage("moveReloadAndShoot");
        final String GRAB = new MessageRetriever().retrieveMessage("grab");



        ArrayList<String> possibleActions = new ArrayList<>();
        Map<Player, Square> initialSituation = sandboxInitialize(table);
        ArrayList<Action> actions = new ArrayList<>();
        ArrayList<FunctionalEffect> effects = new ArrayList<>();

        possibleActions.add(GRAB);
        if(finalFrenzy){
            possibleActions.add(MOVE_RELOAD_AND_SHOOT);
            if(beforeFirstPlayer) {
                possibleActions.add(MOVE);
            }
        }else{
            possibleActions.add(MOVE);
            if (player.getDamageTrack().getDamage().size() >= 6) {
                possibleActions.add(MOVE_AND_SHOOT);
            } else {
                possibleActions.add(SHOOT);
            }
        }

        String choice = null;
        try {
            choice = server.chooseString(player, possibleActions);
        } catch (UnavailableUserException e) {
            return false;
        }

        int numberOfMoves;
        if(choice.equals(MOVE)) {
            if (finalFrenzy) {
                numberOfMoves = 4;
            } else {
                numberOfMoves = 3;
            }
            actions.add(new MoveAction(numberOfMoves));
        }
        if(choice.equals(SHOOT)) {
            actions.add(new ShootAction());
        }
        if(choice.equals(MOVE_AND_SHOOT)) {
            numberOfMoves = 1;
            actions.add(new MoveAction(numberOfMoves));
            actions.add(new ShootAction());
        }
        if(choice.equals(MOVE_RELOAD_AND_SHOOT)) {
            if (beforeFirstPlayer) {
                numberOfMoves = 1;
            } else {
                numberOfMoves = 2;
            }
            actions.add(new MoveAction(numberOfMoves));
            actions.add(new ReloadAction());
            actions.add(new ShootAction());
        }
        if(choice.equals(GRAB)) {
            if (finalFrenzy) {
                if (beforeFirstPlayer) {
                    numberOfMoves = 2;
                } else {
                    numberOfMoves = 3;
                }
            } else {
                numberOfMoves = 1;
            }
            actions.add(new MoveAction(numberOfMoves));
            actions.add(new GrabAction());
        }

        for (Action action : actions) {
            try {
                effects.addAll(action.run(server, table, player, targets));
            } catch (IllegalActionException | UnavailableUserException e) {
                initialSituation.keySet().forEach(player1 -> {
                    if(player1.getPosition()!=null) { //See if the player has spawned (first turns)
                        new FunctionalFactory().createMove(player1, initialSituation.get(player1)).doAction();
                    }
                });
                return false;
            }
        }

        effects.forEach(FunctionalEffect::doAction);


        //Use targeting scope
        effects = new ArrayList<>();
        boolean nextTarget;
        for (Player target : targets.getPlayersDamaged()) {
            if (server.isConnected(player)) {
                do {
                    try {
                        effects.addAll(new PowerUpAction().targetingScopeUse(server, table, player, target));
                        nextTarget = true;
                    } catch (IllegalActionException e) {
                        nextTarget = false;
                    } catch (UnavailableUserException e) {
                        nextTarget = true;
                    }
                } while (nextTarget);
            }
        }
        effects.forEach(FunctionalEffect::doAction);

        //Use tagBack Grenade
        for (Player target : targets.getPlayersDamaged()) {
            if(target.getDamageTrack().getDamage().size()<11 && server.isConnected(target)){ //Makes sure that the player is alive and connected
                try {
                    effects.addAll(new PowerUpAction().tagBackGrenadeUse(server, table, target, player));
                } catch (UnavailableUserException e) {
                }
            }
        }
        effects.forEach(FunctionalEffect::doAction);

        return true;
    }

    private  Map<Player, Square> sandboxInitialize(GameTable table){
        Map<Player, Square> map = new HashMap<>();
        table.getPlayers().forEach(playerToMove -> map.put(playerToMove, playerToMove.getPosition()));
        return map;
    }
}
