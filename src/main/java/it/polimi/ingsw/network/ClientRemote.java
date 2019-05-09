package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface lists all the methods of the client accessible from the controller
 */
public interface ClientRemote extends Remote {

    void printMessage(String s) throws RemoteException;
}