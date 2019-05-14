package it.polimi.ingsw.controller;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * This runnable class keeps waiting for new socket connections, and create a new thread for every socket connection in input
 */
public class ControllerSocketAcceptor implements Runnable {

    private int port;
    private ServerSocket serverSocket = null;
    private Controller controller;

    public ControllerSocketAcceptor(Controller controller) {

        try {

            Properties properties = new Properties();
            properties.load(new FileReader("src/main/java/resources/network_settings.properties"));

            this.port = Integer.parseInt(properties.getProperty("serverSocketPort"));
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

        } catch (IOException e) {
            e.printStackTrace();
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
