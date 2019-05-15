package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface lists all the methods of the client accessible from the controller
 * @author simonebraga
 */
public interface ClientRemote extends Remote {

    void printMessage(String s) throws RemoteException;
    String singleChoice(String obj, String s) throws RemoteException;
    String multipleChoice(String obj, String s) throws RemoteException;
    Boolean booleanQuestion(String s) throws RemoteException;
}