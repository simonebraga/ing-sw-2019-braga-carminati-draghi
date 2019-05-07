package it.polimi.ingsw.model;

import it.polimi.ingsw.model.playerclasses.PointTrack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is the test suite for PointTrack class
 *
 * @author simonebraga
 */
import static org.junit.jupiter.api.Assertions.*;

class TestPointTrack {
    PointTrack track;

    /**
     * This test case checks if reduceValue and setValue methods work
     */
    @Test
    void testNormal() {

        track = new PointTrack(new ArrayList<Integer>(Arrays.asList(8,6,4,2,1,1)));
        track.reduceValue();
        assertEquals(track.getValue(), new ArrayList<Integer>(Arrays.asList(6,4,2,1,1)));

        track.setValue(new ArrayList<Integer>(Arrays.asList(2,1,1)));
        track.reduceValue();
        assertEquals(track.getValue(), new ArrayList<Integer>(Arrays.asList(1,1)));
    }

    /**
     * This test case checks if the attempt to reduce the value of a PointTrack with no value is handled correctly
     */
    @Test
    void testEmpty() {
        track = new PointTrack(new ArrayList<Integer>());
        track.reduceValue();
        assertEquals(track.getValue(),new ArrayList<Integer>());
    }

}