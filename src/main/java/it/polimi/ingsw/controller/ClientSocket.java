package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.ClientRemote;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This class contains the socket implementation of the remote methods of the client
 */
public class ClientSocket implements ClientRemote {

    private Socket socket;
    private PrintWriter out;

    public ClientSocket(Socket socket, Controller controller) {
        this.socket = socket;
        new Thread(new ControllerSocketListener(controller, this)).start();

        try {
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("ClientSocket created");
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void printMessage(String s) throws RemoteException {
        out.println("printMessage;" + s);
        out.flush();
    }

    @Override
    public Player choosePlayer(ArrayList<Player> p) throws RemoteException {
        //TO DO
        return null;
    }

    @Override
    public Square chooseSquare(ArrayList<Square> s) throws RemoteException {
        //TO DO
        return null;
    }

    @Override
    public ArrayList<Powerup> chooseMultiplePowerUps(ArrayList<Powerup> p) throws RemoteException {
        //TO DO
        return null;
    }

    @Override
    public Weapon chooseWeapon(ArrayList<Weapon> w) throws RemoteException {
        //TO DO
        return null;
    }

    @Override
    public ArrayList<Weapon> chooseMultipleWeapons(ArrayList<Weapon> w) throws RemoteException {
        //TO DO
        return null;
    }

    @Override
    public char chooseDirection() throws RemoteException {
        //TO DO
        return 0;
    }

    @Override
    public String chooseString(ArrayList<String> s) throws RemoteException {
        //TO DO
        return null;
    }

    @Override
    public Boolean chooseYesNo(String s) throws RemoteException {
        //TO DO
        return null;
    }

    @Override
    public Color chooseColor() throws RemoteException {
        //TO DO
        return null;
    }
}
