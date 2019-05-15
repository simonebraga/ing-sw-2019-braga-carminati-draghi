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
                        case "printMessage": {
                            client.printMessage(parameters);
                            break;
                        }
                        case "choosePlayer": {
                            //TO DO
                            break;
                        }
                        case "chooseSquare": {
                            //TO DO
                            break;
                        }
                        case "chooseMultiplePowerUps": {
                            //TO DO
                            break;
                        }
                        case "chooseWeapon": {
                            //TO DO
                            break;
                        }
                        case "chooseMultipleWeapons": {
                            //TO DO
                            break;
                        }
                        case "chooseDirection": {
                            //TO DO
                            break;
                        }
                        case "chooseString": {
                            //TO DO
                            break;
                        }
                        case "chooseYesNo": {
                            //TO DO
                            break;
                        }
                        case "chooseColor": {
                            //TO DO
                            break;
                        }
                        default: {
                            System.out.println("Received: " + line);
                        }
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
