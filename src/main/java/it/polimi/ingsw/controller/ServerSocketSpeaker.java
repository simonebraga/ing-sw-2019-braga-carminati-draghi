package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.network.ClientRemote;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * This class contains the socket implementation of the remote methods available on the client to the server
 * It implements the ClientRemote interface to make the invocation of the remote methods transparent to the server
 * @author simonebraga
 */
public class ServerSocketSpeaker implements ClientRemote {

    private Socket socket;
    private PrintWriter out;
    private int pingLatency;
    private CustomStream customStream = new CustomStream();

    private Gson gson = new Gson();

    /**
     * This method is the constructor of the class. It initializes all the necessary parameters and gets the output stream of the socket
     * @param socket is the socket associated to this class
     * @param server is the reference to the server class
     * @param pingLatency is the maximum time allowed to get an answer before considering the interlocutor as disconnected
     * @throws Exception if any step of the setup goes wrong
     */
    public ServerSocketSpeaker(Socket socket, Server server, int pingLatency) throws Exception {

        this.socket = socket;
        this.pingLatency = pingLatency;
        new Thread(new ServerSocketListener(socket, server, this, customStream)).start();
        out = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public synchronized int ping() throws RemoteException {
        try {
            if ((!socket.isClosed()) && (socket.getInetAddress().isReachable(pingLatency)))
                return 0;
            throw new RemoteException();
        } catch (IOException e) {
            throw new RemoteException();
        }
    }

    @Override
    public void genericWithoutResponse(String id, String parameters) throws RemoteException {
        out.println("genericWithoutResponse;" + id + ";" + parameters);
        out.flush();
    }

    @Override
    public String genericWithResponse(String id, String parameters) throws RemoteException {
        customStream.resetBuffer();
        out.println("genericWithResponse;" + id + ";" + parameters);
        out.flush();
        return customStream.getLine();
    }

    @Override
    public int singleChoice(String id, String parameters) throws RemoteException {
        customStream.resetBuffer();
        out.println("singleChoice;" + id + ";" + parameters);
        out.flush();
        return gson.fromJson(customStream.getLine(),int.class);
    }

    @Override
    public int[] multipleChoice(String id, String parameters) throws RemoteException {
        customStream.resetBuffer();
        out.println("multipleChoice;" + id + ";" + parameters);
        out.flush();
        return gson.fromJson(customStream.getLine(),int[].class);
    }

    @Override
    public Boolean booleanQuestion(String parameters) throws RemoteException {
        customStream.resetBuffer();
        out.println("booleanQuestion;" + parameters);
        out.flush();
        return gson.fromJson(customStream.getLine(),Boolean.class);
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
