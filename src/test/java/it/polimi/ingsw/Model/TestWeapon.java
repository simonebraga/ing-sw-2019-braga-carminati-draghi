package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.CardClasses.Weapon;
import it.polimi.ingsw.Model.EnumeratedClasses.Color;
import it.polimi.ingsw.Model.EnumeratedClasses.WeaponName;
import org.junit.After;
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
    private ArrayList<Color> price;

    /**
     * This attribute is a Weapon object that will be used for testing.
     */
    private Weapon shotgun;

    /**
     * This method creates all objects used to test all methods in this test suit.
     */
    @BeforeEach
    void setUp(){
        //creating unloaded shotgun
        price = new ArrayList<>();
        price.add(Color.YELLOW);
        price.add(Color.RED);
        shotgun = new Weapon(price, WeaponName.SHOTGUN,false);
    }

    /**
     * This test verifies that reload() set the isLoaded attribute to true.
     */
    @Test
    void reloadSetsIsLoadedToTrue() {

        shotgun.reload(); //method usage

        assertTrue(shotgun.getLoaded());
        tearDown();
    }

    /**
     * This method frees all objects used to test all methods in this test suit.
     */
    @After
    void tearDown(){
        price=null;
        shotgun=null;
    }
}