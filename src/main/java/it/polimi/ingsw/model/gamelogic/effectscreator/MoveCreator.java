package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Sets and creates the moving effect
 */
public class MoveCreator implements EffectsCreator{
    /**
     * The player that moves.
     */
    private Player player;

    /**
     * The maximum distance where a player can move.
     * <p>If this attribute is set destination -1, the player can move everywhere.</p>
     */
    private Integer distance;

    /**
     * Represents if the player must move only in a direction or not.
     */
    private Boolean sameDirection;

    public MoveCreator() {
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Default constructor. Sets all the attributes except the destination.
     */
    public MoveCreator(Player target, Integer distance, Boolean sameDirection) {
        this.player = target;
        this.distance = distance;
        this.sameDirection = sameDirection;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Controller controller, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<FunctionalEffect> effects = new ArrayList<>();
        ArrayList<Square> squares;
        Square destination;

        squares = findSquares(table);

        destination = controller.chooseSquare(player, squares);

        new FunctionalFactory().createMove(player, destination).doAction();
        return effects;
    }

    private ArrayList<Square> findSquares(GameTable table) throws IllegalActionException {
        ArrayList<Square> squares;
        if(distance<0){
            squares = new ArrayList<>(table.getGameMap().getSpawnSquares());
            squares.addAll(table.getGameMap().getTileSquares());
        }else {
            if (distance == 1) {
                squares = new ArrayList<>(table.getGameMap().getDistance(player.getPosition(), distance));
            } else {
                squares = new ArrayList<>(table.getGameMap().getRange(player.getPosition(), distance));
            }
        }

        if(squares.isEmpty()){
            throw new IllegalActionException();
        }

        if(sameDirection){
            squares = (ArrayList<Square>) squares.stream().filter(square ->
                    table.getGameMap().getCoord(square).get(0) == table.getGameMap().getCoord(player.getPosition()).get(0)
                            || table.getGameMap().getCoord(square).get(1) == table.getGameMap().getCoord(player.getPosition()).get(1))
                    .collect(Collectors.toList());
        }

        return squares;
    }

    public Player getPlayer() {
        return player;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Boolean getSameDirection() {
        return sameDirection;
    }

    public void setSameDirection(Boolean sameDirection) {
        this.sameDirection = sameDirection;
    }
}
