package it.polimi.ingsw;

/**
 * This class represents the move effect.
 *
 * @author Draghi96
 */
public class Move implements Effect{

    /**
     * This attribute is a player to be moved.
     */
    private Player player;

    /**
     * This attribute is the player destination.
     */
    private Square square;

    /**
     * This method constructs a movement object.
     *
     * @param player a Player to be moved.
     * @param square the Square destination.
     */
    public Move(Player player, Square square) {
        this.player = player;
        this.square = square;
    }

    /**
     * This method cause the move action to happen.
     */
    public void doAction(){
        this.player.move(this.square);
    }
}
