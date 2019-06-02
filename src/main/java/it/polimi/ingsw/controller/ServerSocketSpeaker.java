package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.network.ClientRemote;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;

public class ServerSocketSpeaker implements ClientRemote {

    private Socket socket;
    private PrintWriter out;
    private CustomStream customStream = new CustomStream();

    private Gson gson = new Gson();

    public ServerSocketSpeaker(Socket socket, Server server) throws Exception {

        this.socket = socket;
        new Thread(new ServerSocketListener(socket, server, this, customStream)).start();
        out = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public synchronized int ping() throws RemoteException {
        try {
            if (!socket.isClosed())
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
    public String singleChoice(String id, String parameters) throws RemoteException {
        customStream.resetBuffer();
        out.println("singleChoice;" + id + ";" + parameters);
        out.flush();
        return customStream.getLine();
    }

    @Override
    public String multipleChoice(String id, String parameters) throws RemoteException {
        customStream.resetBuffer();
        out.println("multipleChoice;" + id + ";" + parameters);
        out.flush();
        return customStream.getLine();
    }

    @Override
    public Boolean booleanQuestion(String parameters) throws RemoteException {
        customStream.resetBuffer();
        out.println("booleanQuestion;" + parameters);
        out.flush();
        return gson.fromJson(customStream.getLine(),Boolean.class);
    }

    public void returnMessage(String s) {
        out.println("return;" + s);
        out.flush();
    }
}
