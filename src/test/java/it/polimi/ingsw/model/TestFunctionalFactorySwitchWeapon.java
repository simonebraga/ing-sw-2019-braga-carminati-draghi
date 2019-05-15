package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

        player.getWeaponPocket().addWeapon(weaponToGive);

        weaponTemp = new Weapon(new ArrayList<>(), WeaponName.CYBERBLADE, Boolean.TRUE);
        player.getWeaponPocket().addWeapon(weaponTemp);
        playerWeapons.add(weaponTemp);

        weaponTemp = new Weapon(new ArrayList<>(), WeaponName.SHOCKWAVE, Boolean.TRUE);
        player.getWeaponPocket().addWeapon(weaponTemp);
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
        effect.doAction();

        assertTrue(player.getWeaponPocket().getWeapons().containsAll(playerWeapons));
        assertTrue(playerWeapons.containsAll(player.getWeaponPocket().getWeapons()));

        assertTrue(square.getWeapons().containsAll(squareWeapons));
        assertTrue(squareWeapons.containsAll(square.getWeapons()));
    }
}