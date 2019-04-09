package it.polimi.ingsw;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the switchWeapon method: it must add correctly the weapon, remove correctly the weapon and the other weapons must not be changed.
 */
class TestSpawnSquareSwitchWeapon {
    SpawnSquare square;
    Weapon weaponToAdd;
    Weapon weaponToRemove;
    ArrayList<Weapon> weapons;

    @BeforeEach
    void setUp() {
        weapons = new ArrayList<>();
        square = new SpawnSquare(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING, Color.BLUE);
        weaponToAdd = new Weapon(new ArrayList<Color>(), WeaponName.CYBERBLADE, Boolean.TRUE);
        weaponToRemove = new Weapon(new ArrayList<Color>(), WeaponName.TRACTORBEAM, Boolean.TRUE);
        weapons.add(weaponToRemove);
        weapons.add(new Weapon(new ArrayList<Color>(), WeaponName.HEATSEEKER, Boolean.TRUE));
        weapons.add(new Weapon(new ArrayList<Color>(), WeaponName.MACHINEGUN, Boolean.TRUE));
        square.setWeapons((ArrayList<Weapon>) weapons.clone());
        weapons.add(weaponToAdd);
        weapons.remove(weaponToRemove);
    }

    @Test
    void switchWeapon() {
        Weapon weaponThatHasBeenRemoved = square.switchWeapon(weaponToRemove, weaponToAdd);
        assertTrue(weapons.containsAll(square.getWeapons()) && square.getWeapons().containsAll(weapons));
        assertSame(weaponThatHasBeenRemoved, weaponToRemove);
    }

    @AfterEach
    void tearDown() {
        square = null;
        weaponToAdd = null;
        weaponToRemove = null;
        weapons = null;
    }
}