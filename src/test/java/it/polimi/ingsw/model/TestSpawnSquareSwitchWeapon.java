package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the switchWeapon method: it must add correctly the weapon, remove correctly the weaponToGet and the other weapons must not be changed.
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
        weaponToAdd = new Weapon(new ArrayList<>(), WeaponName.CYBERBLADE, Boolean.TRUE);
        weaponToRemove = new Weapon(new ArrayList<>(), WeaponName.TRACTORBEAM, Boolean.TRUE);
        weapons.add(weaponToRemove);
        weapons.add(new Weapon(new ArrayList<>(), WeaponName.HEATSEEKER, Boolean.TRUE));
        weapons.add(new Weapon(new ArrayList<>(), WeaponName.MACHINEGUN, Boolean.TRUE));
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