package it.polimi.ingsw.controller;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class keeps running during the game and receives the socket method invocations from client to controller
 */
public class ControllerSocketListener implements Runnable {

    private Scanner in;
    private Controller controller;
    private ClientSocket clientSocket;

    public ControllerSocketListener(Controller controller, ClientSocket clientSocket) {

        this.controller = controller;
        this.clientSocket = clientSocket;
        System.out.println("ControllerSocketListener created");
    }

    @Override
    public void run() {
        try {
            in = new Scanner(clientSocket.getSocket().getInputStream());

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
            clientSocket.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
