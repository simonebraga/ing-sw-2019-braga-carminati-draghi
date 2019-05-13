package it.polimi.ingsw.view;

import it.polimi.ingsw.network.ClientRemote;
import it.polimi.ingsw.network.ControllerRemote;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * This class contains the socket implementation used to call the remote methods of the controller
 */
public class ControllerSocket implements ControllerRemote {

    private Socket socket;
    private PrintWriter out;

    public ControllerSocket(Socket socket, Client client) {

        this.socket = socket;
        new Thread(new ClientSocketListener(client, this)).start();
        try {
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ControllerSocket created");
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void login(String s, ClientRemote c) throws RemoteException {
        out.println("login;" + s);
        out.flush();
    }

    @Override
    public void logout(ClientRemote c) throws RemoteException {
        out.println("logout;");
        out.flush();
    }
}
