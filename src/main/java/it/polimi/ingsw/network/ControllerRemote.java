package it.polimi.ingsw.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ControllerRemote extends Remote {

    void login(String s, ClientRemote c) throws RemoteException;
}