package it.polimi.ingsw;

import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test suit verifies if a pay effect works correctly.
 *
 * @author Draghi96
 */
class TestPay {

    /**
     * This attribute is a test player.
     */
    private Player player;

    /**
     * This attribute is a payment price.
     */
    private ArrayList<Color> price;

    /**
     * This attribute constitutes a player ammo inventory for the test.
     */
    private ArrayList<Color> ammoPocket;

    /**
     * This attribute is a payment object to be tested.
     */
    private Pay pay;

    /**
     * This method sets up all objects for the test.
     */
    @BeforeEach
    void setUp(){

        pay = new Pay();

        ammoPocket = new ArrayList<>();
        ammoPocket.add(Color.RED);
        ammoPocket.add(Color.RED);
        ammoPocket.add(Color.RED);
        ammoPocket.add(Color.BLUE);
        ammoPocket.add(Color.YELLOW);

        player = new Player(Figure.DOZER,"user");
        player.getAmmoPocket().addAmmo(ammoPocket);

        price = new ArrayList<>();
        price.add(Color.RED);
    }

    /**
     * This test verifies if a payment object decreases a player ammo inventory.
     */
    @Test
    void payDecreasesAmmo() {

        pay.doAction(player,price); //method usage

        assertEquals(2,player.getAmmoPocket().getAmmo(Color.RED));
        assertEquals(1,player.getAmmoPocket().getAmmo(Color.BLUE));
        assertEquals(1,player.getAmmoPocket().getAmmo(Color.YELLOW));

        pay.doAction(player,price);
        pay.doAction(player,price); //now should be out of red

        assertEquals(0,player.getAmmoPocket().getAmmo(Color.RED));
        assertEquals(1,player.getAmmoPocket().getAmmo(Color.BLUE));
        assertEquals(1,player.getAmmoPocket().getAmmo(Color.YELLOW));

        price.clear();
        price.add(Color.BLUE);

        pay.doAction(player,price); //should be out of blue

        assertEquals(0,player.getAmmoPocket().getAmmo(Color.RED));
        assertEquals(0,player.getAmmoPocket().getAmmo(Color.BLUE));
        assertEquals(1,player.getAmmoPocket().getAmmo(Color.YELLOW));

        price.clear();
        price.add(Color.YELLOW);

        pay.doAction(player,price); //should be out of yellow

        assertEquals(0,player.getAmmoPocket().getAmmo(Color.RED));
        assertEquals(0,player.getAmmoPocket().getAmmo(Color.BLUE));
        assertEquals(0,player.getAmmoPocket().getAmmo(Color.YELLOW));

        tearDown();
    }

    /**
     * This method frees all objects references.
     */
    @After
    void tearDown(){
        player=null;
        price=null;
        pay=null;
    }
}