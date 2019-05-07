package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.playerclasses.DoubleKillCounter;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the test suite for DoubleKillCounter class
 *
 * @author simonebraga
 */
class TestDoubleKillCounter {

    DoubleKillCounter track;
    Player player1 = new Player(Figure.DESTRUCTOR,"nickname1");
    Player player2 = new Player(Figure.DOZER,"nickname2");

    /**
     * This test case checks if players are added correctly to the double killers list
     */
    @Test
    void testAddition() {

        track = new DoubleKillCounter(1);
        track.add(player2);
        track.add(player1);
        assertEquals(new ArrayList<>(Arrays.asList(player2,player1)) , track.getList());

    }

}