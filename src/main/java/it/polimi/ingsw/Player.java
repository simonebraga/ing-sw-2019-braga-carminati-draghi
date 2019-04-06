package it.polimi.ingsw;

/**
 * This class is temporary implemented to let other classes use "Player" keyword and constructor correctly
 *
 * @author simonebraga
 */
public class Player {

    private final Figure figure;
    private final String username;

    public Player(Figure figure, String username) {
        this.figure = figure;
        this.username = username;
    }
}