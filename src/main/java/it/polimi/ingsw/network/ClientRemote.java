package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemote extends Remote {

    void printMessage(String s) throws RemoteException;
}