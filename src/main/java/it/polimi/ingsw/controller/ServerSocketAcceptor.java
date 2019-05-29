package it.polimi.ingsw.controller;

import java.net.ServerSocket;

public class ServerSocketAcceptor implements Runnable {

    private ServerSocket serverSocket;
    private Server server;

    public ServerSocketAcceptor(int port, Server server) throws Exception {

        this.server = server;
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {

        while (true) {
            try {
                new ServerSocketSpeaker(serverSocket.accept(), server);
            } catch (Exception e) {
                System.err.println("Failed to accept socket connection");
            }
        }
    }
}
