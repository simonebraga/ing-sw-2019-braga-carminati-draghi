package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.CustomStream;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * This class keeps running reading the socket stream and parsing the obtained strings
 * @author simonebraga
 */
public class ClientSocketListener implements Runnable {

    private Client client;
    private ClientSocketSpeaker clientSocketSpeaker;
    private Socket socket;
    private Scanner in;
    private CustomStream customStream;

    private Gson gson = new Gson();

    /**
     * This method is the constructor of the class. It initializes all the necessary parameters and gets the input stream of the socket
     * @param socket is the socket associated to this class
     * @param client is the reference to the client class
     * @param clientSocketSpeaker is the reference to the clientSocketSpeaker class, which is needed to write on the socket output stream
     * @param customStream is the stream used between this class and the associated clientSocketSpeaker class
     * @throws Exception if any step of the setup goes wrong
     */
    public ClientSocketListener(Socket socket, Client client, ClientSocketSpeaker clientSocketSpeaker, CustomStream customStream) throws Exception {

        this.client = client;
        this.clientSocketSpeaker = clientSocketSpeaker;
        this.socket = socket;
        this.customStream = customStream;
        in = new Scanner(socket.getInputStream());
    }

    @Override
    public void run() {

        while (true) {
            try {
                String line = in.nextLine();
                if (line.equals("quit;"))
                    break;

                String method = getHeading(line);
                String parameters = getBody(line);
                // This switch-case must be configured to invoke all the remote methods of Client with the correct parameters
                switch (method) {
                    case "genericWithoutResponse": {
                        new Thread(() -> {
                            try {
                                client.genericWithoutResponse(getHeading(parameters),getBody(parameters));
                            } catch (RemoteException e) {
                            }
                        }).start();
                        break;
                    }
                    case "genericWithResponse": {
                        new Thread(() -> {
                            try {
                                clientSocketSpeaker.returnMessage(client.genericWithResponse(getHeading(parameters),getBody(parameters)));
                            } catch (RemoteException e) {
                            }
                        }).start();
                        break;
                    }
                    case "singleChoice": {
                        new Thread(() -> {
                            try {
                                clientSocketSpeaker.returnMessage(gson.toJson(client.singleChoice(getHeading(parameters),getBody(parameters))));
                            } catch (RemoteException e) {
                            }
                        }).start();
                        break;
                    }
                    case "multipleChoice": {
                        new Thread(() -> {
                            try {
                                clientSocketSpeaker.returnMessage(gson.toJson(client.multipleChoice(getHeading(parameters),getBody(parameters))));
                            } catch (RemoteException e) {
                            }
                        }).start();
                        break;
                    }
                    case "booleanQuestion": {
                        new Thread(() -> {
                            try {
                                clientSocketSpeaker.returnMessage(gson.toJson(client.booleanQuestion(parameters)));
                            } catch (RemoteException e) {
                            }
                        }).start();
                        break;
                    }
                    case "return": {
                        customStream.putLine(parameters);
                        break;
                    }
                }
            } catch (Exception e) {
                break;
            }
        }
        in.close();
        try {
            socket.close();
        } catch (IOException ignored) {
            // It is useless to handle this exception, because if thrown the socket is already closed
        }
    }

    /**
     * This method is used to parse the strings in input according to a custom protocol
     * @param s is the string to be parsed (I.E. print;HelloWorld)
     * @return the string before ; (I.E. print)
     */
    private String getHeading(String s) {
        int pos = 0;
        while ((pos < s.length()) && (s.charAt(pos) != ';'))
            pos++;
        if (pos >= s.length()) return "";
        return s.substring(0,pos);
    }

    /**
     * This method is used to parse the strings in input according to a custom protocol
     * @param s is the string to be parsed (I.E. print;HelloWorld)
     * @return the string after ; (I.E. HelloWorld)
     */
    private String getBody(String s) {
        int pos = 0;
        while ((pos < s.length()) && (s.charAt(pos) != ';'))
            pos++;
        if (pos >= s.length()) return "";
        return s.substring(pos + 1);
    }
}
