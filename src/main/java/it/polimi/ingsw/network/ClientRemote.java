package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemote extends Remote {

    int ping() throws RemoteException;
    void genericWithoutResponse(String id, String parameters) throws RemoteException;
    String genericWithResponse(String id, String parameters) throws RemoteException;
    String singleChoice(String id, String parameters) throws RemoteException;
    String multipleChoice(String id, String parameters) throws RemoteException;
    Boolean booleanQuestion(String parameters) throws RemoteException;
}