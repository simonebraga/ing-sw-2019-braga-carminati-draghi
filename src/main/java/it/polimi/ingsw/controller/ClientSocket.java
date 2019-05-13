package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.ClientRemote;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;

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
}
