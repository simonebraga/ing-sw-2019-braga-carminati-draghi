package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.CustomStream;
import it.polimi.ingsw.network.ClientRemote;
import it.polimi.ingsw.network.ServerRemote;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;

public class ClientSocketSpeaker implements ServerRemote {

    private Socket socket;
    private PrintWriter out;
    private int pingLatency;
    private CustomStream customStream = new CustomStream();

    private Gson gson = new Gson();

    public ClientSocketSpeaker(Socket socket, Client client, int pingLatency) throws Exception {

        this.socket = socket;
        this.pingLatency = pingLatency;
        new Thread(new ClientSocketListener(socket,client,this,customStream)).start();
        out = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public synchronized int ping(ClientRemote c) throws RemoteException {
        try {
            if (socket.getInetAddress().isReachable(pingLatency))
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

    public void returnMessage(String s) {
        out.println("return;" + s);
        out.flush();
    }
}
