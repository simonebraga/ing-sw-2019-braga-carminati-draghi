package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.ClientRemote;
import it.polimi.ingsw.network.ControllerRemote;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class contains all the methods used to communicate with the client-side application
 * @author simonebraga
 */
public class Controller extends UnicastRemoteObject implements ControllerRemote {

    /**
     * This attribute determines if the controller is open to new connections
     */
    private Boolean loginPhase = false;

    /**
     * This attribute contains the association between nicknames and clients interfaces
     */
    private Map<String,ClientRemote> clientMap = new ConcurrentHashMap<>();

    private String remoteName = "ControllerRemote";
    private String ip = "127.0.0.1";
    private int port = 5001;

    public Controller() throws RemoteException {

        new Thread(new ControllerSocketAcceptor(this)).start();
        LocateRegistry.createRegistry(port).rebind(remoteName,this);
        System.out.println("Controller ready");
    }

    /**
     * This method allows the controller accepting connections during the registration phase
     */
    public synchronized void startLoginPhase() {
        loginPhase = true;
    }

    /**
     * This method stops the registration phase, and allows the controller accepting login only from users already registered
     */
    public synchronized void stopLoginPhase() {
        if ((clientMap.keySet().size() >= 3) && (clientMap.keySet().size() <= 5)) {
            loginPhase = false;
            System.out.println("Login closed");
        } else if (clientMap.keySet().size() > 5) {
            System.err.println("Something went wrong, more clients registered than allowed");
        } else {
            System.out.println("Login not closed");
        }
    }

    /**
     * This method is used to get the list of the registered nicknames
     * @return a set containing all the registered nicknames
     */
    public Set<String> getNicknameSet() {
        return clientMap.keySet();
    }

    /**
     * This remote method is used by the client to login/register themselves
     * @param s is the nickname for the registration
     * @param c is the client associated to the nickname
     * @throws RemoteException when there is any problem during the connection
     */
    @Override
    public synchronized void login(String s, ClientRemote c) throws RemoteException {

        if (loginPhase && (clientMap.keySet().size() < 5)) {

            if (!(clientMap.containsKey(s))) {
                clientMap.put(s,c);
                System.out.println(clientMap.toString());
                c.printMessage("Successful registration");

                if (clientMap.keySet().size() == 3) {
                    new Thread(()->{
                        int i = 10;
                        while (i > 0) {
                            System.out.println("Closing login in "+ i +" seconds");
                            i--;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        stopLoginPhase();
                    }).start();
                }

                if (clientMap.keySet().size() >= 5) {
                    stopLoginPhase();
                }

            } else if (clientMap.get(s).equals(c)){
                c.printMessage("Already registered");
            } else {
                c.printMessage("Nickname already chosen");
            }

        } else {

            if (clientMap.containsKey(s)) {
                clientMap.put(s,c);
                c.printMessage("Successful login");
            } else {
                c.printMessage("Registration not allowed");
            }

        }
    }

    /**
     * This method removes a client from the client map
     * @param c the client to be removed
     * @throws RemoteException if there is any issue with the network
     */
    @Override
    public synchronized void logout(ClientRemote c) throws RemoteException {

        if (clientMap.containsValue(c)) {
            while (clientMap.values().remove(c));
            System.out.println(clientMap.toString());
            c.printMessage("Logout successful");
        } else {
            c.printMessage("Client not registered");
        }
    }
}