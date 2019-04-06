package it.polimi.ingsw;

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

        try {
            pocket.addWeapon(weapon1);
        } catch (FullPocketException e) {
            fail();
        }

        try {
            pocket.addWeapon(weapon2);
        } catch (FullPocketException e) {
            fail();
        }

        assertEquals(new ArrayList<>(Arrays.asList(weapon1,weapon2)) , pocket.getWeapons());

    }

    /**
     * This test case checks if the addition in a full pocket is handled correctly
     */
    @Test
    void testFullPocket() {

        pocket = new WeaponPocket();
        assertEquals(new ArrayList<Weapon>() , pocket.getWeapons());

        try {
            pocket.addWeapon(weapon1);
        } catch (FullPocketException e) {
            fail();
        }

        try {
            pocket.addWeapon(weapon2);
        } catch (FullPocketException e) {
            fail();
        }

        try {
            pocket.addWeapon(weapon3);
        } catch (FullPocketException e) {
            fail();
        }

        try {
            pocket.addWeapon(weapon4);
        } catch (FullPocketException e) {
            assertTrue(true);
        }
    }

    /**
     * This test checks if the switch of weapons in the pocket is performed correctly
     */
    @Test
    void testSwitchWeapon() {

        pocket = new WeaponPocket();

        try {
            pocket.addWeapon(weapon1);
        } catch (FullPocketException e) {
            fail();
        }

        try {
            pocket.addWeapon(weapon2);
        } catch (FullPocketException e) {
            fail();
        }

        try {
            pocket.addWeapon(weapon3);
        } catch (FullPocketException e) {
            fail();
        }

        Weapon weaponOut = pocket.switchWeapon(weapon4,1);
        assertEquals(weapon2 , weaponOut);
        assertEquals(3 , pocket.getWeapons().size());
    }

}