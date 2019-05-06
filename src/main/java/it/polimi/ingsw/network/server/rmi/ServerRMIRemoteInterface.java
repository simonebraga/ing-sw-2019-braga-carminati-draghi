package it.polimi.ingsw.network.server.rmi;

import it.polimi.ingsw.network.client.rmi.ClientRMIRemoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This remote interface lists all the methods of the server available to the clients
 * @author simonebraga
 */
public interface ServerRMIRemoteInterface extends Remote {
    boolean register(String s, ClientRMIRemoteInterface o) throws RemoteException;
    void sendMessage(String s) throws RemoteException;
}
