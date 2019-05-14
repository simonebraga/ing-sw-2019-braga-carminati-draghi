package it.polimi.ingsw.view;

import it.polimi.ingsw.network.ClientRemote;
import it.polimi.ingsw.network.ControllerRemote;

import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

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
            properties.load(new FileReader("resources/network_settings.properties"));

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
}