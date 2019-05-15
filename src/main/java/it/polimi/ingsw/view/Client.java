package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
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

    private String remoteName;
    private String serverIp;
    private String clientIp;
    private int serverPortRMI;
    private int clientPortRMI;
    private int serverPortSocket;

    private Gson gson = new Gson();

    /**
     * @param i = 0 to use RMI technology
     *          = 1 to use Socket technology
     * @throws RemoteException if there is any problem with the connection
     */
    public Client(int i) throws RemoteException {

        try {

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

    //Javadoc TO DO
    public void login(String s) {

        try {
            controller.login(s,this);
        } catch (RemoteException e) {
            System.err.println("Something went wrong with the login");
        }
    }

    //Javadoc TO DO
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

        System.out.println("Make a choice:");

        switch (obj) {
            case "player": {
                ArrayList<Player> arrayList = gson.fromJson(s,ArrayList.class);
                System.out.println(arrayList);
                int index = Integer.parseInt(new Scanner(System.in).nextLine());
                while (!((index >= 0)&&(index < arrayList.size()))) {
                    System.out.println("Not valid selection");
                    index = Integer.parseInt(new Scanner(System.in).nextLine());
                } return gson.toJson(arrayList.get(index));
            }
            case "square": {
                ArrayList<Square> arrayList = gson.fromJson(s,ArrayList.class);
                System.out.println(arrayList);
                int index = Integer.parseInt(new Scanner(System.in).nextLine());
                while (!((index >= 0)&&(index < arrayList.size()))) {
                    System.out.println("Not valid selection");
                    index = Integer.parseInt(new Scanner(System.in).nextLine());
                } return gson.toJson(arrayList.get(index));
            }
            case "string": {
                ArrayList<String> arrayList = gson.fromJson(s,ArrayList.class);
                System.out.println(arrayList);
                int index = Integer.parseInt(new Scanner(System.in).nextLine());
                while (!((index >= 0)&&(index < arrayList.size()))) {
                    System.out.println("Not valid selection");
                    index = Integer.parseInt(new Scanner(System.in).nextLine());
                } return gson.toJson(arrayList.get(index));
            }
            case "weapon": {
                ArrayList<Weapon> arrayList = gson.fromJson(s,ArrayList.class);
                System.out.println(arrayList);
                int index = Integer.parseInt(new Scanner(System.in).nextLine());
                while (!((index >= 0)&&(index < arrayList.size()))) {
                    System.out.println("Not valid selection");
                    index = Integer.parseInt(new Scanner(System.in).nextLine());
                } return gson.toJson(arrayList.get(index));
            }
            default: {
                System.err.println("Unsupported type");
            }
        }
        return null;
    }

    @Override
    public String multipleChoice(String obj, String s) throws RemoteException {
        System.out.println("Make a multiple choice:");

        switch (obj) {
            case "powerup": {
                ArrayList<Powerup> arrayList = gson.fromJson(s,ArrayList.class);
                ArrayList<Powerup> retVal = new ArrayList<>();
                for (int i = 0 ; i < arrayList.size() ; i++) {
                    System.out.println(arrayList.get(i) + "\n1. Yes\n0. No");
                    int index = Integer.parseInt(new Scanner(System.in).nextLine());
                    while (!((index == 0)||(index == 1))) {
                        System.out.println("Not valid selection");
                        index = Integer.parseInt(new Scanner(System.in).nextLine());
                    }
                    if (index == 1) retVal.add(arrayList.get(i));
                }
                return gson.toJson(retVal);
            }
            case "weapon": {
                ArrayList<Weapon> arrayList = gson.fromJson(s,ArrayList.class);
                ArrayList<Weapon> retVal = new ArrayList<>();
                for (int i = 0 ; i < arrayList.size() ; i++) {
                    System.out.println(arrayList.get(i) + "\n1. Yes\n0. No");
                    int index = Integer.parseInt(new Scanner(System.in).nextLine());
                    while (!((index == 0)||(index == 1))) {
                        System.out.println("Not valid selection");
                        index = Integer.parseInt(new Scanner(System.in).nextLine());
                    }
                    if (index == 1) retVal.add(arrayList.get(i));
                }
                return gson.toJson(retVal);
            }
            default: {
                System.err.println("Unsupported type");
            }
        }
        return null;
    }

    @Override
    public Boolean booleanQuestion(String s) throws RemoteException {
        System.out.println(s);
        System.out.println("1. Yes\n0. No");
        int index = Integer.parseInt(new Scanner(System.in).nextLine());
        while (!((index == 0)||(index == 1))) {
            System.out.println("Not valid selection");
            index = Integer.parseInt(new Scanner(System.in).nextLine());
        }

        if (index == 1) return true;
        return false;
    }
}