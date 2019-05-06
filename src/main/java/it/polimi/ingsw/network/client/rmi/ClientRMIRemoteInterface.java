package it.polimi.ingsw.network.client.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This remote interface lists all the methods of the client available to the server in callback
 * @author simonebraga
 */
public interface ClientRMIRemoteInterface extends Remote {
    void message(String s) throws RemoteException;
}
