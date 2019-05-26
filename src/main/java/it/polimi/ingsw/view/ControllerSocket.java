package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.CustomStream;
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
    private CustomStream customStream;

    /**
     * This methods initializes the class with the correct parameters
     * @param socket is the socket to which this class sends messages
     * @param client is used to create a socket listener on this socket
     */
    public ControllerSocket(Socket socket, Client client) {

        this.socket = socket;
        customStream = new CustomStream();
        new Thread(new ClientSocketListener(client, this,customStream)).start();
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
    public int login(String s, ClientRemote c) throws RemoteException {
        customStream.resetBuffer();
        out.println("login;" + s);
        out.flush();
        return new Gson().fromJson(customStream.getLine(),int.class);
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
