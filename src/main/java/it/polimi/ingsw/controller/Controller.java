package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.ClientRemote;
import it.polimi.ingsw.network.ControllerRemote;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Controller extends UnicastRemoteObject implements ControllerRemote {

    private Boolean loginPhase = false;
    private Map<String,ClientRemote> clientMap = new ConcurrentHashMap<>();

    private String remoteName = "ControllerRemote";
    private String ip = "127.0.0.1";
    private int port = 5001;

    protected Controller() throws RemoteException {

        LocateRegistry.createRegistry(port).rebind(remoteName,this);
        System.out.println("Ready");
    }

    public synchronized void startLoginPhase() {
        loginPhase = true;
    }

    public synchronized void stopLoginPhase() {
        loginPhase = false;
    }

    public Set<String> getNicknameSet() {
        return clientMap.keySet();
    }

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
                        System.out.println("Login closed");
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
}