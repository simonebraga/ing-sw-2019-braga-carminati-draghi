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

    public synchronized Set<String> getNicknameSet() {
        return new HashSet<>(nicknameList);
    }

    public synchronized Set<String> getActivePlayers() {
        return clientMap.keySet();
    }

    public synchronized Boolean isConnected(Player player) {

        return clientMap.containsKey(player.getUsername());
    }

    public synchronized void resetClientMap() {
        clientMap = new ConcurrentHashMap<>();
    }

    public synchronized void forceLogout(Player player) {

        clientMap.remove(player.getUsername());
        System.out.println(clientMap.toString());
        //TODO Notify all of the disconnection
    }

    @Override
    public int ping() throws RemoteException {
        return 0;
    }

    @Override
    public synchronized int login(String s, ClientRemote c) throws RemoteException {

        if (loginPhase) { // Behavior if the login phase is running

            if (!clientMap.containsKey(s)) {
                clientMap.put(s,c);
                //TODO Notify all of the connection
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
                    //TODO Notify all of the connection
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

        if (clientMap.containsValue(c)) {
            while (clientMap.values().remove(c));
            System.out.println(clientMap.toString());
            //TODO Notify all of the disconnection
        }
    }

    // The following methods invoke the remote methods of the client. Every method must have a timer to throw an exception if the client is too slow with the response
    /**
     * This methods asks the user to make a choice in a set of players
     * @param player Player who must choose
     * @param arrayList Set of players to choose from
     * @return Player chosen by the user
     * @throws UnavailableUserException if the user is not connected
     */
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

    /**
     * This method asks the user to make a choice in a set o squares
     * @param player Player who must choose
     * @param arrayList Set of squares to choose from
     * @return Square chosen by the user
     * @throws UnavailableUserException if the user is not connected
     */
    public Square chooseSquare(Player player, ArrayList<Square> arrayList) throws UnavailableUserException {
        // TODO
        return null;
    }

    /**
     * This methods asks the user to make a choice in a set of powerups
     * @param player Player who must choose
     * @param arrayList Set of powerups to choose from
     * @return Powerup chosen by the user
     * @throws UnavailableUserException if the user is not connected
     */
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

    /**
     * This methods asks the user to make a choice in a set of weapons
     * @param player Player who must choose
     * @param arrayList Set of weapons to choose from
     * @return Weapon chosen by the user
     * @throws UnavailableUserException if the user is not connected
     */
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

    /**
     * This methods asks the user to choose a direction
     * @param player Player who must choose
     * @return Direction chosen by the user
     * @throws UnavailableUserException if the user is not connected
     */
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

    /**
     * This methods asks the user to make a choice in a set of strings
     * @param player Player who must choose
     * @param arrayList Set of strings to choose from
     * @return String chosen by the user
     * @throws UnavailableUserException if the user is not connected
     */
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

    /**
     * This methods asks the user to answer yes or not to a question
     * @param player Player who must answer
     * @param string Boolean question
     * @return Boolean answer of the user
     * @throws UnavailableUserException if the user is not connected
     */
    public Boolean booleanQuestion(Player player, String string) throws UnavailableUserException {

        try {
            return clientMap.get(player.getUsername()).booleanQuestion(string);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This methods asks the user to make a multiple choice in a set of weapons
     * @param player Player who must choose
     * @param arrayList Set of weapons to choose from
     * @return Set of weapons selected by the user
     * @throws UnavailableUserException if the user is not connected
     */
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

    /**
     * This methods asks the user to choose a color
     * @param player Player who must choose
     * @return Color chosen by the user
     * @throws UnavailableUserException if the user is not connected
     */
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

    /**
     * This methods sends a message to a user
     * @param player Player to send the message to
     * @param message Message to be sent
     * @throws UnavailableUserException if the user is not connected
     */
    public void sendMessage(Player player, String message) throws UnavailableUserException {

        try {
            clientMap.get(player.getUsername()).genericWithoutResponse("systemMessage", message);
        } catch (RemoteException | NullPointerException e) {
            e.printStackTrace();
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This methods asks the user to make a multiple choice in a set of powerups
     * @param player Player who must choose
     * @param arrayList Set of powerups to choose from
     * @return Set of powerups selected by the user
     * @throws UnavailableUserException if the user is not connected
     */
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

    /**
     * This method asks the user to choose an ID representing a map
     * @param player Player who must choose
     * @param min Minimum ID
     * @param max Maximum ID
     * @return ID chosen by the user
     * @throws UnavailableUserException if the user is not connected
     */
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

    /**
     * This method asks the user to choose a game mode
     * @param player Player who must choose
     * @return Character representing the chosen game mode
     * @throws UnavailableUserException if the user is not connected
     */
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

    /**
     * This method asks the user to choose a save game
     * @param player Player who must choose
     * @param arrayList List of the unique names of the save games
     * @return Name of the chosen save game
     * @throws UnavailableUserException if the user is not connected
     */
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

}