package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This class contains all the remote methods of the server that can be invoked from the client.
 */
public interface ServerRemote extends Remote {

    /**
     * This method should just return any value. It is used to check the connection
     */
    int ping(ClientRemote c) throws RemoteException;

    /**
     * This method should allow the client to register his remote interface on the server using an id
     * Registering the remote interface on the server, allow him to call back the client with the given remote methods
     * @param s should be the id used for the registration
     * @param c should be the remote interface of the client to be registered
     * @return an int value that represents the outcome of the login
     */
    int login(String s, ClientRemote c) throws RemoteException;

    /**
     * This method allows the client to unregister his remote interface from the server
     * @param c should be the remote interface of the client to be unregistered
     */
    void logout(ClientRemote c) throws RemoteException;

    /**
     * This method should allow the client to request an update of the model
     * @return a pocket serialized version of the model
     */
    String getModelUpdate() throws RemoteException;
}