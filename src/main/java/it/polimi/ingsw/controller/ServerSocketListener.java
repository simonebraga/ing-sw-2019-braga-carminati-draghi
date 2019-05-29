package it.polimi.ingsw.controller;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;

public class ServerSocketListener implements Runnable {

    private Server server;
    private ServerSocketSpeaker serverSocketSpeaker;
    private Socket socket;
    private Scanner in;
    private CustomStream customStream;

    private Gson gson = new Gson();

    public ServerSocketListener(Socket socket, Server server, ServerSocketSpeaker serverSocketSpeaker, CustomStream customStream) throws Exception {

        this.server = server;
        this.serverSocketSpeaker = serverSocketSpeaker;
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
                    // This switch-case must be configured to invoke all the remote methods of Server with the correct parameters
                    switch (method) {
                        case "login": {
                            serverSocketSpeaker.returnMessage(gson.toJson(server.login(parameters,serverSocketSpeaker)));
                            break;
                        }
                        case "logout": {
                            server.logout(serverSocketSpeaker);
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
        serverSocketSpeaker.kill();
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
