package it.polimi.ingsw.network.server.rmi;

import it.polimi.ingsw.network.client.rmi.ClientRMIRemoteInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is the RMI controller of the server
 * @author simonebraga
 */
public class ServerRMI implements ServerRMIRemoteInterface {

    /**
     * This attribute contains all the skeletons of the players that are using RMI technology
     */
    private ConcurrentHashMap<String, ClientRMIRemoteInterface> clientList = new ConcurrentHashMap<>();

    private String serverIp;
    private int serverPort;

    public ServerRMI(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    /**
     * This methods initializes the server stub and the RMI register. It must be invoked before any other method call
     */
    public void init() {
        System.setProperty("java.rmi.server.hostname",serverIp);
        try {
            ServerRMIRemoteInterface stub = (ServerRMIRemoteInterface) UnicastRemoteObject.exportObject(this,serverPort);
            LocateRegistry.createRegistry(serverPort).rebind("ServerRMIRemoteInterface",stub);
        } catch (RemoteException e) {
            System.err.println("Something went wrong with ServerRMI initialization");
        }
    }

    /**
     * This method register a client in the server's skeleton list
     * @param s the nickname of the client
     * @param o the skeleton of the client
     * @return boolean true iff the registration is performed correctly
     * @throws RemoteException if there is any problem with the connection
     */
    @Override
    public synchronized boolean register(String s, ClientRMIRemoteInterface o) throws RemoteException {
        if (clientList.containsKey(s)) {
            return false;
        } else {
            clientList.put(s,o);
            return true;
        }
    }

    /**
     * This method sends a message to all the clients that use RMI technology to communicate with the server
     * @param s the message sent to the clients
     * @throws RemoteException when something goes wrong when connecting to the client
     */
    public void sendMessage(String s) throws RemoteException {
        for (Map.Entry<String,ClientRMIRemoteInterface> entry : clientList.entrySet()) {
            try {
                entry.getValue().message("Hello from a server");
            } catch (RemoteException e) {
                System.err.println("Error in sending message to client: " + entry.getValue());
            }
        }
    }
}
