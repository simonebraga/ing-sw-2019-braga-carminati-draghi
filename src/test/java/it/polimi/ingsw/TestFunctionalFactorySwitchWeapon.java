package it.polimi.ingsw;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the function that switches two weapons between a Square and a Player:
 * the Weapons get switched correctly and the other weapons don't change.
 */
class TestFunctionalFactorySwitchWeapon {
    SpawnSquare square;
    Weapon weaponToTake;
    Weapon weaponToGive;
    Weapon weaponTemp;
    ArrayList<Weapon> playerWeapons;
    ArrayList<Weapon> squareWeapons;
    FunctionalEffect effect;
    Player player;

    @BeforeEach
    void setUp() {
        playerWeapons = new ArrayList<>();
        squareWeapons = new ArrayList<>();

        player = new Player(Figure.BANSHEE, "Player1");
        square = new SpawnSquare(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING, Color.BLUE);
        player.move(square);

        weaponToTake = new Weapon(new ArrayList<>(), WeaponName.ELECTROSCYTHE, Boolean.TRUE);
        weaponToGive = new Weapon(new ArrayList<>(), WeaponName.WHISPER, Boolean.TRUE);

        square.addWeapon(weaponToTake);
        try {
            player.getWeaponPocket().addWeapon(weaponToGive);
        } catch (FullPocketException e) {
            fail();
        }

        weaponTemp = new Weapon(new ArrayList<>(), WeaponName.CYBERBLADE, Boolean.TRUE);
        try {
            player.getWeaponPocket().addWeapon(weaponTemp);
        } catch (FullPocketException e) {
            fail();
        }
        playerWeapons.add(weaponTemp);

        weaponTemp = new Weapon(new ArrayList<>(), WeaponName.SHOCKWAVE, Boolean.TRUE);
        try {
            player.getWeaponPocket().addWeapon(weaponTemp);
        } catch (FullPocketException e) {
            fail();
        }
        playerWeapons.add(weaponTemp);

        weaponTemp = new Weapon(new ArrayList<>(), WeaponName.FURNACE, Boolean.TRUE);
        square.addWeapon(weaponTemp);
        squareWeapons.add(weaponTemp);

        weaponTemp = new Weapon(new ArrayList<>(), WeaponName.VORTEXCANNON, Boolean.TRUE);
        square.addWeapon(weaponTemp);
        squareWeapons.add(weaponTemp);

        squareWeapons.add(weaponToGive);
        playerWeapons.add(weaponToTake);
        effect = new FunctionalFactory().createSwitchWeapon(player, weaponToTake, weaponToGive);
    }

    @AfterEach
    void tearDown() {
        square = null;
        weaponToTake = null;
        weaponToGive = null;
        weaponTemp = null;
        playerWeapons = null;
        squareWeapons = null;
        effect = null;
        player = null;
    }


    @Test
    void createSwitchWeapon() {
        try {
            effect.doAction();
        } catch (FullPocketException e) {
            fail();
        }
        assertTrue(player.getWeaponPocket().getWeapons().containsAll(playerWeapons));
        assertTrue(playerWeapons.containsAll(player.getWeaponPocket().getWeapons()));

        assertTrue(square.getWeapons().containsAll(squareWeapons));
        assertTrue(squareWeapons.containsAll(square.getWeapons()));
    }
}