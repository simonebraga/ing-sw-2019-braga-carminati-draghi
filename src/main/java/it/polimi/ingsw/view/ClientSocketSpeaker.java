package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.CustomStream;
import it.polimi.ingsw.network.ClientRemote;
import it.polimi.ingsw.network.ServerRemote;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * This class contains the socket implementation of the remote methods available on the server to the client
 * It implements the ServerRemote interface to make the invocation of the remote methods transparent to the client
 * @author simonebraga
 */
public class ClientSocketSpeaker implements ServerRemote {

    private Socket socket;
    private PrintWriter out;
    private int pingLatency;
    private CustomStream customStream = new CustomStream();

    private Gson gson = new Gson();

    /**
     * This method is the constructor of the class. It initializes all the necessary parameters and gets the output stream of the socket
     * @param socket is the socket associated to this class
     * @param client is the reference to the client class
     * @param pingLatency is the maximum time allowed to get an answer before considering the interlocutor as disconnected
     * @throws Exception if any step of the setup goes wrong
     */
    public ClientSocketSpeaker(Socket socket, Client client, int pingLatency) throws Exception {

        this.socket = socket;
        this.pingLatency = pingLatency;
        new Thread(new ClientSocketListener(socket,client,this,customStream)).start();
        out = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public synchronized int ping(ClientRemote c) throws RemoteException {
        try {
            if ((!socket.isClosed()) && (socket.getInetAddress().isReachable(pingLatency)))
                return 0;
            throw new RemoteException();
        } catch (IOException e) {
            throw new RemoteException();
        }
    }

    @Override
    public int login(String s, ClientRemote c) throws RemoteException {
        customStream.resetBuffer();
        out.println("login;" + s);
        out.flush();
        return gson.fromJson(customStream.getLine(),int.class);
    }

    @Override
    public void logout(ClientRemote c) throws RemoteException {
        out.println("logout;");
        out.println("quit;");
        out.flush();
    }

    @Override
    public String getModelUpdate() throws RemoteException {
        customStream.resetBuffer();
        out.println("getModelUpdate");
        out.flush();
        return gson.fromJson(customStream.getLine(),String.class);
    }

    /**
     * This method is used from the methods that return something to the caller.
     * It prints a return string on the output socket stream, to be parsed from the client
     * @param s is the string to be returned as return value
     */
    public void returnMessage(String s) {
        out.println("return;" + s);
        out.flush();
    }
}
