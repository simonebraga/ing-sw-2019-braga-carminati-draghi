package it.polimi.ingsw.network.client;

public class ClientNetworkHandler {

    private String serverIp;
    private int serverPort;

    private String clientIp;
    private int clientPort;

    public ClientNetworkHandler(String serverIp, int serverPort, String clientIp, int clientPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.clientIp = clientIp;
        this.clientPort = clientPort;
    }
}
