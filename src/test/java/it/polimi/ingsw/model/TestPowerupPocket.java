package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.PowerupName;
import it.polimi.ingsw.model.playerclasses.PowerupPocket;
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

        pocket.addPowerup(powerup1);


        pocket.addPowerup(powerup2);

        assertEquals(pocket.getPowerups(), new ArrayList<Powerup>(Arrays.asList(powerup1,powerup2)));

    }

    /**
     * This test checks if the removal of powerups from a pocket is performed correctly
     */
    @Test
    void testRemove() {

        pocket = new PowerupPocket();

        pocket.addPowerup(powerup1);

        pocket.addPowerup(powerup2);

        pocket.addPowerup(powerup3);

        Powerup returnPowerup = pocket.removePowerup(0);
        assertEquals(powerup1 , returnPowerup);
        assertEquals(2 , pocket.getPowerups().size());

    }
}