package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.ClientRemote;
import it.polimi.ingsw.network.ServerRemote;
import it.polimi.ingsw.network.UnavailableUserException;
import it.polimi.ingsw.view.Client;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements ServerRemote {

    private String remoteName;
    private String ip;
    private int rmiPort;
    private int socketPort;
    private int timerLength;
    private int pingFrequency;

    private Boolean loginPhase = false;
    private Map<String,ClientRemote> clientMap = new ConcurrentHashMap<>();
    private ArrayList<String> nicknameList = new ArrayList<>();

    private Gson gson = new Gson();

    // Utility methods

    public Server() throws Exception {

        Properties properties = new Properties();
        try {
            properties.load(Objects.requireNonNull(Client.class.getClassLoader().getResourceAsStream("network_settings.properties")));
        } catch (IOException e) {
            System.err.println("Error reading network_settings.properties");
            throw new Exception();
        }

        this.remoteName = properties.getProperty("remoteName");
        this.ip = properties.getProperty("serverIp");
        this.rmiPort = Integer.parseInt(properties.getProperty("serverRmiPort"));
        this.socketPort = Integer.parseInt(properties.getProperty("serverSocketPort"));
        this.timerLength = Integer.parseInt(properties.getProperty("loginTimerLength"));
        this.pingFrequency = Integer.parseInt(properties.getProperty("pingFrequency"));

        try {
            new Thread(new ServerSocketAcceptor(socketPort,this)).start();
        } catch (Exception e) {
            System.err.println("Failed to start ServerSocketAcceptor");
            throw new Exception();
        }

        System.setProperty("java.rmi.server.hostname",ip);
        try {
            UnicastRemoteObject.exportObject(this, rmiPort);
        } catch (RemoteException e) {
            System.err.println("Error exporting remote object");
            throw new Exception();
        }
        try {
            LocateRegistry.createRegistry(rmiPort).bind(remoteName,this);
        } catch (RemoteException e) {
            System.err.println("Error creating RMI registry");
            throw new Exception();
        } catch (AlreadyBoundException e) {
            System.err.println("Error binding remote object in RMI registry");
            throw new Exception();
        }

        new Thread(() -> {
            while (true) {
                for (String s : clientMap.keySet()) {
                    try {
                        clientMap.get(s).ping();
                    } catch (RemoteException e) {
                        try {
                            logout(clientMap.get(s));
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                try {
                    Thread.sleep(1000 * pingFrequency);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public synchronized void startLoginPhase() {
        loginPhase = true;
    }

    public synchronized void stopLoginPhase() {

        if ((clientMap.keySet().size() >= 3) && (clientMap.keySet().size() <= 5)) {
            loginPhase = false;
            //TODO Edit the creation of nicknameList so that the player in the first position is the first player connected
            nicknameList = new ArrayList<>(clientMap.keySet());
            System.out.println("Login closed");
        } else if (clientMap.keySet().size() > 5) {
            System.err.println("Something very bad went wrong, more clients registered than allowed");
            clientMap = new ConcurrentHashMap<>();
        } else {
            System.out.println("Login not closed");
        }
    }

    public synchronized Boolean isLoginPhase() {
        return loginPhase;
    }

    public synchronized List<String> getNicknameSet() {
        return nicknameList;
    }

    public synchronized Set<String> getActivePlayers() {
        return clientMap.keySet();
    }

    public synchronized Boolean isConnected(Player player) {

        return clientMap.containsKey(player.getUsername());
    }

    public synchronized void resetClientMap() {
        for (String s : clientMap.keySet()) {
            try {
                clientMap.get(s).notifyLogout();
            } catch (RemoteException ignored) {
                // No matter if notifyLogout fails, client will notice that the connection is lost and logout automatically
            }
        }
        clientMap = new ConcurrentHashMap<>();
        System.out.println(clientMap);
    }

    public synchronized void forceLogout(Player player) {

        try {
            clientMap.remove(player.getUsername()).notifyLogout();
        } catch (RemoteException ignored) {
            // No matter if notifyLogout fails, client will notice that the connection is lost and logout automatically
        }
        System.out.println(clientMap.toString());
        notifyEvent(player.getUsername() + " disconnected");
    }

    // Remote methods

    @Override
    public int ping() throws RemoteException {
        return 0;
    }

    @Override
    public synchronized int login(String s, ClientRemote c) throws RemoteException {

        if (loginPhase) { // Behavior if the login phase is running

            if (!clientMap.containsKey(s)) {
                notifyEvent(s + " connected ");
                clientMap.put(s,c);
                System.out.println(clientMap.toString());

                if (clientMap.keySet().size() >= 5)
                    stopLoginPhase();

                if (clientMap.keySet().size() == 3)
                    new Thread(() -> {
                        int i = timerLength;
                        while ((i > 0) && (clientMap.keySet().size() >= 3)) {
                            System.out.println("Closing login in "+ i +" seconds");
                            i--;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        stopLoginPhase();
                    }).start();

                return 0; // Successful registration
            } else {
                return 1; // Nickname already chosen
            }

        } else { // Behavior if the login phase is not running

            if (nicknameList.contains(s)) {

                if (!clientMap.containsKey(s)) {
                    clientMap.put(s, c);
                    System.out.println(clientMap.toString());
                    notifyEvent(s + " connected");
                    return 2; // Successful login
                } else {
                    return 3; // Already logged in
                }

            } else {
                return 4; // Nickname not registered
            }
        }
    }

    @Override
    public synchronized void logout(ClientRemote c) throws RemoteException {

        try {
            c.notifyLogout();
        } catch (RemoteException ignored) {
            // No matter if notifyLogout fails, client will notice that the connection is lost and logout automatically
        }
        for (Map.Entry<String,ClientRemote> entry : clientMap.entrySet())
            if (entry.getValue().equals(c))
                notifyEvent(entry.getKey() + " disconnected");
        while (clientMap.values().remove(c));
        System.out.println(clientMap.toString());
    }

    // Network methods
    //TODO Clean the network traffic

    public void sendMessage(Player player, String message) throws UnavailableUserException {

        try {
            clientMap.get(player.getUsername()).genericWithoutResponse("sendMessage", message);
        } catch (RemoteException | NullPointerException e) {
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    public void notifyEvent(String s) {

        for (ClientRemote c : clientMap.values()) {
            try {
                c.genericWithoutResponse("notifyEvent",s);
            } catch (RemoteException ignored) {
                // Disconnection of the user is up to the ping thread
            }
        }
    }

    public Player choosePlayer(Player player, ArrayList<Player> arrayList) throws UnavailableUserException {

        Figure[] figures = new Figure[arrayList.size()];

        for (int i = 0 ;  i < arrayList.size() ; i++) {
            figures[i] = arrayList.get(i).getFigure();
        }

        try {
            Figure choice = gson.fromJson(clientMap.get(player.getUsername()).singleChoice("player",gson.toJson(figures)),Figure.class);
            for (Player player1 : arrayList) {
                if (player1.getFigure() == choice)
                    return player1;
            } throw new UnavailableUserException();
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    public Weapon chooseWeapon(Player player, ArrayList<Weapon> arrayList) throws UnavailableUserException {

        WeaponName[] weapons = new WeaponName[arrayList.size()];

        for (int i = 0 ; i < arrayList.size() ; i++) {
            weapons[i] = arrayList.get(i).getName();
        }

        try {
            WeaponName choice = gson.fromJson(clientMap.get(player.getUsername()).singleChoice("weapon",gson.toJson(weapons)),WeaponName.class);
            for (Weapon weapon : arrayList) {
                if (weapon.getName() == choice) {
                    return weapon;
                }
            } throw new UnavailableUserException();
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    public String chooseString(Player player, ArrayList<String> arrayList) throws UnavailableUserException {

        String[] strings = new String[arrayList.size()];
        strings = arrayList.toArray(strings);

        try {
            return gson.fromJson(clientMap.get(player.getUsername()).singleChoice("string",gson.toJson(strings)),String.class);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    public Character chooseDirection(Player player) throws UnavailableUserException {

        String[] directions = new String[4];
        directions[0] = "North";
        directions[1] = "South";
        directions[2] = "East";
        directions[3] = "West";

        try {
            String choice = gson.fromJson(clientMap.get(player.getUsername()).singleChoice("string",gson.toJson(directions)),String.class);
            switch (choice) {
                case "North": return 'N';
                case "South": return 'S';
                case "East": return 'E';
                case "West": return 'W';
                default: throw new UnavailableUserException();
            }
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    public Color chooseColor(Player player) throws UnavailableUserException {

        String[] colors = new String[3];
        colors[0] = "Red";
        colors[1] = "Blue";
        colors[2] = "Yellow";

        try {
            String choice = gson.fromJson(clientMap.get(player.getUsername()).singleChoice("string",gson.toJson(colors)),String.class);
            switch (choice) {
                case "Red": return Color.RED;
                case "Blue": return Color.BLUE;
                case "Yellow": return Color.YELLOW;
                default: throw new UnavailableUserException();
            }
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    public Powerup choosePowerup(Player player, ArrayList<Powerup> arrayList) throws UnavailableUserException {

        Powerup[] powerups = new Powerup[arrayList.size()];
        powerups = arrayList.toArray(powerups);

        try {
            return gson.fromJson(clientMap.get(player.getUsername()).singleChoice("powerup",gson.toJson(powerups)),Powerup.class);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    public int chooseMap(Player player, int min, int max) throws UnavailableUserException {

        String[] maps = new String[max-min+1];
        for (int i = 0 ; i <= max-min ; i++) {
            maps[i] = "" + (max-min+i);
        }

        try {
            return Integer.parseInt(gson.fromJson(clientMap.get(player.getUsername()).singleChoice("map",gson.toJson(maps)),String.class));
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    public Character chooseMode(Player player) throws UnavailableUserException {

        String[] modes = new String[3];
        modes[0] = "Normal";
        modes[1] = "Domination";
        modes[2] = "Load existing match";

        try {
            String choice = gson.fromJson(clientMap.get(player.getUsername()).singleChoice("mode",gson.toJson(modes)),String.class);
            switch (choice) {
                case "Normal": return 'N';
                case "Domination": return 'D';
                case "Load existing match": return 'S';
                default: throw new UnavailableUserException();
            }
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    public String chooseSave(Player player, ArrayList<String> arrayList) throws UnavailableUserException {

        String[] saves = new String[arrayList.size()];
        saves = arrayList.toArray(saves);

        try {
            return gson.fromJson(clientMap.get(player.getUsername()).singleChoice("save", gson.toJson(saves)),String.class);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    public Square chooseSquare(Player player, ArrayList<Square> arrayList) throws UnavailableUserException {
        // TODO
        return null;
    }

    public ArrayList<Powerup> chooseMultiplePowerup(Player player, ArrayList<Powerup> arrayList) throws UnavailableUserException {

        Powerup[] powerups = new Powerup[arrayList.size()];
        powerups = arrayList.toArray(powerups);

        try {
            return new ArrayList<>(Arrays.asList(gson.fromJson(clientMap.get(player.getUsername()).multipleChoice("powerup",gson.toJson(powerups)),Powerup[].class)));
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    public ArrayList<Weapon> chooseMultipleWeapon(Player player, ArrayList<Weapon> arrayList) throws UnavailableUserException {

        WeaponName[] weapons = new WeaponName[arrayList.size()];

        for (int i = 0 ; i < arrayList.size() ; i++) {
            weapons[i] = arrayList.get(i).getName();
        }

        try {
            WeaponName[] choice = gson.fromJson(clientMap.get(player.getUsername()).multipleChoice("weapon",gson.toJson(weapons)),WeaponName[].class);
            ArrayList<Weapon> retVal = new ArrayList<>();
            for (int i = 0 ; i < choice.length ; i++) {
                for (Weapon weapon : arrayList) {
                    if (weapon.getName() == choice[i])
                        retVal.add(weapon);
                }
            } return retVal;
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    public Boolean booleanQuestion(Player player, String string) throws UnavailableUserException {

        try {
            return clientMap.get(player.getUsername()).booleanQuestion(string);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

}