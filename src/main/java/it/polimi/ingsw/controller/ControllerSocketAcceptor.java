package it.polimi.ingsw.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This runnable class keeps waiting for new socket connections, and create a new thread for every socket connection in input
 */
public class ControllerSocketAcceptor implements Runnable {

    private int port = 6001;
    private ServerSocket serverSocket = null;
    private Controller controller;

    public ControllerSocketAcceptor(Controller controller) {

        this.controller = controller;
        while (serverSocket == null) {
            try {
                serverSocket  = new ServerSocket(port);
                System.out.println("ControllerSocketAcceptor ready");
            } catch (IOException e) {
                System.err.println("Something went wrong with ControllerSocketAcceptor initialization");
                port++;
            }
        }
    }

    /**
     * This method keeps running waiting socket connections
     */
    @Override
    public void run() {

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New socket connection");
                new ClientSocket(socket, controller);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
