package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.CardClasses.Weapon;
import it.polimi.ingsw.Model.EnumeratedClasses.Border;
import it.polimi.ingsw.Model.EnumeratedClasses.Color;
import it.polimi.ingsw.Model.EnumeratedClasses.WeaponName;
import it.polimi.ingsw.Model.MapClasses.SpawnSquare;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the method: it adds the Weapon to the list of Weapons in the SpawnSquare and doesn't change the other Weapons.
 */
class TestSpawnSquareAddWeapon {
    SpawnSquare square;
    Weapon weaponToAdd;
    ArrayList<Weapon> weapons;


    @BeforeEach
    void setUp() {
        square = new SpawnSquare(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING, Color.BLUE);
        weaponToAdd = new Weapon(new ArrayList<Color>(), WeaponName.ELECTROSCYTHE, Boolean.TRUE);
        weapons = new ArrayList<>();
        weapons.add(new Weapon(new ArrayList<Color>(), WeaponName.TRACTORBEAM, Boolean.TRUE));
        weapons.add(new Weapon(new ArrayList<Color>(), WeaponName.CYBERBLADE, Boolean.TRUE));
        weapons.add(new Weapon(new ArrayList<Color>(), WeaponName.VORTEXCANNON, Boolean.TRUE));
        weapons.add(new Weapon(new ArrayList<Color>(), WeaponName.SHOCKWAVE, Boolean.TRUE));
        weapons.add(new Weapon(new ArrayList<Color>(), WeaponName.POWERGLOVE, Boolean.TRUE));
        square.setWeapons((ArrayList<Weapon>) weapons.clone());
        weapons.add(weaponToAdd);
    }

    @Test
    void addWeapon() {
        square.addWeapon(weaponToAdd);
        assertTrue(square.getWeapons().containsAll(weapons) && weapons.containsAll(square.getWeapons()));
    }

    @AfterEach
    void tearDown() {
        square = null;
        weaponToAdd = null;
        weapons = null;
    }
}