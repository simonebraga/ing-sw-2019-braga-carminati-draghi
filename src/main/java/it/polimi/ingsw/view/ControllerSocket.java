package it.polimi.ingsw.view;

import it.polimi.ingsw.network.ClientRemote;
import it.polimi.ingsw.network.ControllerRemote;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * This class contains the socket implementation used to call the remote methods of the controller
 * @author simonebraga
 */
public class ControllerSocket implements ControllerRemote {

    private Socket socket;
    private PrintWriter out;

    /**
     * This methods initializes the class with the correct parameters
     * @param socket is the socket to which this class sends messages
     * @param client is used to create a socket listener on this socket
     */
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

    public void returnMessage(String s) {
        out.println("return;" + s);
        out.flush();
    }
}
