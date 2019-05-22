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
    private CustomStream customStream;

    /**
     * This method initializes the class with the correct parameters
     * @param controller is the controller whose methods are invoked
     * @param clientSocket is the class used to answer to the client
     */
    public ControllerSocketListener(Controller controller, ClientSocket clientSocket, CustomStream customStream) {

        this.controller = controller;
        this.clientSocket = clientSocket;
        this.customStream = customStream;
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

                String line = in.nextLine();

                if (line.equals("quit")) {
                    break;
                }

                String method = getHeading(line);
                String parameters = getBody(line);

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
                    case "return": {
                        customStream.putLine(parameters);
                        break;
                    }
                    default: {
                        System.out.println("Received invalid message: " + line);
                    }
                }
            }

            in.close();
            clientSocket.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO Javadoc
    private String getHeading(String s) {
        int pos = 0;
        while ((pos < s.length()) && (s.charAt(pos) != ';'))
            pos++;
        if (pos >= s.length()) return "";
        return s.substring(0,pos);
    }

    // TODO Javadoc
    private String getBody(String s) {
        int pos = 0;
        while ((pos < s.length()) && (s.charAt(pos) != ';'))
            pos++;
        if (pos >= s.length()) return "";
        return s.substring(pos + 1);
    }
}
