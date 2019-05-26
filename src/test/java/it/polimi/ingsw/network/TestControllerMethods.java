package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.PowerupName;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.NetworkException;
import it.polimi.ingsw.view.ViewInterface;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains all the tests of the methods that uses the network.
 * It is not a test-suite of the network itself, it is a test-suite used to check if the serialization protocol of the objects is implemented correctly.
 * @author simonebraga
 */
class TestControllerMethods {

    static Controller controller;
    static Client client1;
    static Client client2;
    static Client client3;

    /**
     * This class is a temporary implementation of the view, used to simulate the user interaction
     */
    static class TestView implements ViewInterface {

        @Override
        public Figure choosePlayer(Figure[] f) {
            return f[0];
        }

        @Override
        public WeaponName chooseWeapon(WeaponName[] w) {
            return w[0];
        }

        @Override
        public String chooseString(String[] s) {
            return s[0];
        }

        @Override
        public Powerup choosePowerup(Powerup[] p) {
            return p[0];
        }

        @Override
        public Boolean booleanQuestion(String s) {
            return true;
        }

        @Override
        public Powerup[] chooseMultiplePowerups(Powerup[] p) {
            Powerup[] retVal = new Powerup[2];
            retVal[0] = p[1];
            retVal[1] = p[2];
            return retVal;
        }

        @Override
        public WeaponName[] chooseMultipleWeapons(WeaponName[] w) {
            WeaponName[] retVal = new WeaponName[2];
            retVal[0] = w[1];
            retVal[1] = w[2];
            return retVal;
        }

        @Override
        public String chooseMap(String[] s) {
            return s[0];
        }

        @Override
        public String chooseMode(String[] s) {
            return s[0];
        }

        @Override
        public String chooseSave(String[] s) {
            return s[0];
        }
    }

    @BeforeAll
    static void initController() {

        try {
            controller = new Controller();
        } catch (RemoteException e) {
            fail();
        }
    }

    @BeforeEach
    void initUser() {

        try {
            controller.resetClientMap();
            controller.startLoginPhase();
            client1 = new Client(0,new TestView());
            client2 = new Client(0,new TestView());
            client3 = new Client(0,new TestView());
            client1.login("User1");
            client2.login("User2");
            client3.login("User3");
            controller.stopLoginPhase();
        } catch (RemoteException | NetworkException e) {
            fail();
        }
    }

    @Test
    void testLogin() {

        controller.resetClientMap();
        controller.startLoginPhase();

        try {
            Client client = new Client(0, null);
            assertEquals(0,client.login("Nickname"));

            Set<String> set = new HashSet<>();
            set.add("Nickname");

            assertEquals(set , controller.getNicknameSet());
        } catch (RemoteException | NetworkException e) {
            fail();
        }
    }

    @Test
    void testLogout() {

        controller.resetClientMap();
        controller.startLoginPhase();

        try {
            Client client1 = new Client(0, null);
            Client client2 = new Client(0, null);
            client1.login("Nickname1");
            client2.login("Nickname2");
            client1.logout();

            Set<String> set = new HashSet<>();
            set.add("Nickname2");

            assertEquals(set,controller.getNicknameSet());
        } catch (RemoteException | NetworkException e) {
            fail();
        }
    }

    @Test
    void testChoosePlayer() {

        Player player1 = new Player(Figure.DOZER,"Nickname1");
        Player player2 = new Player(Figure.DESTRUCTOR,"Nickname2");
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        try {
            Player retVal = controller.choosePlayer(new Player(Figure.VIOLET,"User1"),players);
            assertEquals(player1,retVal);
        } catch (UnavailableUserException e) {
            fail();
        }
    }

    @Test
    void testChooseWeapon() {

        Weapon weapon1 = new Weapon(new ArrayList<>(), WeaponName.THOR,true);
        Weapon weapon2 = new Weapon(new ArrayList<>(), WeaponName.FURNACE, true);
        Weapon weapon3 = new Weapon(new ArrayList<>(), WeaponName.CYBERBLADE, true);
        ArrayList<Weapon> weapons = new ArrayList<>();
        weapons.add(weapon1);
        weapons.add(weapon2);
        weapons.add(weapon3);

        try {
            Weapon retVal = controller.chooseWeapon(new Player(Figure.DOZER,"User1"),weapons);
            assertEquals(weapon1,retVal);
        } catch (UnavailableUserException e) {
            fail();
        }
    }

    @Test
    void testChooseDirection() {

        try {
            Character retVal = controller.chooseDirection(new Player(Figure.VIOLET,"User1"));
            assertEquals('N',retVal);
        } catch (UnavailableUserException e) {
            fail();
        }
    }

    @Test
    void testChooseString() {

        String string1 = "Hello";
        String string2 = "How are you?";
        ArrayList<String> strings = new ArrayList<>();
        strings.add(string1);
        strings.add(string2);

        try {
            String retVal = controller.chooseString(new Player(Figure.DESTRUCTOR,"User1"),strings);
            assertEquals(string1,retVal);
        } catch (UnavailableUserException e) {
            fail();
        }
    }

    @Test
    void testChoosePowerup() {

        Powerup powerup1 = new Powerup(Color.RED, PowerupName.NEWTON);
        Powerup powerup2 = new Powerup(Color.YELLOW, PowerupName.NEWTON);
        Powerup powerup3 = new Powerup(Color.YELLOW, PowerupName.TARGETINGSCOPE);
        ArrayList<Powerup> powerups = new ArrayList<>();
        powerups.add(powerup1);
        powerups.add(powerup2);
        powerups.add(powerup3);

        try {
            Powerup retVal = controller.choosePowerup(new Player(Figure.BANSHEE,"User1"),powerups);
            assertEquals(powerup1.getColor(),retVal.getColor());
            assertEquals(powerup1.getName(),retVal.getName());
        } catch (UnavailableUserException e) {
            fail();
        }
    }

    @Test
    void testBooleanQuestion() {

        try {
            assertEquals(true,controller.booleanQuestion(new Player(Figure.SPROG,"User1"),"Question"));
        } catch (UnavailableUserException e) {
            fail();
        }
    }

    @Test
    void testChooseColor() {

        try {
            Color retVal = controller.chooseColor(new Player(Figure.BANSHEE,"User1"));
            assertEquals(Color.RED,retVal);
        } catch (UnavailableUserException e) {
            fail();
        }
    }

    @Test
    void testChooseMultiplePowerup() {

        Powerup powerup1 = new Powerup(Color.RED,PowerupName.NEWTON);
        Powerup powerup2 = new Powerup(Color.RED,PowerupName.TARGETINGSCOPE);
        Powerup powerup3 = new Powerup(Color.BLUE,PowerupName.TELEPORTER);
        Powerup powerup4 = new Powerup(Color.YELLOW, PowerupName.NEWTON);
        ArrayList<Powerup> powerups = new ArrayList<>();
        powerups.add(powerup1);
        powerups.add(powerup2);
        powerups.add(powerup3);
        powerups.add(powerup4);

        try {
            ArrayList<Powerup> returned = controller.chooseMultiplePowerup(new Player(Figure.BANSHEE,"User1"),powerups);
            assertEquals(powerups.get(1).getName(),returned.get(0).getName());
            assertEquals(powerups.get(1).getColor(),returned.get(0).getColor());
            assertEquals(powerups.get(2).getName(),returned.get(1).getName());
            assertEquals(powerups.get(2).getColor(),returned.get(1).getColor());
        } catch (UnavailableUserException e) {
            fail();
        }
    }

    @Test
    void testChooseMultipleWeapon() {

        Weapon weapon1 = new Weapon(new ArrayList<>(), WeaponName.FURNACE,true);
        Weapon weapon2 = new Weapon(new ArrayList<>(), WeaponName.MACHINEGUN, true);
        Weapon weapon3 = new Weapon(new ArrayList<>(), WeaponName.CYBERBLADE, true);
        ArrayList<Weapon> weapons = new ArrayList<>();
        weapons.add(weapon1);
        weapons.add(weapon2);
        weapons.add(weapon3);
        ArrayList<Weapon> expected = new ArrayList<>();
        expected.add(weapon2);
        expected.add(weapon3);

        try {
            ArrayList<Weapon> returned = controller.chooseMultipleWeapon(new Player(Figure.DESTRUCTOR,"User1"),weapons);
            assertEquals(returned,expected);
        } catch (UnavailableUserException e) {
            fail();
        }
    }

    @Test
    void testChooseMap() {

        int returned = 0;
        try {
            returned = controller.chooseMap(new Player(Figure.DESTRUCTOR,"User1"),2,4);
            assertEquals(2,returned);
        } catch (UnavailableUserException e) {
            fail();
        }
    }

    @Test
    void testChooseMode() {

        try {
            assertEquals('N',controller.chooseMode(new Player(Figure.DESTRUCTOR,"User1")));
        } catch (UnavailableUserException e) {
            fail();
        }
    }

    @Test
    void testChooseSave() {

        String string1 = "Savegame1";
        String string2 = "Savegame2";
        ArrayList<String> strings = new ArrayList<>();
        strings.add(string1);
        strings.add(string2);

        try {
            String retVal = controller.chooseString(new Player(Figure.DESTRUCTOR,"User1"),strings);
            assertEquals(string1,retVal);
        } catch (UnavailableUserException e) {
            fail();
        }
    }
}