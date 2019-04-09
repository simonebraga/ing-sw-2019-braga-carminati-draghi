package it.polimi.ingsw;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the method takeWeapon: it has to remove the target Weapon and must return it, also the other Weapons must not change
 */
class TestSpawnSquareTakeWeapon {

    SpawnSquare square;
    Weapon weaponToRemove;
    ArrayList<Weapon> weapons;

    @BeforeEach
    void setUp() {
        square = new SpawnSquare(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING, Color.BLUE);
        weapons = new ArrayList<>();
        weaponToRemove = new Weapon(new ArrayList<Color>(), WeaponName.ELECTROSCYTHE, Boolean.TRUE);
        weapons.add(new Weapon(new ArrayList<Color>(), WeaponName.FURNACE, Boolean.TRUE));
        weapons.add(new Weapon(new ArrayList<Color>(), WeaponName.SHOCKWAVE, Boolean.TRUE));
        weapons.add(weaponToRemove);
        square.setWeapons((ArrayList<Weapon>) weapons.clone());
        weapons.remove(weaponToRemove);

    }

    @Test
    void takeWeapon() {
        Weapon weaponThatHasBeenRemoved = square.takeWeapon(weaponToRemove);
        assertTrue(weapons.containsAll(square.getWeapons()) && square.getWeapons().containsAll(weapons));
        assertSame(weaponThatHasBeenRemoved, weaponToRemove);
    }

    @AfterEach
    void tearDown() {
        square = null;
        weapons = null;
        weaponToRemove = null;
    }
}