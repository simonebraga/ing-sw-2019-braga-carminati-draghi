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
import it.polimi.ingsw.network.ControllerRemote;
import it.polimi.ingsw.network.UnavailableUserException;

import java.io.FileReader;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class contains all the methods used to communicate with the client-side application
 * @author simonebraga
 */
public class Controller implements ControllerRemote {

    /**
     * This attribute determines if the controller is open to new connections
     */
    private Boolean loginPhase = false;

    /**
     * This attribute contains the association between nicknames and clients interfaces, keeping only the links to the currently connected players
     */
    private Map<String,ClientRemote> clientMap = new ConcurrentHashMap<>();

    private String remoteName;
    private String ip;
    private int port;
    private int timerLength;

    private Gson gson = new Gson();

    /**
     * This method is the constructor of the class. It initializes the controller and creates the necessary objects to support RMI and Socket connections
     * @throws RemoteException if there are problems with the network during the initialization
     */
    public Controller() throws RemoteException {

        try {

            Properties properties = new Properties();
            properties.load(new FileReader("src/main/resources/network_settings.properties"));

            this.remoteName = properties.getProperty("controllerRemoteName");
            this.ip = properties.getProperty("serverIp");
            this.port = Integer.parseInt(properties.getProperty("serverRmiPort"));
            this.timerLength = Integer.parseInt(properties.getProperty("loginTimerLength"));

            new Thread(new ControllerSocketAcceptor(this)).start();
            System.setProperty("java.rmi.server.hostname",ip);
            UnicastRemoteObject.exportObject(this,port);

            try {
                LocateRegistry.createRegistry(port).bind(remoteName,this);
            } catch (AlreadyBoundException e) {
                e.printStackTrace();
            }

            System.out.println("Controller ready");

        } catch (IOException e) {
            System.err.println("Error while loading properties");
            e.printStackTrace();
        }
    }

    /**
     * This method allows the controller accepting connections during the registration phase
     */
    public synchronized void startLoginPhase() {
        loginPhase = true;
    }

    /**
     * This method stops the registration phase, and allows the controller accepting login only from users already registered
     */
    public synchronized void stopLoginPhase() {
        if ((clientMap.keySet().size() >= 3) && (clientMap.keySet().size() <= 5)) {
            loginPhase = false;
            System.out.println("Login closed");
        } else if (clientMap.keySet().size() > 5) {
            System.err.println("Something went wrong, more clients registered than allowed");
        } else {
            System.out.println("Login not closed");
        }
    }

    public synchronized Set<String> getNicknameSet() {
        return clientMap.keySet();
    }

    public synchronized Map<String, ClientRemote> getClientMap() {
        return clientMap;
    }

    /**
     * This method checks if the user associated to a player is connected
     * @param player is the player to be checked
     * @return boolean value true iff the user associated to player is connected
     */
    public synchronized Boolean isConnected(Player player) {

        if (clientMap.containsKey(player.getUsername())) return true;
        return false;
    }

    /**
     * This method is used to force teh disconnection of a player
     * @param player is the player to be disconnected
     */
    public synchronized void forceLogout(Player player) {

        if (clientMap.containsKey(player.getUsername())) {
            clientMap.remove(player.getUsername());
        }
    }

    /**
     * This method creates a new empty clientMap
     */
    public synchronized void resetClientMap() {
        clientMap = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void login(String s, ClientRemote c) throws RemoteException {

        if (loginPhase && (clientMap.keySet().size() < 5)) {

            if (!(clientMap.containsKey(s))) {
                clientMap.put(s,c);
                System.out.println(clientMap.toString());
                c.printMessage("Successful registration");

                if (clientMap.keySet().size() == 3) {
                    new Thread(()->{
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
                }

                if (clientMap.keySet().size() >= 5) {
                    stopLoginPhase();
                }

            } else if (clientMap.get(s).equals(c)){
                c.printMessage("Already registered");
            } else {
                c.printMessage("Nickname already chosen");
            }

        } else {

            if (clientMap.containsKey(s)) {
                clientMap.put(s,c);
                c.printMessage("Successful login");
            } else {
                c.printMessage("Registration not allowed");
            }

        }
    }

    @Override
    public synchronized void logout(ClientRemote c) throws RemoteException {

        if (clientMap.containsValue(c)) {
            while (clientMap.values().remove(c));
            System.out.println(clientMap.toString());
            c.printMessage("Logout successful");
        } else {
            c.printMessage("Client not registered");
        }
    }

    // 1

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
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 2
    // TODO Javadoc
    public Square chooseSquare(Player player, ArrayList<Square> arrayList) throws UnavailableUserException {
        // TODO
        return null;
    }

    // 3

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
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 4

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
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 5

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
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 6

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
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 7

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
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 8

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
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 9

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
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 10

    /**
     * This methods sends a message to a user
     * @param player Player to send the message to
     * @param message Message to be sent
     * @throws UnavailableUserException if the user is not connected
     */
    public void sendMessage(Player player, String message) throws UnavailableUserException {

        try {
            clientMap.get(player.getUsername()).printMessage(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 11

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
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 12

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
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 13

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
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 14

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
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }
}