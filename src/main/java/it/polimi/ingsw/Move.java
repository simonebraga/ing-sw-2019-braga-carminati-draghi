package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * This class represent a movement on the game map.
 *
 * @author Draghi96
 */
public class Move {

    /**
     * This method implements the movement.
     * <p>This method doesn't check if the movement is legal therefore it doesn't throw any exception.
     * All rules checks will be executed by the controller.</p>
     *
     * @param player a Player object that needs to be moved.
     * @param square a Square object that is the player destination.
     */
    void doAction(Player player, Square square) {
        player.setPosition(square);
    }

    /**
     * This method implements the movement.
     * <p>This method doesn't check if the movement is legal therefore it doesn't throw any exception.
     * All rules checks will be executed by the controller.</p>
     *
     * @param player a Player object that needs to be moved.
     * @param coordinates an ArrayList of two integers that represent the destination square coordinates.
     * @param map a GameMap object that includes all squares coordinates.
     */
    void doAction(Player player, ArrayList<Integer> coordinates, GameMap map){
        Player.setPosition(map.getSquare(coordinates.get(0),coordinates.get(1)));
    }
}
