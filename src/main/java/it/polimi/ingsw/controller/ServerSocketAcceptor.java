package it.polimi.ingsw.controller;

import java.net.ServerSocket;

/**
 * This class is the thread that keeps listening for new socket connections.
 * It accepts new socket connections and creates the correct class to let every socket communicate with the server
 * @author simonebraga
 */
public class ServerSocketAcceptor implements Runnable {

    private ServerSocket serverSocket;
    private Server server;
    private int pingLatency;

    /**
     * This method is the constructor of the class
     * @param port is the port used for socket connection
     * @param server is the reference to the server
     * @param pingLatency is the limit time for the ping to answer a request
     * @throws Exception if any step of the setup goes wrong
     */
    public ServerSocketAcceptor(int port, Server server, int pingLatency) throws Exception {

        this.server = server;
        this.pingLatency = pingLatency;
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {

        while (true) {
            try {
                new ServerSocketSpeaker(serverSocket.accept(), server, pingLatency);
            } catch (Exception e) {
                System.err.println("Failed to accept socket connection");
            }
        }
    }
}
