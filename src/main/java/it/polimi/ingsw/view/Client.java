package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.smartmodel.SmartModel;
import it.polimi.ingsw.network.ClientRemote;
import it.polimi.ingsw.network.ServerRemote;

import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * This class contains all the necessary methods to communicate with the server.
 * It represents the client-side access to the network
 * @author simonebraga
 */
public class Client implements ClientRemote {

    private ServerRemote server;
    private ViewInterface view;

    private String remoteName;
    private String serverIp;
    private String clientIp;
    private int serverPortRMI;
    private int clientPortRMI;
    private int serverPortSocket;
    private int pingFrequency;
    private int pingLatency;

    private Gson gson = new Gson();
    ExecutorService executorService = Executors.newCachedThreadPool();

    // Utility methods

    /**
     * This method is the constructor of the class. It checks the parameters in input to determine if setup a connection using Sockets or RMI
     * @param i This parameter determines which type of connection must be used
     *          0 - RMI
     *          1 - Socket
     * @param view is the interface used to communicate with the user
     * @throws Exception if any step of the setup goes wrong
     */
    public Client(int i, ViewInterface view) throws Exception {

        Properties properties = new Properties();
        try {
            properties.load(Objects.requireNonNull(Client.class.getClassLoader().getResourceAsStream("network_settings.properties")));
        } catch (IOException e) {
            System.err.println("Error reading network_settings.properties");
            throw new Exception();
        }

        this.remoteName = properties.getProperty("remoteName");
        this.serverIp = properties.getProperty("serverIp");
        this.clientIp = properties.getProperty("clientIp");
        this.serverPortRMI = Integer.parseInt(properties.getProperty("serverRmiPort"));
        this.clientPortRMI = Integer.parseInt(properties.getProperty("clientRmiPort"));
        this.serverPortSocket = Integer.parseInt(properties.getProperty("serverSocketPort"));
        this.pingFrequency = Integer.parseInt(properties.getProperty("pingFrequency"));
        this.pingLatency = Integer.parseInt(properties.getProperty("pingLatency"));
        this.view = view;

        if (i == 0) {
            // RMI setup
            System.setProperty("java.rmi.server.hostname",clientIp);
            try {
                UnicastRemoteObject.exportObject(this,clientPortRMI);
            } catch (RemoteException e) {
                System.err.println("Error exporting remote object");
                throw new Exception();
            }
            try {
                server = (ServerRemote) LocateRegistry.getRegistry(serverIp, serverPortRMI).lookup(remoteName);
            } catch (RemoteException e) {
                System.err.println("Error getting RMI registry");
                throw new Exception();
            } catch (NotBoundException e) {
                System.err.println("Error with RMI registry remote object lookup");
                throw new Exception();
            }
        } else if (i == 1) {
            // Socket setup
            try {
                server = new ClientSocketSpeaker(new Socket(serverIp,serverPortSocket),this, pingLatency);
            } catch (Exception e) {
                System.err.println("Error with socket connection initialization");
                throw new Exception();
            }
        } else {
            System.err.println("Incorrect parameters in Client constructor");
            throw new Exception();
        }
    }

    /**
     * This method starts a thread that checks the connection with the server with a specified frequency
     * Note that the ping fails even if the server is connected but the client is not logged in
     */
    private void startPingThread() {

        new Thread(() -> {
            System.out.println("Ping thread started");
            while (true) {
                try {
                    executorService.submit(() -> {
                        try {
                            server.ping(this);
                        } catch (RemoteException e) {
                            try {
                                Thread.sleep((pingLatency + 1) * 1000);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }).get(pingLatency,TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    notifyLogout();
                    break;
                }
                try {
                    Thread.sleep(1000 * pingFrequency);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Ping thread stopped");
        }).start();
    }

    /**
     * This method is used to notify the view that the client is not logged in the server
     */
    private void notifyLogout() {
        System.out.println("Lost connection with the server");
        view.logout();
    }

    // Remote methods

    @Override
    public int ping() throws RemoteException {
        return 0;
    }

    @Override
    public void genericWithoutResponse(String id, String parameters) throws RemoteException {

        switch (id) {
            case "sendMessage": {
                view.sendMessage(parameters);
                break;
            }
            case "notifyEvent": {
                view.notifyEvent(parameters);
                break;
            }
            case "notifyModelUpdate": {
                view.notifyModelUpdate();
                break;
            }
            default: {
                System.err.println("Unsupported genericWithoutResponse id");
                throw new RemoteException();
            }
        }
    }

    @Override
    public String genericWithResponse(String id, String parameters) throws RemoteException {

        switch (id) {
            default: {
                System.err.println("Unsupported genericWithResponse id");
                throw new RemoteException();
            }
        }
    }

    @Override
    public int singleChoice(String id, String parameters) throws RemoteException {

        switch (id) {
            case "player": {
                return view.choosePlayer(gson.fromJson(parameters,Figure[].class));
            }
            case "weapon": {
                return view.chooseWeapon(gson.fromJson(parameters,WeaponName[].class));
            }
            case "string": {
                return view.chooseString(gson.fromJson(parameters,String[].class));
            }
            case "direction": {
                return view.chooseDirection(gson.fromJson(parameters,Character[].class));
            }
            case "color": {
                return view.chooseColor(gson.fromJson(parameters, Color[].class));
            }
            case "powerup": {
                return view.choosePowerup(gson.fromJson(parameters, Powerup[].class));
            }
            case "map": {
                return view.chooseMap(gson.fromJson(parameters,int[].class));
            }
            case "mode": {
                return view.chooseMode(gson.fromJson(parameters,Character[].class));
            }
            case "square": {
                return view.chooseSquare(gson.fromJson(parameters, Square[].class));
            }
            default: {
                System.err.println("Unsupported singleChoice id");
                throw new RemoteException();
            }
        }
    }

    @Override
    public int[] multipleChoice(String id, String parameters) throws RemoteException {

        switch (id) {
            case "powerup": {
                return view.chooseMultiplePowerup(gson.fromJson(parameters,Powerup[].class));
            }
            case "weapon": {
                return view.chooseMultipleWeapon(gson.fromJson(parameters,WeaponName[].class));
            }
            default: {
                System.err.println("Unsupported multipleChoice id");
                throw new RemoteException();
            }
        }
    }

    @Override
    public Boolean booleanQuestion(String parameters) throws RemoteException {

        return view.booleanQuestion(parameters);
    }

    // Network methods

    /**
     * This method is used to login on the server using a specified nickname. If the login is successful, a ping-thread is started using the express method
     * @param s is the nickname used to login
     * @return an integer positive value that represents the outcome of the login (see the login remote method for the meanings), or -1 if the connection timeout expires
     */
    public int login(String s) {

        try {
            int retVal = executorService.submit(() -> server.login(s,this)).get(pingLatency, TimeUnit.SECONDS);
            if ((retVal == 0) || (retVal == 2))
                startPingThread();
            return retVal;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return -1;
        }
    }

    /**
     * This method is used to logout from the server.
     * Note that if the logout is successful, the notification to the view of the logout is not performed by this method, but it is responsibility of the ping thread
     * Also note that the case of logout not successful (which means that the network timeout expires before the logout is completed) is not handled, because it is responsibility of the ping thread to notice that
     */
    public void logout() {

        try {
            executorService.submit(() -> {
                try {
                    server.logout(this);
                } catch (RemoteException ignored) {
                }
            }).get(pingLatency, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
        }
    }

    /**
     * This method is used to ask to the server an updated version of the game information
     * @return de-serialized SmartModel, or null pointer if the smart model does not exist
     * @throws Exception if something with the network goes wrong or the network timeout expires
     */
    public SmartModel getModelUpdate() throws Exception {
        try {
            String retVal = executorService.submit(() -> server.getModelUpdate()).get(pingLatency,TimeUnit.SECONDS);
            if (retVal == "") return null;
            return SmartModel.fromString(retVal);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new Exception();
        }
    }
}