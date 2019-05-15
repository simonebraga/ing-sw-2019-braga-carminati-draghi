package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.playerclasses.WeaponPocket;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the test suite for WeaponPocket class
 *
 * @author simonebraga
 */
class TestWeaponPocket {
    WeaponPocket pocket;
    Weapon weapon1 = new Weapon(new ArrayList<Color>(Arrays.asList(Color.RED,Color.BLUE)) , WeaponName.THOR , true);
    Weapon weapon2 = new Weapon(new ArrayList<Color>(Arrays.asList(Color.RED,Color.BLUE)) , WeaponName.WHISPER , true);
    Weapon weapon3 = new Weapon(new ArrayList<Color>(Arrays.asList(Color.RED,Color.BLUE)) , WeaponName.HEATSEEKER , true);
    Weapon weapon4 = new Weapon(new ArrayList<Color>(Arrays.asList(Color.RED,Color.BLUE)) , WeaponName.ROCKETLAUNCHER , true);

    /**
     * This test case checks if normal additions of weapons is performed correctly
     */
    @Test
    void testNormal() {

        pocket = new WeaponPocket();
        assertEquals(new ArrayList<Weapon>() , pocket.getWeapons());

        pocket.addWeapon(weapon1);

        pocket.addWeapon(weapon2);


        assertEquals(new ArrayList<>(Arrays.asList(weapon1,weapon2)) , pocket.getWeapons());

    }

    /**
     * This test checks if the switch of weapons in the pocket is performed correctly
     */
    @Test
    void testSwitchWeapon() {

        pocket = new WeaponPocket();

        pocket.addWeapon(weapon1);

        pocket.addWeapon(weapon2);

        pocket.addWeapon(weapon3);

        Weapon weaponOut = pocket.switchWeapon(weapon4,1);
        assertEquals(weapon2 , weaponOut);
        assertEquals(3 , pocket.getWeapons().size());
    }

}