package it.polimi.ingsw.view;

import it.polimi.ingsw.network.ClientRemote;
import it.polimi.ingsw.network.ControllerRemote;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * This class contains all the necessary methods of the client-side application to communicate with the server-side applications, and implements the remote methods that can be called by the controller
 */
public class Client extends UnicastRemoteObject implements ClientRemote {

    /**
     * This attribute points the remote interface of the controller, used to communicate with the controller-side application
     */
    ControllerRemote controller;

    private String remoteName = "ControllerRemote";
    private String serverIp = "127.0.0.1";
    private int serverPort = 5001;

    protected Client() throws RemoteException {

        try {
            controller = (ControllerRemote) LocateRegistry.getRegistry(serverIp,serverPort).lookup(remoteName);
            System.out.println("Ready");
        } catch (NotBoundException e) {
            System.err.println("Something went wrong with registry lookup");
        }
    }

    @Override
    public void printMessage(String s) throws RemoteException {

        System.out.println(s);
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
}