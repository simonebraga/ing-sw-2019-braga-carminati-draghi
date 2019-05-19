package it.polimi.ingsw.view;

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

import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

/**
 * This class contains all the necessary methods of the client-side application to communicate with the server-side applications, and implements the remote methods that can be called by the controller
 * @author simonebraga
 */
public class Client implements ClientRemote {

    /**
     * This attribute points the remote interface of the controller, used to communicate with the controller-side application
     */
    private ControllerRemote controller;

    /**
     * This attribute points to the view of the client. It is an interface to allow both GUI and CLI implementations
     */
    private ViewInterface view;

    private String remoteName;
    private String serverIp;
    private String clientIp;
    private int serverPortRMI;
    private int clientPortRMI;
    private int serverPortSocket;

    private Gson gson = new Gson();

    /**
     *
     * @param i = 0 to use RMI technology
     *          = 1 to use Socket technology
     * @param view is the view implementation on the client (it must be instantiated previously, CLI or GUI)
     * @throws RemoteException if there is any problem with the connection
     */
    public Client(int i, ViewInterface view) throws RemoteException {

        try {

            this.view = view;

            Properties properties = new Properties();
            properties.load(new FileReader("src/main/resources/network_settings.properties"));

            this.remoteName = properties.getProperty("controllerRemoteName");
            this.serverIp = properties.getProperty("serverIp");
            this.clientIp = properties.getProperty("clientIp");
            this.serverPortRMI = Integer.parseInt(properties.getProperty("serverRmiPort"));
            this.clientPortRMI = Integer.parseInt(properties.getProperty("clientRmiPort"));
            this.serverPortSocket = Integer.parseInt(properties.getProperty("serverSocketPort"));

            if (i == 0) {
                // RMI setup
                try {
                    System.setProperty("java.rmi.server.hostname",clientIp);
                    UnicastRemoteObject.exportObject(this,clientPortRMI);
                    controller = (ControllerRemote) LocateRegistry.getRegistry(serverIp, serverPortRMI).lookup(remoteName);
                    System.out.println("RMI ready");
                } catch (NotBoundException e) {
                    System.err.println("Something went wrong with registry lookup");
                }
            } else if (i == 1) {
                // Socket setup
                try {
                    controller = new ControllerSocket(new Socket(serverIp,serverPortSocket),this);
                    System.out.println("Socket ready");
                } catch (IOException e) {
                    System.err.println("Something went wrong with socket setup");
                }
            } else {
                System.err.println("Incorrect parameter in constructor of Client");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to register the client on the controller. It does not handle the outcome of the login procedure; it just request to the controller to login
     * @param s is the nickname used for the login/registration
     */
    public void login(String s) {

        try {
            controller.login(s,this);
        } catch (RemoteException e) {
            System.err.println("Something went wrong with the login");
        }
    }

    /**
     * This  methods is used to logout from the server, and should be used for the correct disconnection procedure. It does not handle the outcome of the procedure.
     */
    public void logout() {

        try {
            controller.logout(this);
        } catch (RemoteException e) {
            System.err.println("Something went wrong with the logout");
        }
    }

    @Override
    public void printMessage(String s) throws RemoteException {

        System.out.println(s);
    }

    @Override
    public String singleChoice(String obj, String s) throws RemoteException {

        switch (obj) {
            case "player": {
                Figure[] figures = gson.fromJson(s,Figure[].class);
                return gson.toJson(view.choosePlayer(figures));
            }
            case "weapon": {
                WeaponName[] weapons = gson.fromJson(s,WeaponName[].class);
                return gson.toJson(view.chooseWeapon(weapons));
            }
            case "string": {
                String[] strings = gson.fromJson(s,String[].class);
                return gson.toJson(view.chooseString(strings));
            }
            case "powerup": {
                Powerup[] powerups = gson.fromJson(s,Powerup[].class);
                return gson.toJson(view.choosePowerup(powerups));
            }
            default: {
                System.err.println("Unsupported type");
                throw new RemoteException();
            }
        }
    }

    @Override
    public String multipleChoice(String obj, String s) throws RemoteException {

        switch (obj) {
            case "powerup": {
                Powerup[] powerups = gson.fromJson(s,Powerup[].class);
                return gson.toJson(view.chooseMultiplePowerups(powerups));
            }
            case "weapon": {
                WeaponName[] weapons = gson.fromJson(s,WeaponName[].class);
                return gson.toJson(view.chooseMultipleWeapons(weapons));
            }
            default: {
                System.err.println("Unsupported type");
                throw new RemoteException();
            }
        }
    }

    @Override
    public Boolean booleanQuestion(String s) throws RemoteException {

        return view.booleanQuestion(s);
    }
}