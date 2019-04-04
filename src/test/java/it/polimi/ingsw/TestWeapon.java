package it.polimi.ingsw;

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
     * This test verifies that the reload() operation actually set the isLoaded attribute to true.
     */
    @Test
    void reloadSetsIsLoadedToTrue() throws WeaponAlreadyLoadedException {
        //creating unloaded shotgun
        ArrayList<Color> price = new ArrayList<>();
        price.add(Color.YELLOW);
        price.add(Color.YELLOW);
        Weapon shotgun = new Weapon(price,WeaponName.SHOTGUN,false);

        shotgun.reload(); //should reload it

        assertTrue(shotgun.getLoaded());
    }
}