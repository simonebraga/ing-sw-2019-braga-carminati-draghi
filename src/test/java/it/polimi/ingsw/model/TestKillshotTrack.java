package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.exceptionclasses.FrenzyModeException;
import it.polimi.ingsw.model.playerclasses.KillshotTrack;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the test suite for KillshotTrack class
 *
 * @author simonebraga
 */
class TestKillshotTrack {

    KillshotTrack track;
    Player player1 = new Player(Figure.DESTRUCTOR,"nickname1");
    Player player2 = new Player(Figure.DOZER,"nickname2");

    /**
     * This test case checks if killshot track is updated correctly in a standard situation
     */
    @Test
    void testNormalKillAndOverkill() {

        track = new KillshotTrack(8, new ArrayList<Integer>(Arrays.asList(8,6,4,2,1,1)));
        try {
            track.kill(player1);
        } catch (FrenzyModeException e) {
            fail();
        }
        track.overKill(player1);
        try {
            track.kill(player2);
        } catch (FrenzyModeException e) {
            fail();
        }
        assertEquals(new ArrayList<>(Arrays.asList(player1,player1,player2)) , track.getKillTrack());
        assertEquals(6 , track.getKillCount());

    }

    /**
     * This test case checks if FrenzyModeException is thrown correctly
     */
    @Test
    void testFrenzyException() {

        track = new KillshotTrack(2, new ArrayList<Integer>(Arrays.asList(8,6,4,2,1,1)));
        try {
            track.kill(player1);
        } catch (FrenzyModeException e) {
            fail();
        }
        track.overKill(player1);
        try {
            track.kill(player2);
        } catch (FrenzyModeException e) {
            assertTrue(true);
        }
        assertEquals(new ArrayList<>(Arrays.asList(player1,player1,player2)) , track.getKillTrack());
        assertEquals(0 , track.getKillCount());

    }

}