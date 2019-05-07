package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.exceptionclasses.KilledPlayerException;
import it.polimi.ingsw.model.exceptionclasses.OverKilledPlayerException;
import it.polimi.ingsw.model.playerclasses.DamageTrack;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the test suite for DamageTrack class
 *
 * @author simonebraga
 */
class TestDamageTrack {

    Player player;
    DamageTrack track;

    @BeforeEach
    void setUp() {
        player = new Player(Figure.DOZER,"nickname");
        track = new DamageTrack();
    }

    /**
     * This test checks addDamage and resetDamage methods
     */
    @Test
    void testReset() {
        try {
            track.addDamage(player,3);
        } catch (KilledPlayerException e){
            fail();
        } catch (OverKilledPlayerException e) {
            fail();
        }
        assertEquals(new ArrayList<Player>(Arrays.asList(player,player,player)) , track.getDamage());

        track.resetDamage();
        assertTrue(track.getDamage().size() == 0);
    }

    /**
     * This test checks if exceptions are thrown correctly
     */
    @Test
    void testExceptions() {
        try {
            track.addDamage(player,11);
        } catch (KilledPlayerException e) {
            assertTrue(true);
        } catch (OverKilledPlayerException e) {
            fail();
        }

        try {
            track.addDamage(player,1);
        } catch (KilledPlayerException e) {
            fail();
        } catch (OverKilledPlayerException e) {
            assertTrue(true);
        }
    }

}