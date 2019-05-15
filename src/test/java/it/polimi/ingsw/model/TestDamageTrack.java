package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.playerclasses.DamageTrack;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        track.addDamage(player,3);
        assertEquals(new ArrayList<Player>(Arrays.asList(player,player,player)) , track.getDamage());

        track.resetDamage();
        assertTrue(track.getDamage().size() == 0);
    }

}