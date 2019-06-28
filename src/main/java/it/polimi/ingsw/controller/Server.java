package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.model.smartmodel.SmartModel;
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
    private SmartModel smartModel;

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
        this.smartModel = null;

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
     * This method sets the correct reference to the smart model sent to the client when an update on the model is performed
     * @param smartModel is the smartModel to be sent to the players
     */
    public void setSmartModel(SmartModel smartModel) {
        this.smartModel = smartModel;
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
            Collections.shuffle(nicknameList);
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
                        while ((i > 0) && (clientMap.keySet().size() >= 3) && (clientMap.keySet().size() < 5)) {
                            System.out.println("Closing login in "+ i +" seconds");
                            i--;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (isLoginPhase())
                            stopLoginPhase();
                    }).start();

                return 0; // Successful registration
            } else {
                return 1; // Nickname already chosen
            }

        } else { // Behavior if the login phase is not running

            if (nicknameList.contains(s)) {

                if (!clientMap.containsKey(s)) {
                    notifyEvent(s + " connected");
                    clientMap.put(s, c);
                    System.out.println(clientMap.toString());
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

    @Override
    public String getModelUpdate() throws RemoteException {
        if (smartModel == null)
            return "";
        return smartModel.toString();
    }

    // Network methods

    /**
     * This method sends a message to a specific player. It should be used to send messages inherent in the game (I.E. "It is your turn")
     * @param player is the player to send the message to
     * @param message is the message to be sent
     * @throws UnavailableUserException if the player is not connected or the network timeout expires
     */
    public void sendMessage(Player player, String message) throws UnavailableUserException {

        try {
            executorService.submit(() -> {
                try {
                    clientMap.get(player.getUsername()).genericWithoutResponse("sendMessage", message);
                } catch (RemoteException ignored) {
                    // It is useless to disconnect the client
                }
            }).get(pingLatency,TimeUnit.SECONDS);
        } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
            // It is useless to disconnect the client
        }
    }

    /**
     * This method notifies all the players about an event. It should be used to notify about events not inherent in the game (I.E. "Player disconnected")
     * @param s is the event to be notified
     */
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
            } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
                // It is useless to disconnect the client
            }
        }
    }

    /**
     * This method asks a player to make a single choice in a set of players
     * @param player is the player who makes the choice
     * @param arrayList is the set of players to choose from
     * @return the player chosen by the player
     * @throws UnavailableUserException if the player is not connected or the network timeout expires
     */
    public Player choosePlayer(Player player, ArrayList<Player> arrayList) throws UnavailableUserException {

        Figure[] figures = new Figure[arrayList.size()];
        for (int i = 0 ;  i < arrayList.size() ; i++)
            figures[i] = arrayList.get(i).getFigure();

        try {
            return executorService.submit(() -> arrayList.get(clientMap.get(player.getUsername()).singleChoice("player",gson.toJson(figures)))).get(inactivityTime,TimeUnit.SECONDS);
        } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This method asks a player to make a single choice in a set of weapons
     * @param player is the player who makes the choice
     * @param arrayList is the set of weapons to choose from
     * @return the weapon chosen by the player
     * @throws UnavailableUserException if the player is not connected or the network timeout expires
     */
    public Weapon chooseWeapon(Player player, ArrayList<Weapon> arrayList) throws UnavailableUserException {

        WeaponName[] weapons = new WeaponName[arrayList.size()];
        for (int i = 0 ; i < arrayList.size() ; i++)
            weapons[i] = arrayList.get(i).getName();

        try {
            return executorService.submit(() -> arrayList.get(clientMap.get(player.getUsername()).singleChoice("weapon",gson.toJson(weapons)))).get(inactivityTime,TimeUnit.SECONDS);
        } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This method asks a player to make a single choice in a set of strings
     * @param player is the player who makes the choice
     * @param arrayList is the set of strings to choose from
     * @return the string chosen by the player
     * @throws UnavailableUserException if the player is not connected or the network timeout expires
     */
    public String chooseString(Player player, ArrayList<String> arrayList) throws UnavailableUserException {

        String[] strings = new String[arrayList.size()];
        for (int i = 0 ; i < arrayList.size() ; i++)
            strings[i] = arrayList.get(i);

        try {
            return executorService.submit(() -> arrayList.get(clientMap.get(player.getUsername()).singleChoice("string",gson.toJson(strings)))).get(inactivityTime,TimeUnit.SECONDS);
        } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This method asks a player to choose a direction between north, south, west and east
     * @param player is the player who makes the choice
     * @return a character representing the chosen direction
     *          N - North
     *          S - South
     *          W - West
     *          E - East
     * @throws UnavailableUserException if the player is not connected or the network timeout expires
     */
    public Character chooseDirection(Player player) throws UnavailableUserException {

        Character[] directions = new Character[4];
        directions[0] = 'N';
        directions[1] = 'S';
        directions[2] = 'E';
        directions[3] = 'W';

        try {
            return executorService.submit(() -> directions[clientMap.get(player.getUsername()).singleChoice("direction",gson.toJson(directions))]).get(inactivityTime,TimeUnit.SECONDS);
        } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This method asks a player to choose a color between red, yellow and blue
     * @param player is the player who makes the choice
     * @return the enumerated color chosen
     * @throws UnavailableUserException if the player is not connected or the network timeout expires
     */
    public Color chooseColor(Player player) throws UnavailableUserException {

        Color[] colors = new Color[3];
        colors[0] = Color.RED;
        colors[1] = Color.YELLOW;
        colors[2] = Color.BLUE;

        try {
            return executorService.submit(() -> colors[clientMap.get(player.getUsername()).singleChoice("color",gson.toJson(colors))]).get(inactivityTime,TimeUnit.SECONDS);
        } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This method asks a player to make a single choice in a set of powerups
     * @param player is the player who makes the choice
     * @param arrayList is the set of powerups to choose from
     * @return the powerup chosen by the player
     * @throws UnavailableUserException if the player is not connected or the network timeout expires
     */
    public Powerup choosePowerup(Player player, ArrayList<Powerup> arrayList) throws UnavailableUserException {

        Powerup[] powerups = new Powerup[arrayList.size()];
        for (int i = 0 ; i < arrayList.size() ; i++)
            powerups[i] = arrayList.get(i);

        try {
            return executorService.submit(() -> arrayList.get(clientMap.get(player.getUsername()).singleChoice("powerup",gson.toJson(powerups)))).get(inactivityTime,TimeUnit.SECONDS);
        } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This method asks a player to choose a map.
     * More specifically, it asks to choose in a range of indexes, which meaning is defined by the users of this class
     * @param player is the player who makes the choice
     * @param min is the minimum index to choose
     * @param max is the maximum index to choose
     * @return an int value that represents the chosen map
     * @throws UnavailableUserException if the player is not connected or the network timeout expires
     */
    public int chooseMap(Player player, int min, int max) throws UnavailableUserException {

        int[] maps = new int[max-min+1];
        for (int i = 0 ; i <= max-min ; i++)
            maps[i] = min+i;

        try {
            return executorService.submit(() -> maps[clientMap.get(player.getUsername()).singleChoice("map",gson.toJson(maps))]).get(inactivityTime,TimeUnit.SECONDS);
        } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This method asks a player to choose a game mode between normal and domination
     * @param player is the player who makes the choice
     * @return a character representing the chosen game mode
     *          N - Normal
     *          D - Domination
     * @throws UnavailableUserException if the player is not connected or the network timeout expires
     */
    public Character chooseMode(Player player) throws UnavailableUserException {

        //TODO Remove S option
        Character[] modes = new Character[3];
        modes[0] = 'N';
        modes[1] = 'D';
        modes[2] = 'S';

        try {
            return executorService.submit(() -> modes[clientMap.get(player.getUsername()).singleChoice("mode",gson.toJson(modes))]).get(inactivityTime,TimeUnit.SECONDS);
        } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This method asks a player to make a choice in a set of squares
     * @param player is the player who makes the choice
     * @param arrayList is the set of squares to choose from
     * @return the square chosen by the player
     * @throws UnavailableUserException if the player is not connected or the network timeout expires
     */
    public Square chooseSquare(Player player, ArrayList<Square> arrayList) throws UnavailableUserException {

        Square[] squares = new Square[arrayList.size()];
        for (int i = 0 ; i < arrayList.size() ; i++)
            squares[i] = arrayList.get(i);

        try {
            return executorService.submit(() -> arrayList.get(clientMap.get(player.getUsername()).singleChoice("square",gson.toJson(squares)))).get(inactivityTime,TimeUnit.SECONDS);
        } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This method asks a player to make a multiple choice in a set of powerups
     * @param player is the player who makes the choice
     * @param arrayList is the set of powerups to choose from
     * @return an ArrayList containing the powerups chosen by the player
     * @throws UnavailableUserException if the player is not connected or the network timeout expires
     */
    public ArrayList<Powerup> chooseMultiplePowerup(Player player, ArrayList<Powerup> arrayList) throws UnavailableUserException {

        Powerup[] powerups = new Powerup[arrayList.size()];
        for (int i = 0 ; i < arrayList.size() ; i++)
            powerups[i] = arrayList.get(i);

        try {
            return executorService.submit(() -> {
                ArrayList<Powerup> retList = new ArrayList<>();
                for (int i : clientMap.get(player.getUsername()).multipleChoice("powerup",gson.toJson(powerups)))
                    retList.add(arrayList.get(i));
                return retList;
            }).get(inactivityTime,TimeUnit.SECONDS);
        } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This method asks a player to make a multiple choice in a set of weapons
     * @param player is the player who makes the choice
     * @param arrayList is the set of weapons to choose from
     * @return an ArrayList containing the weapons chosen by the player
     * @throws UnavailableUserException if the player is not connected or the network timeout expires
     */
    public ArrayList<Weapon> chooseMultipleWeapon(Player player, ArrayList<Weapon> arrayList) throws UnavailableUserException {

        WeaponName[] weapons = new WeaponName[arrayList.size()];
        for (int i = 0 ; i < arrayList.size() ; i++)
            weapons[i] = arrayList.get(i).getName();

        try {
            return executorService.submit(() -> {
                ArrayList<Weapon> retList = new ArrayList<>();
                for (int i : clientMap.get(player.getUsername()).multipleChoice("weapon",gson.toJson(weapons)))
                    retList.add(arrayList.get(i));
                return retList;
            }).get(inactivityTime,TimeUnit.SECONDS);
        } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This method asks a player to answer a boolean question
     * @param player is the player who must answer
     * @param string is the boolean question
     * @return the boolean answer of the player
     * @throws UnavailableUserException if the player is not connected or the network timeout expires
     */
    public Boolean booleanQuestion(Player player, String string) throws UnavailableUserException {

        try {
            return executorService.submit(() -> clientMap.get(player.getUsername()).booleanQuestion(string)).get(inactivityTime,TimeUnit.SECONDS);
        } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
            forceLogout(player);
            throw new UnavailableUserException();
        }
    }

    /**
     * This method notifies all the client that the model has been updated
     */
    public void notifyModelUpdate() {

        for (ClientRemote c : clientMap.values()) {
            try {
                executorService.submit(() -> {
                    try {
                        c.genericWithoutResponse("notifyModelUpdate","");
                    } catch (RemoteException e) {
                        // It is useless to disconnect the client
                    }
                }).get(pingLatency,TimeUnit.SECONDS);
            } catch (NullPointerException | InterruptedException | ExecutionException | TimeoutException e) {
                // It is useless to disconnect the client
            }
        }
    }
}