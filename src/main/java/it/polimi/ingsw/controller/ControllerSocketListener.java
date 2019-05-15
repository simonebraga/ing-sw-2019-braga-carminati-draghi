package it.polimi.ingsw.controller;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class keeps running during the game and receives the socket method invocations from client to controller
 * @author simonebraga
 */
public class ControllerSocketListener implements Runnable {

    private Scanner in;
    private Controller controller;
    private ClientSocket clientSocket;

    /**
     * This method initializes the class with the correct parameters
     * @param controller is the controller whose methods are invoked
     * @param clientSocket is the class used to answer to the client
     */
    public ControllerSocketListener(Controller controller, ClientSocket clientSocket) {

        this.controller = controller;
        this.clientSocket = clientSocket;
        System.out.println("ControllerSocketListener created");
    }

    /**
     * This method keeps running waiting messages from the client-side socket
     */
    @Override
    public void run() {
        try {
            in = new Scanner(clientSocket.getSocket().getInputStream());

            while (true) {

                String method = "";
                String parameters = "";
                String line = in.nextLine();
                int pos = 0;

                if (line.equals("quit")) {
                    break;
                }

                while ((pos < line.length() && (line.charAt(pos) != ';'))) {
                    pos++;
                }

                if (pos >= line.length()) {
                    System.out.println("Received invalid syntax message: " + line);
                } else {
                    method = line.substring(0,pos);
                    parameters = line.substring(pos + 1);

                    // This switch-case must be configured to invoke all the remote methods of Client with the correct parameters
                    switch (method) {
                        case "login": {
                            controller.login(parameters,clientSocket);
                            break;
                        }
                        case "logout": {
                            controller.logout(clientSocket);
                            break;
                        }
                        default: {
                            System.out.println("Received: " + line);
                        }
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
