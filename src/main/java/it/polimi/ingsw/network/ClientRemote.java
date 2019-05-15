package it.polimi.ingsw.network;

import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This interface lists all the methods of the client accessible from the controller
 */
public interface ClientRemote extends Remote {

    void printMessage(String s) throws RemoteException;
    Player choosePlayer(ArrayList<Player> p) throws RemoteException;
    Square chooseSquare(ArrayList<Square> s) throws RemoteException;
    ArrayList<Powerup> chooseMultiplePowerUps(ArrayList<Powerup> p) throws RemoteException;
    Weapon chooseWeapon(ArrayList<Weapon> w) throws RemoteException;
    ArrayList<Weapon> chooseMultipleWeapons(ArrayList<Weapon> w) throws RemoteException;
    char chooseDirection() throws RemoteException;
    String chooseString(ArrayList<String> s) throws RemoteException;
    Boolean chooseYesNo(String s) throws RemoteException;
    Color chooseColor() throws RemoteException;
}