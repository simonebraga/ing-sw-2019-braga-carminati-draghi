package it.polimi.ingsw.view;

import it.polimi.ingsw.network.ClientRemote;
import it.polimi.ingsw.network.ControllerRemote;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientRemote {

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

    public void login(String s) {

        try {
            controller.login(s,this);
        } catch (RemoteException e) {
            System.err.println("Something went wrong with the login");
        }
    }
}