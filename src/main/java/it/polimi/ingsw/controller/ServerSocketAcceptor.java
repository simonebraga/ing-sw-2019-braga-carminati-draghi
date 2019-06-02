package it.polimi.ingsw.controller;

import java.net.ServerSocket;

public class ServerSocketAcceptor implements Runnable {

    private ServerSocket serverSocket;
    private Server server;
    private int pingLatency;

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
