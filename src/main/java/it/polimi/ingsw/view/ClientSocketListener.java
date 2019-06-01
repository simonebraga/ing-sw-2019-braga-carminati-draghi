package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.CustomStream;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;

public class ClientSocketListener implements Runnable {

    private Client client;
    private ClientSocketSpeaker clientSocketSpeaker;
    private Socket socket;
    private Scanner in;
    private CustomStream customStream;

    private Gson gson = new Gson();

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
                try {
                    // This switch-case must be configured to invoke all the remote methods of Client with the correct parameters
                    switch (method) {
                        case "genericWithoutResponse": {
                            client.genericWithoutResponse(getHeading(parameters),getBody(parameters));
                            break;
                        }
                        case "genericWithResponse": {
                            clientSocketSpeaker.returnMessage(client.genericWithResponse(getHeading(parameters),getBody(parameters)));
                            break;
                        }
                        case "singleChoice": {
                            clientSocketSpeaker.returnMessage(client.singleChoice(getHeading(parameters),getBody(parameters)));
                            break;
                        }
                        case "multipleChoice": {
                            clientSocketSpeaker.returnMessage(client.multipleChoice(getHeading(parameters),getBody(parameters)));
                            break;
                        }
                        case "booleanQuestion": {
                            clientSocketSpeaker.returnMessage(gson.toJson(client.booleanQuestion(parameters)));
                            break;
                        }
                        case "return": {
                            customStream.putLine(parameters);
                            break;
                        }
                        default: System.out.println("Received invalid protocol message: " + line);
                    }
                } catch (RemoteException e) {
                    System.err.println("Something very bad happened: RemoteException thrown during a local invocation");
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

    private String getHeading(String s) {
        int pos = 0;
        while ((pos < s.length()) && (s.charAt(pos) != ';'))
            pos++;
        if (pos >= s.length()) return "";
        return s.substring(0,pos);
    }

    private String getBody(String s) {
        int pos = 0;
        while ((pos < s.length()) && (s.charAt(pos) != ';'))
            pos++;
        if (pos >= s.length()) return "";
        return s.substring(pos + 1);
    }
}
