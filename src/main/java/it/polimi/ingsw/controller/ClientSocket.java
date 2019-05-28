package it.polimi.ingsw.controller;

import com.google.gson.Gson;
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
    private CustomStream customStream;

    /**
     * This method is the constructor of the class. It initializes the socket connection used to communicate with the client
     * @param socket is the socket used to connect with the client
     * @param controller is the server-side controller
     */
    public ClientSocket(Socket socket, Controller controller) {
        this.socket = socket;
        customStream = new CustomStream();
        new Thread(new ControllerSocketListener(controller, this, customStream)).start();

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
    public void noChoice(String obj, String s) throws RemoteException {
        out.println("noChoice;" + obj + ";" + s);
        out.flush();
    }

    @Override
    public String singleChoice(String obj, String s) throws RemoteException {
        customStream.resetBuffer();
        out.println("singleChoice;" + obj + ";" + s);
        out.flush();
        return customStream.getLine();
    }

    @Override
    public String multipleChoice(String obj, String s) throws RemoteException {
        customStream.resetBuffer();
        out.println("multipleChoice;" + obj + ";" + s);
        out.flush();
        return customStream.getLine();
    }

    @Override
    public Boolean booleanQuestion(String s) throws RemoteException {
        customStream.resetBuffer();
        out.println("booleanQuestion;" + s);
        out.flush();
        return new Gson().fromJson(customStream.getLine(),Boolean.class);
    }

    @Override
    public void notifyDisconnection(String s) throws RemoteException {
        out.println("notifyDisconnection;" + s);
        out.flush();
        return;
    }

    public void returnMessage(String s) {
        out.println("return;" + s);
        out.flush();
    }
}
