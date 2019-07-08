package it.polimi.ingsw.controller.autocontroller;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.model.smartmodel.SmartModel;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.Random;

public class ServerRandom extends Server {

    private static final int LATENCY = 200;
    private Random random = new Random();

    public ServerRandom() throws Exception {
        super();
        setSmartModel(new SmartModel());
    }

    @Override
    public synchronized Boolean isConnected(Player player) {
        return true;
    }

    @Override
    public void sendMessage(Player player, String message) throws UnavailableUserException {
    }

    @Override
    public Player choosePlayer(Player player, ArrayList<Player> arrayList) throws UnavailableUserException {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return arrayList.get(random.nextInt(arrayList.size()));
    }

    @Override
    public Weapon chooseWeapon(Player player, ArrayList<Weapon> arrayList) throws UnavailableUserException {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return arrayList.get(random.nextInt(arrayList.size()));
    }

    @Override
    public String chooseString(Player player, ArrayList<String> arrayList) throws UnavailableUserException {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return arrayList.get(random.nextInt(arrayList.size()));
    }

    @Override
    public Character chooseDirection(Player player) throws UnavailableUserException {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int direction = random.nextInt(4);
        switch (direction) {
            case 0: return 'N';
            case 1: return 'S';
            case 2: return 'W';
            default: return 'E';
        }
    }

    @Override
    public Color chooseColor(Player player) throws UnavailableUserException {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int direction = random.nextInt(3);
        switch (direction) {
            case 0: return Color.BLUE;
            case 1: return Color.YELLOW;
            default: return Color.RED;
        }
    }

    @Override
    public Powerup choosePowerup(Player player, ArrayList<Powerup> arrayList) throws UnavailableUserException {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return arrayList.get(random.nextInt(arrayList.size()));
    }

    @Override
    public int chooseMap(Player player, int min, int max) throws UnavailableUserException {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return random.nextInt(max-min) + min;
    }

    @Override
    public Character chooseMode(Player player) throws UnavailableUserException {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int direction = random.nextInt(2);
        switch (direction) {
            case 0: return 'n';
            default: return 'd';
        }
    }

    @Override
    public Square chooseSquare(Player player, ArrayList<Square> arrayList) throws UnavailableUserException {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return arrayList.get(random.nextInt(arrayList.size()));
    }

    @Override
    public ArrayList<Powerup> chooseMultiplePowerup(Player player, ArrayList<Powerup> arrayList) throws UnavailableUserException {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Powerup> powerups = new ArrayList<>();
        for (Powerup powerup : arrayList)
            if (random.nextBoolean())
                powerups.add(powerup);
        return powerups;
    }

    @Override
    public ArrayList<Weapon> chooseMultipleWeapon(Player player, ArrayList<Weapon> arrayList) throws UnavailableUserException {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Weapon> weapons = new ArrayList<>();
        for (Weapon weapon : arrayList)
            if (random.nextBoolean())
                weapons.add(weapon);
        return weapons;
    }

    @Override
    public Boolean booleanQuestion(Player player, String string) throws UnavailableUserException {
        try {
            Thread.sleep(LATENCY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int direction = random.nextInt(2);
        switch (direction) {
            case 0: return Boolean.TRUE;
            default: return Boolean.FALSE;
        }
    }
}
