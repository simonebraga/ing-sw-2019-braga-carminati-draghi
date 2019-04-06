package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TestPowerupPocket {

    PowerupPocket pocket;
    Powerup powerup1 = new Powerup(Color.RED,PowerupName.TARGETINGSCOPE);
    Powerup powerup2 = new Powerup(Color.BLUE,PowerupName.TARGETINGSCOPE);
    Powerup powerup3 = new Powerup(Color.RED,PowerupName.TAGBACKGRENADE);
    Powerup powerup4 = new Powerup(Color.YELLOW,PowerupName.NEWTON);

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