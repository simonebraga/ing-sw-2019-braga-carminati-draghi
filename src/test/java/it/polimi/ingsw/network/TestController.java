package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.view.Client;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TestController {

    @Test
    void testLogin() {

        try {
            Controller controller = new Controller();
            controller.startLoginPhase();
            ClientRemote client = new Client(0);
            controller.login("Nickname",client);

            Set<String> set = new HashSet<>();
            set.add("Nickname");
            assertEquals(set , controller.getNicknameSet());

        } catch (RemoteException e) {
            assertTrue(false);
        }
    }
}