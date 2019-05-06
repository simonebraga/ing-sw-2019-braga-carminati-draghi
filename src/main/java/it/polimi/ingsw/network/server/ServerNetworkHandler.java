package it.polimi.ingsw.network.server;

public class ServerNetworkHandler {

    private String serverIp;
    private int serverPort;

    public ServerNetworkHandler(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }
}
