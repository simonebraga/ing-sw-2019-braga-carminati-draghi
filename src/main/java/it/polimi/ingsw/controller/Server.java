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
import java.util.concurrent.*;

/**
 * This class contains all the necessary methods to communicate with the clients.
 * It represents the server-side access to the network
 * @author simonebraga
 */
public class Server implements ServerRemote {

    private String remoteName;
    private String ip;
    private int rmiPort;
    private int socketPort;
    private int timerLength;
    private int pingFrequency;
    private int pingLatency;
    private int inactivityTime;

    private Boolean loginPhase = false;
    private Map<String,ClientRemote> clientMap = new ConcurrentHashMap<>();
    private ArrayList<String> nicknameList = new ArrayList<>();

    private Gson gson = new Gson();
    ExecutorService executorService = Executors.newCachedThreadPool();

    // Utility methods

    /**
     * This method is the constructor of the class. It sets up RMI and Socket connection, and also runs a ping thread that checks if clients disconnect
     * @throws Exception if any step of the setup goes wrong
     */
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
        this.pingLatency = Integer.parseInt(properties.getProperty("pingLatency"));
        this.inactivityTime = Integer.parseInt(properties.getProperty("inactivityTime"));

        try {
            new Thread(new ServerSocketAcceptor(socketPort,this,pingLatency)).start();
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

        System.out.println("Server created");

        startPingThread();
    }

    /**
     * This method resets the client map attribute and allows new registrations until the login is closed
     */
    public synchronized void startLoginPhase() {
        clientMap = new ConcurrentHashMap<>();
        loginPhase = true;
        System.out.println("Login opened");
    }

    /**
     * This method checks if there are enough clients connected to start the game.
     * If so, it sets loginPhase attribute to false, and saves the nickname list when the login closes
     */
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

    /**
     * @return  true if the login phase is opened (which means that new clients are allowed to register in client map)
     *          false if the login phase is closed
     */
    public synchronized Boolean isLoginPhase() {
        return loginPhase;
    }

    /**
     * @return a list containing the nicknames registered when the login closed
     */
    public synchronized List<String> getNicknameSet() {
        return nicknameList;
    }

    /**
     * @return a set containing the nicknames of the currently connected players
     */
    public synchronized Set<String> getActivePlayers() {
        return clientMap.keySet();
    }

    /**
     * @param player is the player which connection is checked
     * @return  true if the user associated to players is connected
     *          false if the user associated to player is not connected
     */
    public synchronized Boolean isConnected(Player player) {

        return clientMap.containsKey(player.getUsername());
    }

    /**
     * This methods forces the reset of the client map. It removes all the players connected
     */
    public synchronized void resetClientMap() {

        clientMap = new ConcurrentHashMap<>();
        System.out.println(clientMap);
    }

    /**
     * This method forces the logout of a player, directly removing it from the client map
     * @param player is the player to be removed
     */
    public synchronized void forceLogout(Player player) {

        clientMap.remove(player.getUsername());
        System.out.println(clientMap.toString());
        notifyEvent(player.getUsername() + " disconnected");
    }

    /**
     * This method starts a thread that checks the connection with the clients with a specified frequency
     * If a client is checked as disconnected, it is removed from the client map attribute
     */
    private void startPingThread() {

        new Thread(() -> {
            System.out.println("Ping thread started");
            while (true) {
                for (String s : clientMap.keySet()) {
                    try {
                        executorService.submit(() -> {
                            try {
                                clientMap.get(s).ping();
                            } catch (RemoteException e) {
                                try {
                                    Thread.sleep((pingLatency + 1) * 1000);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }).get(pingLatency,TimeUnit.SECONDS);
                    } catch (InterruptedException | TimeoutException | ExecutionException e) {
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
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Remote methods

    @Override
    public int ping(ClientRemote c) throws RemoteException {
        if (clientMap.containsValue(c)) return 0;
        throw new RemoteException();
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

        String disconnectedNickname = "Player";
        for (Map.Entry<String,ClientRemote> entry : clientMap.entrySet())
            if (entry.getValue().equals(c))
                disconnectedNickname = entry.getKey();
        while (clientMap.values().remove(c));
        System.out.println(clientMap.toString());
        notifyEvent(disconnectedNickname + " disconnected");
    }

    // Network methods
    //TODO Clean the network traffic
    //TODO Add timer to network requests

    public void sendMessage(Player player, String message) throws UnavailableUserException {

        try {
            executorService.submit(() -> {
                try {
                    clientMap.get(player.getUsername()).genericWithoutResponse("sendMessage", message);
                } catch (RemoteException ignored) {
                    // It is useless to disconnect the client
                }
            }).get(pingLatency,TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // It is useless to disconnect the client
        }
    }

    public void notifyEvent(String s) {

        for (ClientRemote c : clientMap.values()) {
            try {
                executorService.submit(() -> {
                    try {
                        c.genericWithoutResponse("notifyEvent",s);
                    } catch (RemoteException e) {
                        // It is useless to disconnect the client
                    }
                }).get(pingLatency,TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                // It is useless to disconnect the client
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