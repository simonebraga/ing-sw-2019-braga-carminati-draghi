package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test suit packs all tests relatives to the SpawnSquare class methods.
 *
 * @author Draghi96
 */
public class TestSpawnSquare {

    /**
     * This attributes are the SpawnSquare object used for the tests.
     */
    SpawnSquare spawnSquare1, spawnSquare2;

    /**
     * This method sets up all objects that will be used for this test suit.
     */
    @BeforeEach
    void setUp() {
        spawnSquare1 = new SpawnSquare(Border.WALL,Border.WALL,Border.WALL,Border.WALL,0,0, Color.RED);
        spawnSquare2 = new SpawnSquare(Border.WALL,Border.WALL,Border.WALL,Border.WALL,0,0, Color.RED);
    }

    /**
     * This method tests if the equals() method has been implemented correctly.
     */
    @Test
    void testEquals() {
        assertTrue(spawnSquare1.equals(spawnSquare2));
        assertTrue(spawnSquare1.equals(spawnSquare1));
        spawnSquare1.addPlayer(new Player(Figure.DOZER,"User1"));
        assertFalse(spawnSquare1.equals(spawnSquare2));
        spawnSquare2.addPlayer(new Player(Figure.DOZER,"User1"));
        assertTrue(spawnSquare1.equals(spawnSquare2));
        tearDown();
    }

    /**
     * This method frees all objects used for these tests.
     */
    @AfterEach
    void tearDown() {
        spawnSquare1 = null;
        spawnSquare2 = null;
    }
}
