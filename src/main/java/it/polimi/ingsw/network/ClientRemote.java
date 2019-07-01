package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This class contains all the remote methods of the client that can be invoked from the server.
 * These methods are parameterized to keep a lightened network implementation. Any other method should be implemented using the ones of this class
 * (I.E. The request of an answer like "How are you?" should be implemented using genericWithResponse method passing a keyword as first parameter and the question as second parameter.
 * This keyword should then be correctly interpreted from the client and managed accordingly)
 */
public interface ClientRemote extends Remote {

    int ping() throws RemoteException;
    void genericWithoutResponse(String id, String parameters) throws RemoteException;
    String genericWithResponse(String id, String parameters) throws RemoteException;
    int singleChoice(String id, String parameters) throws RemoteException;
    int[] multipleChoice(String id, String parameters) throws RemoteException;
    boolean booleanQuestion(String parameters) throws RemoteException;
}