package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.mapclasses.TileSquare;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test suit verifies that all TileSquare class method work correctly.
 *
 * @author Draghi96
 */
public class TestTileSquare {

    /**
     * This attribute is the TileSquare that will be tested.
     */
    private TileSquare tileSquare;

    /**
     * This method creates all objects that will be used for testing.
     */
    @BeforeEach
    void setUp() {
        tileSquare = new TileSquare(Border.WALL,Border.WALL,Border.WALL,Border.WALL, 0, 0);
    }

    /**
     * This test verifies that equals() method compares two TileSquare objects correctly.
     */
    @Test
    void testEquals() {
        TileSquare tileSquare1 = new TileSquare(Border.WALL,Border.WALL,Border.WALL,Border.WALL,0,0);
        assertTrue(tileSquare.equals(tileSquare));
        assertTrue(tileSquare1.equals(tileSquare));
        TileSquare tileSquare2 = new TileSquare(Border.WALL,Border.WALL,Border.WALL,Border.NOTHING,0,1);
        assertFalse(tileSquare.equals(tileSquare2));
    }

    /**
     * This method frees all objects used in this test suit.
     */
    @AfterEach
    void tearDown() {
        tileSquare = null;
    }
}
