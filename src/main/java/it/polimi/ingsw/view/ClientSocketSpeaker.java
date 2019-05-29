package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.CustomStream;
import it.polimi.ingsw.network.ClientRemote;
import it.polimi.ingsw.network.ServerRemote;

import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;

public class ClientSocketSpeaker implements ServerRemote {

    private Boolean kill = false;
    private Socket socket;
    private PrintWriter out;
    private CustomStream customStream = new CustomStream();

    private Gson gson = new Gson();

    public ClientSocketSpeaker(Socket socket, Client client) throws Exception {

        this.socket = socket;
        new Thread(new ClientSocketListener(socket,client,this,customStream)).start();
        out = new PrintWriter(socket.getOutputStream());
    }

    public synchronized void kill() {
        kill = true;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public synchronized int ping() throws RemoteException {
        if (kill) throw new RemoteException();
        return 0;
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
        out.flush();
    }

    public void returnMessage(String s) {
        out.println("return;" + s);
        out.flush();
    }
}
