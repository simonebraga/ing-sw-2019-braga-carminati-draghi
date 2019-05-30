package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.network.ClientRemote;
import it.polimi.ingsw.network.ServerRemote;

import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import java.util.Properties;

public class Client implements ClientRemote {

    private ServerRemote server;
    private ViewInterface view;

    private String remoteName;
    private String serverIp;
    private String clientIp;
    private int serverPortRMI;
    private int clientPortRMI;
    private int serverPortSocket;
    private int pingFrequency;

    private Gson gson = new Gson();

    // Utility methods

    public Client(int i, ViewInterface view) throws Exception {

        Properties properties = new Properties();
        try {
            properties.load(Objects.requireNonNull(Client.class.getClassLoader().getResourceAsStream("network_settings.properties")));
        } catch (IOException e) {
            System.err.println("Error reading network_settings.properties");
            throw new Exception();
        }

        this.remoteName = properties.getProperty("remoteName");
        this.serverIp = properties.getProperty("serverIp");
        this.clientIp = properties.getProperty("clientIp");
        this.serverPortRMI = Integer.parseInt(properties.getProperty("serverRmiPort"));
        this.clientPortRMI = Integer.parseInt(properties.getProperty("clientRmiPort"));
        this.serverPortSocket = Integer.parseInt(properties.getProperty("serverSocketPort"));
        this.pingFrequency = Integer.parseInt(properties.getProperty("pingFrequency"));
        this.view = view;

        if (i == 0) {
            // RMI setup
            //TODO Check if is it possible to remove this explicit setProperty, because client-side is very boring to set
            System.setProperty("java.rmi.server.hostname",clientIp);
            try {
                UnicastRemoteObject.exportObject(this,clientPortRMI);
            } catch (RemoteException e) {
                System.err.println("Error exporting remote object");
                throw new Exception();
            }
            try {
                server = (ServerRemote) LocateRegistry.getRegistry(serverIp, serverPortRMI).lookup(remoteName);
            } catch (RemoteException e) {
                System.err.println("Error getting RMI registry");
                throw new Exception();
            } catch (NotBoundException e) {
                System.err.println("Error with RMI registry remote object lookup");
                throw new Exception();
            }
        } else if (i == 1) {
            // Socket setup
            try {
                server = new ClientSocketSpeaker(new Socket(serverIp,serverPortSocket),this);
            } catch (Exception e) {
                System.err.println("Error with socket connection initialization");
                throw new Exception();
            }
        } else {
            System.err.println("Incorrect parameters in Client constructor");
            throw new Exception();
        }

        new Thread(() -> {
            while (true) {
                try {
                    server.ping();
                } catch (RemoteException e) {
                    try {
                        notifyLogout();
                        break;
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
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

    // Remote methods

    @Override
    public int ping() throws RemoteException {
        return 0;
    }

    @Override
    public void notifyLogout() throws RemoteException {
        System.out.println("Lost connection with the server");
        //TODO Call a specific method to start a new login routine on the view. It must create a new client to login again
    }

    @Override
    public void genericWithoutResponse(String id, String parameters) throws RemoteException {

        switch (id) {
            case "sendMessage": {
                //TODO Call the view to show the message in the correct way
                break;
            }
            case "notifyEvent": {
                //TODO Call the view to show the event in the correct way
                break;
            }
            default: {
                System.err.println("Unsupported genericWithoutResponse id");
                throw new RemoteException();
            }
        }
    }

    @Override
    public String genericWithResponse(String id, String parameters) throws RemoteException {

        switch (id) {
            default: {
                System.err.println("Unsupported genericWithResponse id");
                throw new RemoteException();
            }
        }
    }

    @Override
    public String singleChoice(String id, String parameters) throws RemoteException {

        switch (id) {
            case "player": {
                Figure[] figures = gson.fromJson(parameters,Figure[].class);
                return gson.toJson(view.choosePlayer(figures));
            }
            case "weapon": {
                WeaponName[] weapons = gson.fromJson(parameters,WeaponName[].class);
                return gson.toJson(view.chooseWeapon(weapons));
            }
            case "string": {
                String[] strings = gson.fromJson(parameters,String[].class);
                return gson.toJson(view.chooseString(strings));
            }
            case "powerup": {
                Powerup[] powerups = gson.fromJson(parameters,Powerup[].class);
                return gson.toJson(view.choosePowerup(powerups));
            }
            case "map": {
                String[] maps = gson.fromJson(parameters,String[].class);
                return gson.toJson(view.chooseMap(maps));
            }
            case "mode": {
                String[] modes = gson.fromJson(parameters,String[].class);
                return gson.toJson(view.chooseMode(modes));
            }
            case "save": {
                String[] saves = gson.fromJson(parameters,String[].class);
                return gson.toJson(view.chooseSave(saves));
            }
            default: {
                System.err.println("Unsupported singleChoice id");
                throw new RemoteException();
            }
        }
    }

    @Override
    public String multipleChoice(String id, String parameters) throws RemoteException {

        switch (id) {
            case "powerup": {
                Powerup[] powerups = gson.fromJson(parameters,Powerup[].class);
                return gson.toJson(view.chooseMultiplePowerups(powerups));
            }
            case "weapon": {
                WeaponName[] weapons = gson.fromJson(parameters,WeaponName[].class);
                return gson.toJson(view.chooseMultipleWeapons(weapons));
            }
            default: {
                System.err.println("Unsupported multipleChoice id");
                throw new RemoteException();
            }
        }
    }

    @Override
    public Boolean booleanQuestion(String parameters) throws RemoteException {

        return view.booleanQuestion(parameters);
    }

    // Network methods

    public int login(String s) throws Exception {

        try {
            return server.login(s,this);
        } catch (RemoteException e) {
            throw new Exception();
        }
    }

    public void logout() {

        try {
            server.logout(this);
        } catch (RemoteException e) {
            System.err.println("Something went wrong with the logout");
        }
    }
}