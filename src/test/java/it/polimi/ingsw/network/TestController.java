package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.view.Client;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TestController {

    static Controller controller;

    @BeforeAll
    static void initController() {
        try {
            controller = new Controller();
        } catch (RemoteException e) {
            assertTrue(false);
        }
    }

    @BeforeEach
    void resetController() {
        controller.resetClientMap();
        controller.startLoginPhase();
    }

    @Test
    void testLoginRmi() {

        try {
            Client client = new Client(0);
            client.login("Nickname");

            Set<String> set = new HashSet<>();
            set.add("Nickname");

            assertEquals(set , controller.getNicknameSet());
        } catch (RemoteException e) {
            assertTrue(false);
        }
    }

    @Test
    void testLogoutRmi() {

        try {
            Client client1 = new Client(0);
            Client client2 = new Client(0);
            client1.login("Nickname1");
            client2.login("Nickname2");
            client1.logout();

            Set<String> set = new HashSet<>();
            set.add("Nickname2");

            assertEquals(set,controller.getNicknameSet());
        } catch (RemoteException e) {
            assertTrue(false);
        }
    }
}