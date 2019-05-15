package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.ClientRemote;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * This class contains the socket implementation of the remote methods of the client
 * @author simonebraga
 */
public class ClientSocket implements ClientRemote {

    private Socket socket;
    private PrintWriter out;

    /**
     * This method is the constructor of the class. It initializes the socket connection used to communicate with the client
     * @param socket is the socket used to connect with the client
     * @param controller is the server-side controller
     */
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
    public String singleChoice(String obj, String s) throws RemoteException {
        // TO DO
        return null;
    }

    @Override
    public String multipleChoice(String obj, String s) throws RemoteException {
        // TO DO
        return null;
    }

    @Override
    public Boolean booleanQuestion(String s) throws RemoteException {
        // TO DO
        return null;
    }
}
