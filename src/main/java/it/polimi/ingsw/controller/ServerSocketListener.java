package it.polimi.ingsw.controller;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Scanner;

/**
 * This class keeps running reading the socket stream and parsing the obtained strings.
 * Note that this class is the only one that must have access to the input stream of the socket it is associated to
 * @author simonebraga
 */
public class ServerSocketListener implements Runnable {

    private Server server;
    private ServerSocketSpeaker serverSocketSpeaker;
    private Socket socket;
    private Scanner in;
    private CustomStream customStream;

    private Gson gson = new Gson();

    /**
     * This method starts a new thread which checks whether the client associated to this listener is logged in the server.
     * When the client is found not logged in, it closes the socket so that the real client can notice that the connection is lost
     */
    private void startPingThread() {

        new Thread(() -> {
            while (true) {
                try {
                    server.ping(serverSocketSpeaker);
                } catch (RemoteException e) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }).start();
    }

    /**
     * This method is the constructor of the class. It initializes all the necessary parameters and gets the input stream of the socket
     * @param socket is the socket associated to this class
     * @param server is the reference to the server class
     * @param serverSocketSpeaker is the reference to the serverSocketSpeaker class, which is needed to write on the socket output stream
     * @param customStream is the stream used between this class and the associated serverSocketSpeaker class
     * @throws Exception if any step of the setup goes wrong
     */
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
                            startPingThread();
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
