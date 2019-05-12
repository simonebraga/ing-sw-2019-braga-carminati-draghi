package it.polimi.ingsw.view;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class keeps running during the game and receives the socket method invocations from controller to client
 */
public class ClientSocketListener implements Runnable {

    private Client client;
    private ControllerSocket controllerSocket;
    private Scanner in;


    public ClientSocketListener(Client client, ControllerSocket controllerSocket) {
        this.client = client;
        this.controllerSocket = controllerSocket;
        System.out.println("ClientSocketListener created");
    }

    /**
     * This method keeps running waiting messages from the socket input stream
     */
    @Override
    public void run() {

        try {
            in = new Scanner(controllerSocket.getSocket().getInputStream());
            while (true) {
                String line = in.nextLine();
                if (line.equals("quit")) {
                    break;
                }

                // This switch-case must be configured to invoke all the remote methods of Client with the correct parameters
                switch (line) {
                    default: {
                        System.out.println("Received: " + line);
                    }
                }
            }
            in.close();
            controllerSocket.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
