package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
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
     * This attribute contains the association between nicknames and clients interfaces
     */
    private Map<String,ClientRemote> clientMap = new ConcurrentHashMap<>();

    private String remoteName;
    private String ip;
    private int port;

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

    public Set<String> getNicknameSet() {
        return clientMap.keySet();
    }

    public Map<String, ClientRemote> getClientMap() {
        return clientMap;
    }

    /**
     * This method creates a new empty clientMap
     */
    public void resetClientMap() {
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
                        int i = 10;
                        while (i > 0) {
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
    public Player choosePlayer(Player player, ArrayList<Player> arrayList) throws UnavailableUserException {

        try {
            return gson.fromJson(clientMap.get(player.getUsername()).singleChoice("player",gson.toJson(arrayList)),Player.class);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 2
    public Square chooseSquare(Player player, ArrayList<Square> arrayList) throws UnavailableUserException {

        try {
            return gson.fromJson(clientMap.get(player.getUsername()).singleChoice("square",gson.toJson(arrayList)),Square.class);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 3
    public ArrayList<Powerup> chooseMultiplePowerup(Player player, ArrayList<Powerup> arrayList) throws UnavailableUserException {

        try {
            ArrayList<Powerup> retVal = new ArrayList<>();
            ArrayList<Powerup> toCast = gson.fromJson(clientMap.get(player.getUsername()).multipleChoice("powerup",gson.toJson(arrayList)),ArrayList.class);
            for (int i = 0 ; i < toCast.size() ; i++) {
                retVal.add(toCast.get(i));
            }
            return retVal;
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 4
    public Weapon chooseWeapon(Player player, ArrayList<Weapon> arrayList) throws UnavailableUserException {

        try {
            return gson.fromJson(clientMap.get(player.getUsername()).singleChoice("weapon",gson.toJson(arrayList)),Weapon.class);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 5
    public Character chooseDirection(Player player) throws UnavailableUserException {
        try {
            String retVal = gson.fromJson(clientMap.get(player.getUsername()).singleChoice("string",gson.toJson(new ArrayList<>(Arrays.asList("North","South","West","East")))),String.class);
            switch (retVal) {
                case "North": return 'N';
                case "South": return 'S';
                case "West": return 'W';
                case "East": return 'E';
                default: {
                    System.err.println("Invalid return value");
                    return 'N';
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 6
    public String chooseString(Player player, ArrayList<String> arrayList) throws UnavailableUserException {

        try {
            return gson.fromJson(clientMap.get(player.getUsername()).singleChoice("string",gson.toJson(arrayList)),String.class);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 7
    public Boolean booleanQuestion(Player player, String string) throws UnavailableUserException {

        try {
            return clientMap.get(player.getUsername()).booleanQuestion(string);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 8
    public ArrayList<Weapon> chooseMultipleWeapon(Player player, ArrayList<Weapon> arrayList) throws UnavailableUserException {

        try {
            ArrayList<Weapon> retVal = new ArrayList<>();
            ArrayList<Weapon> toCast = gson.fromJson(clientMap.get(player.getUsername()).multipleChoice("weapon",gson.toJson(arrayList)),ArrayList.class);
            for (int i = 0 ; i < toCast.size() ; i++) {
                retVal.add(toCast.get(i));
            }
            return retVal;
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 9
    public Color chooseColor(Player player) throws UnavailableUserException {

        try {
            String retVal = gson.fromJson(clientMap.get(player.getUsername()).singleChoice("string",gson.toJson(new ArrayList<>(Arrays.asList("Red","Blue","Yellow")))),String.class);
            switch (retVal) {
                case "Red": return Color.RED;
                case "Blue": return Color.BLUE;
                case "Yellow": return Color.YELLOW;
                default: {
                    System.err.println("Invalid return value");
                    return Color.RED;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }

    // 10
    public void sendMessage(Player player, String message) throws UnavailableUserException {

        try {
            clientMap.get(player.getUsername()).printMessage(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new UnavailableUserException();
        }
    }
}