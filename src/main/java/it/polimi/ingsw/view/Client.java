package it.polimi.ingsw.view;

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

    /**
     * This method login/register the client to the server using the nickname in input
     * @param s in the nickname used for the registration
     */
    public void login(String s) {

        try {
            controller.login(s,this);
        } catch (RemoteException e) {
            System.err.println("Something went wrong with the login");
        }
    }

    /**
     * This method calls the remote logout method on the server with the correct parameters
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
    public Player choosePlayer(ArrayList<Player> p) throws RemoteException {

        System.out.println("Make a choose");
        System.out.println(p);
        int index = Integer.parseInt(new Scanner(System.in).nextLine());
        while (!((index < p.size())&&(index >= 0))) {
            System.out.println("Invalid input");
            index = Integer.parseInt(new Scanner(System.in).nextLine());
        }
        return p.get(index);
    }

    @Override
    public Square chooseSquare(ArrayList<Square> s) throws RemoteException {

        System.out.println("Make a choose");
        System.out.println(s);
        int index = Integer.parseInt(new Scanner(System.in).nextLine());
        while (!((index < s.size())&&(index >= 0))) {
            System.out.println("Invalid input");
            index = Integer.parseInt(new Scanner(System.in).nextLine());
        }
        return s.get(index);
    }

    @Override
    public ArrayList<Powerup> chooseMultiplePowerUps(ArrayList<Powerup> p) throws RemoteException {

        ArrayList<Powerup> retVal = new ArrayList<>();
        System.out.println("Make a multiple choose");
        for (Powerup powerup : p) {
            System.out.println(powerup);
            System.out.println("1. Yes\n0. No");
            int index = Integer.parseInt(new Scanner(System.in).nextLine());
            while (!((index <= 1)&&(index >= 0))) {
                System.out.println("Invalid input");
                index = Integer.parseInt(new Scanner(System.in).nextLine());
            }
            if (index == 1)
                retVal.add(powerup);
        }
        return retVal;
    }

    @Override
    public Weapon chooseWeapon(ArrayList<Weapon> w) throws RemoteException {

        System.out.println("Make a choose");
        System.out.println(w);
        int index = Integer.parseInt(new Scanner(System.in).nextLine());
        while (!((index < w.size())&&(index >= 0))) {
            System.out.println("Invalid input");
            index = Integer.parseInt(new Scanner(System.in).nextLine());
        }
        return w.get(index);
    }

    @Override
    public ArrayList<Weapon> chooseMultipleWeapons(ArrayList<Weapon> w) throws RemoteException {

        ArrayList<Weapon> retVal = new ArrayList<>();
        System.out.println("Make a multiple choose");
        for (Weapon weapon : w) {
            System.out.println(weapon);
            System.out.println("1. Yes\n0. No");
            int index = Integer.parseInt(new Scanner(System.in).nextLine());
            while (!((index <= 1)&&(index >= 0))) {
                System.out.println("Invalid input");
                index = Integer.parseInt(new Scanner(System.in).nextLine());
            }
            if (index == 1)
                retVal.add(weapon);
        }
        return retVal;
    }

    @Override
    public char chooseDirection() throws RemoteException {

        System.out.println("Make a choose");
        System.out.println("1. North\n2. South\n3. West\n4. East");
        int index = Integer.parseInt(new Scanner(System.in).nextLine());
        while (!((index <= 4)&&(index >= 1))) {
            System.out.println("Invalid input");
            index = Integer.parseInt(new Scanner(System.in).nextLine());
        }
        switch (index) {
            case 1:
                return 'N';
            case 2:
                return 'S';
            case 3:
                return 'W';
            default:
                return 'E';
        }
    }

    @Override
    public String chooseString(ArrayList<String> s) throws RemoteException {

        System.out.println("Make a choose");
        System.out.println(s);
        int index = Integer.parseInt(new Scanner(System.in).nextLine());
        while (!((index < s.size())&&(index >= 0))) {
            System.out.println("Invalid input");
            index = Integer.parseInt(new Scanner(System.in).nextLine());
        }
        return s.get(index);
    }

    @Override
    public Boolean chooseYesNo(String s) throws RemoteException {

        System.out.println(s);
        System.out.println("Answer Yes/No");
        System.out.println("1. Yes\n0. No");
        int index = Integer.parseInt(new Scanner(System.in).nextLine());
        while (!((index <= 1)&&(index >= 0))) {
            System.out.println("Invalid input");
            index = Integer.parseInt(new Scanner(System.in).nextLine());
        }
        if (index == 1)
            return true;
        else
            return false;
    }

    @Override
    public Color chooseColor() throws RemoteException {
        System.out.println("Make a choose");
        System.out.println("1. RED\n2. BLUE\n3. YELLOW");
        int index = Integer.parseInt(new Scanner(System.in).nextLine());
        while (!((index <= 3)&&(index >= 1))) {
            System.out.println("Invalid input");
            index = Integer.parseInt(new Scanner(System.in).nextLine());
        }
        if (index == 1)
            return Color.RED;
        else if (index == 2)
            return Color.BLUE;
        else
            return Color.YELLOW;
    }
}