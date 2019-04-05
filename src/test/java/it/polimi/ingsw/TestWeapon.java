package it.polimi.ingsw;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test suit verifies all Weapon class methods.
 *
 * @author Draghi96
 */
class TestWeapon {

    /**
     * This attribute is a list of Color that represent a price for a weapon that will be used for testing.
     */
    ArrayList<Color> price;

    /**
     * This attribute is a Weapon object that will be used for testing.
     */
    Weapon shotgun;

    /**
     * This method creates all objects used to test all methods in this test suit.
     */
    @BeforeEach
    void setUp(){
        //creating unloaded shotgun
        price = new ArrayList<>();
        price.add(Color.YELLOW);
        price.add(Color.RED);
        shotgun = new Weapon(price,WeaponName.SHOTGUN,false);
    }

    /**
     * This test verifies that the reload() operation set the isLoaded attribute to true.
     */
    @Test
    void reloadSetsIsLoadedToTrue() throws WeaponAlreadyLoadedException {

        shotgun.reload(); //method usage

        assertTrue(shotgun.getLoaded());
    }

    /**
     * This method frees all objects used to test all methods in this test suit.
     */
    @AfterAll
    void tearDown(){
        price=null;
        shotgun=null;
    }
}