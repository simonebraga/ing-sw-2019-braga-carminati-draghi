package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This test suit verifies that all Square methods work correctly.
 */
import static org.junit.jupiter.api.Assertions.*;

public class TestSquare {

    /**
     * This attributes are Square objects to be tested.
     */
    private Square square1,square2;

    /**
     * This method sets up all object used for testing.
     */
    @BeforeEach
    void setUp() {
        square1 = new Square(Border.WALL,Border.WALL,Border.WALL,Border.WALL,0,0);
        square2 = new Square(Border.WALL,Border.WALL,Border.WALL,Border.WALL,0,0);
    }

    /**
     * This test verifies that equals() compares two Player objects correctly.
     */
    @Test
    void testEquals() {
        assertTrue(square1.equals(square2));
        assertTrue(square2.equals(square1));
    }

    /**
     * This method frees all objects used in this test suit.
     */
    @AfterEach
    void tearDown() {
        square1=null;
        square2=null;
    }
}
