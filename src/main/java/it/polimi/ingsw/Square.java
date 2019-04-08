package it.polimi.ingsw;

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

    /**
     * Represents a square coordinates in lines and columns indexes.
     */
    private ArrayList<Integer> coords;

    public Square(Border up, Border down, Border left, Border right, ArrayList<Integer> coords) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.players = new ArrayList<>();
        this.coords = new ArrayList<>();
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
     * This method returns a square coordinates.
     *
     * @return an ArrayList of Integers representing a square coordinates.
     */
    public ArrayList<Integer> getCoords(){
        return coords;
    }
}
