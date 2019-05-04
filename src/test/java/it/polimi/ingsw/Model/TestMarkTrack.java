package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.EnumeratedClasses.Figure;
import it.polimi.ingsw.Model.PlayerClasses.MarkTrack;
import it.polimi.ingsw.Model.PlayerClasses.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the test suite for MarkTrack class
 *
 * @author simonebraga
 */
class TestMarkTrack {
    Player player1;
    Player player2;
    MarkTrack track;

    @BeforeEach
    void setUp() {
        player1 = new Player(Figure.DOZER,"nickname");
        player2 = new Player(Figure.DESTRUCTOR,"anotherNickname");
        track = new MarkTrack();
    }

    /**
     * This test check if normal addMarks operation works correctly
     */
    @Test
    void testAddMarks() {
        track.addMarks(player1,2);
        track.addMarks(player2,1);
        assertTrue(track.getMarks(player1) == 2);
        assertTrue(track.getMarks(player2) == 1);
    }

    /**
     * This test checks if the internal handling of max limit of the marks works correctly
     */
    @Test
    void testSuperAddMarks() {
        track.addMarks(player2,14);
        assertTrue(track.getMarks(player2) == 3);
    }

    /**
     * This test checks if removeMarks operation works correctly in two cases
     * - Elimination of existing marks
     * - Elimination of non-existing marks
     */
    @Test
    void testRemoveMarks() {
        track.addMarks(player1,3);
        track.addMarks(player2,1);
        assertTrue(track.removeMarks(player1) == 3);
        assertTrue(track.removeMarks(player1) == 0);
        assertTrue(track.removeMarks(player2) == 1);
        assertTrue(track.getMarks(player2) == 0);
    }
}