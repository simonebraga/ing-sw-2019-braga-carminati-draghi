package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.CardClasses.Powerup;
import it.polimi.ingsw.Model.EnumeratedClasses.Color;
import it.polimi.ingsw.Model.EnumeratedClasses.PowerupName;
import it.polimi.ingsw.Model.ExceptionClasses.FullPocketException;
import it.polimi.ingsw.Model.PlayerClasses.PowerupPocket;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the test suite for PowerupPocket class
 *
 * @author simonebraga
 */
class TestPowerupPocket {

    PowerupPocket pocket;
    Powerup powerup1 = new Powerup(Color.RED, PowerupName.TARGETINGSCOPE);
    Powerup powerup2 = new Powerup(Color.BLUE,PowerupName.TARGETINGSCOPE);
    Powerup powerup3 = new Powerup(Color.RED,PowerupName.TAGBACKGRENADE);
    Powerup powerup4 = new Powerup(Color.YELLOW,PowerupName.NEWTON);

    /**
     * This test checks if the normal addition of powerups is performed correctly
     */
    @Test
    void testNormal() {

        pocket = new PowerupPocket();

        try {
            pocket.addPowerup(powerup1);
        } catch (FullPocketException e) {
            fail();
        }

        try {
            pocket.addPowerup(powerup2);
        } catch (FullPocketException e) {
            fail();
        }

        assertEquals(pocket.getPowerups(), new ArrayList<Powerup>(Arrays.asList(powerup1,powerup2)));

    }

    /**
     * This test checks if the removal of powerups from a pocket is performed correctly
     */
    @Test
    void testRemove() {

        pocket = new PowerupPocket();

        try {
            pocket.addPowerup(powerup1);
        } catch (FullPocketException e) {
            fail();
        }

        try {
            pocket.addPowerup(powerup2);
        } catch (FullPocketException e) {
            fail();
        }

        try {
            pocket.addPowerup(powerup3);
        } catch (FullPocketException e) {
            fail();
        }

        Powerup returnPowerup = pocket.removePowerup(0);
        assertEquals(powerup1 , returnPowerup);
        assertEquals(2 , pocket.getPowerups().size());

    }

    /**
     * This test checks if the addition of powerups in a full pocket is handled correctly
     */
    @Test
    void testFullPocket() {

        pocket = new PowerupPocket();

        try {
            pocket.addPowerup(powerup1);
        } catch (FullPocketException e) {
            fail();
        }

        try {
            pocket.addPowerup(powerup2);
        } catch (FullPocketException e) {
            fail();
        }

        try {
            pocket.addPowerup(powerup3);
        } catch (FullPocketException e) {
            fail();
        }

        try {
            pocket.addPowerup(powerup4);
        } catch (FullPocketException e) {
            assertTrue(true);
        }

    }

}