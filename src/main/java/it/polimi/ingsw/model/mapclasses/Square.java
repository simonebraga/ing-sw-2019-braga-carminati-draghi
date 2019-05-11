package it.polimi.ingsw.model.mapclasses;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.playerclasses.Player;

import java.util.ArrayList;

/**
 * Represent a single Square on the map of the game.
 */
public class Square {
    /**
     * Represents the upper border of the Square
     */
    private final Border up;
    /**
     * Represents the inferior border of the Square
     */
    private final Border down;

    /**
     * Represents the left border of the Square
     */
    private final Border left;

    /**
     * Represents the right border of the Square
     */
    private final Border right;

    /**
     * Represents all the players that are positioned in that Square
     */
    private ArrayList<Player> players;

    public Square(Border up, Border down, Border left, Border right) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.players = new ArrayList<>();
    }

    public Border getUp() {
        return up;
    }

    public Border getDown() {
        return down;
    }

    public Border getLeft() {
        return left;
    }

    public Border getRight() {
        return right;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    /**
     * Adds a new player to the list of players in the Square.
     * @param player The player that must be added to the list of players in the Square.
     */
    public void addPlayer(Player player){
        getPlayers().add(player);
    }

    /**
     * Removes the player in the parameter from the list of players in the Square.
     * @param player The player that must be removed from the list of players in the Square
     */
    public void removePlayer(Player player){
        getPlayers().remove(player);
    }

    /**
     * This method compares two Square objects and returns true if they are to be considered equals.
     *
     * @param obj a Player object to be compared with the Player object that called the method.
     * @return true if two Square objects have same Border values and same players lists.
     */
    @Override
    public boolean equals(Object obj) {
        Square square = (Square) obj;
        return square.getDown()==this.down && square.getUp()==this.up &&
                square.getLeft()==this.left && square.getRight()==this.right &&
                ((square.getPlayers()==null && this.getPlayers() == null)||square.getPlayers().equals(this.players));
    }
}
