package it.polimi.ingsw.view;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class keeps running during the game and receives the socket method invocations from controller to client
 * @author simonebraga
 */
public class ClientSocketListener implements Runnable {

    private Client client;
    private ControllerSocket controllerSocket;
    private Scanner in;


    /**
     * This method initializes the class with the correct parameters
     * @param client is the client associated to the listener
     * @param controllerSocket is the class used to communicate with the controller
     */
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

                String method = getHeading(line);
                String parameters = getBody(line);

                // This switch-case must be configured to invoke all the remote methods of Client with the correct parameters
                switch (method) {
                    case "printMessage": {
                        client.printMessage(parameters);
                        break;
                    }
                    case "singleChoice": {
                        controllerSocket.returnMessage(client.singleChoice(getHeading(parameters),getBody(parameters)));
                        break;
                    }
                    case "multipleChoice": {
                        controllerSocket.returnMessage(client.multipleChoice(getHeading(parameters),getBody(parameters)));
                        break;
                    }
                    case "booleanQuestion": {
                        controllerSocket.returnMessage(new Gson().toJson(client.booleanQuestion(parameters)));
                        break;
                    }
                    default: {
                        System.out.println("Received invalid message: " + line);
                    }
                }
            }
            in.close();
            controllerSocket.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method parses a string using our custom protocol
     * @param s The string to be parsed
     * @return firstPartOfTheString of firstPartOfTheString;lastPartOfTheString
     */
    private String getHeading(String s) {
        int pos = 0;
        while ((pos < s.length()) && (s.charAt(pos) != ';'))
            pos++;
        if (pos >= s.length()) return "";
        return s.substring(0,pos);
    }

    /**
     * This method parses a string using our custom protocol
     * @param s The string to be parsed
     * @return lastPartOfTheString of firstPartOfTheString;lastPartOfTheString
     */
    private String getBody(String s) {
        int pos = 0;
        while ((pos < s.length()) && (s.charAt(pos) != ';'))
            pos++;
        if (pos >= s.length()) return "";
        return s.substring(pos + 1);
    }
}
