package it.polimi.ingsw.network.server.rmi;

import it.polimi.ingsw.network.client.rmi.ClientRMI;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

class TestRMI {

    @Test
    void testMessage() {
        ClientRMI clientRMI = new ClientRMI("127.0.0.1",5001,"127.0.0.1",5002,"Nickname");
        ServerRMI serverRMI = new ServerRMI("127.0.0.1",5001);
        serverRMI.init();
        clientRMI.init();
        clientRMI.register();
        try {
            serverRMI.sendMessage("Hello");
        } catch (RemoteException e) {
            assertTrue(false);
        }
        assertTrue(true);
    }

}