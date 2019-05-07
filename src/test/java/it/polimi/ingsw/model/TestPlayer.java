package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the test suite for Player class
 *
 * @author simonebraga
 */
class TestPlayer {
    Player player;

    @BeforeEach
    void setUp() {
        player = new Player(Figure.DESTRUCTOR,"nickname");
    }

    /**
     * This test checks if the addition of points works correctly
     */
    @Test
    void testPoints() {

        assertEquals(0 , player.getPoints());
        player.addPoints(8);
        assertEquals(8 , player.getPoints());
        player.addPoints(3);
        assertEquals(11 , player.getPoints());

    }

    /**
     * This test checks if the movement of the player works correctly
     */
    @Test
    void testMove() {

        assertEquals(null , player.getPosition());
        Square position = new Square(Border.WALL,Border.WALL,Border.WALL,Border.WALL);
        player.move(position);
        assertEquals(position , player.getPosition());

    }
}