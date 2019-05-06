package it.polimi.ingsw.network.client.rmi;

import it.polimi.ingsw.network.server.rmi.ServerRMIRemoteInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class ClientRMI implements ClientRMIRemoteInterface {

    private String serverIp;
    private int serverPort;

    private String clientIp;
    private int clientPort;

    private String nickname;

    public ClientRMI(String serverIp, int serverPort, String clientIp, int clientPort, String nickname) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.clientIp = clientIp;
        this.clientPort = clientPort;
        this.nickname = nickname;
    }

    /**
     * This methods sets the correct parameters for the RMI registry binding
     */
    public void init() {
        System.setProperty("java.rmi.server.hostname",clientIp);
    }

    /**
     * This methods registers the client on the server's active clients list
     */
    public void register() {
        try {
            ServerRMIRemoteInterface stub = (ServerRMIRemoteInterface) LocateRegistry.getRegistry(serverIp,serverPort).lookup("ServerRMIRemoteInterface");
            UnicastRemoteObject.exportObject(this,clientPort);
            stub.register(nickname,this);
        } catch (Exception e) {
            System.err.println("Failed Registration");
        }
    }

    /**
     * This methods prints a message from the server
     * @param s the message from the server
     * @throws RemoteException if there is any problem during the communication
     */
    @Override
    public void message(String s) throws RemoteException {
        System.out.println("Message from the server: " + s);
    }

}
