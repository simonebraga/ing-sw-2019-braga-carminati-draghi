package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the test suite for StartingPlayerMarker class
 *
 * @author simonebraga
 */
class TestStartingPlayerMarker {

    StartingPlayerMarker marker;
    Player player1 = new Player(Figure.DESTRUCTOR,"nickname1");
    Player player2 = new Player(Figure.DOZER,"nickname2");

    /**
     * This test case checks if singleton class works correctly
     */
    @Test
    void testSingleton() {

        marker = StartingPlayerMarker.getInstance();
        marker.setTarget(player1);
        assertEquals(player1 , marker.getTarget());

        StartingPlayerMarker.getInstance().setTarget(player2);
        assertEquals(player2 , StartingPlayerMarker.getInstance().getTarget());

    }

}