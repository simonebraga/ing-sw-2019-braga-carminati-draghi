package it.polimi.ingsw.model.mapclasses;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.playerclasses.Player;

import java.util.ArrayList;

@JsonTypeInfo(  use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.PROPERTY,
                property = "type")

@JsonSubTypes({
        @JsonSubTypes.Type(value = TileSquare.class, name = "TileSquare"),
        @JsonSubTypes.Type(value = SpawnSquare.class, name = "SpawnSquare"),
        @JsonSubTypes.Type(value = DominationSpawnSquare.class, name = "DominationSpawnSquare")
        })

/**
 * Represent a single Square on the map of the game.
 */
public class Square{
    /**
     * Represents the upper border of the Square
     */
    private Border up;
    /**
     * Represents the inferior border of the Square
     */
    private Border down;

    /**
     * Represents the left border of the Square
     */
    private Border left;

    /**
     * Represents the right border of the Square
     */
    private Border right;

    /**
     * Represents all the players that are positioned in that Square
     */
    private ArrayList<Player> players;

    /**
     * Represents the x coordinate of the square.
     */
    private Integer x;

    /**
     * Represents the y coordinate of the square.
     */
    private Integer y;

    /**
     * This constructor initializes all attributes to null.
     */
    public Square() {
        this.right=null;
        this.left=null;
        this.up=null;
        this.down=null;
        this.players=new ArrayList<>();
        this.x = null;
        this.y = null;
    }

    public Square(Border up, Border down, Border left, Border right, Integer x, Integer y) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        this.players = new ArrayList<>();
        this.x = x;
        this.y = y;
    }

    /**
     * This method sets the up attribute to a new value.
     *
     * @param up a new Border value for up attribute.
     */
    public void setUp(Border up) {this.up=up;}

    /**
     * This method sets the down attribute to a new value.
     *
     * @param down a new Border value for down attribute.
     */
    public void setDown(Border down) {this.down=down;}

    /**
     * This method sets the left attribute to a new value.
     *
     * @param left a new Border value for left attribute.
     */
    public void setLeft(Border left) {this.left=left;}

    /**
     * This method sets the right attribute to a new value.
     *
     * @param right a new Border value for right attribute.
     */
    public void setRight(Border right) {this.right=right;}

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
        return this.x.equals(square.getX()) && this.y.equals(square.getY()) && this.players.equals(square.getPlayers());
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }
}
