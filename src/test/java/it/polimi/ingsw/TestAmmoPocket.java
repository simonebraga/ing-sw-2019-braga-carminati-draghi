package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is the test suite for AmmoPocket class
 *
 * @author simonebraga
 */
class TestAmmoPocket {
    AmmoPocket pocket;

    /**
     * This test case checks if the addition of ammo is handled correctly in both cases of normal addition, and addition to full pocket
     */
    @Test
    void testNormalAndFullPocket() {

        pocket = new AmmoPocket();
        pocket.addAmmo(new ArrayList<Color>(Arrays.asList(Color.BLUE,Color.BLUE,Color.YELLOW)));
        assertEquals(0 , pocket.getAmmo().get(Color.RED));
        assertEquals(2 , pocket.getAmmo().get(Color.BLUE));
        assertEquals(1 , pocket.getAmmo().get(Color.YELLOW));

        pocket.addAmmo(new ArrayList<Color>(Arrays.asList(Color.YELLOW,Color.YELLOW,Color.YELLOW,Color.YELLOW,Color.RED)));
        assertEquals(1 , pocket.getAmmo().get(Color.RED));
        assertEquals(2 , pocket.getAmmo().get(Color.BLUE));
        assertEquals(3 , pocket.getAmmo().get(Color.YELLOW));

    }

    /**
     * This test checks if the removal of ammo is performed correctly on a pocket with sufficient ammo
     */
    @Test
    void testRemoveSuccessfull() {

        pocket = new AmmoPocket();
        pocket.addAmmo(new ArrayList<Color>(Arrays.asList(Color.BLUE,Color.BLUE,Color.BLUE,Color.RED,Color.RED,Color.RED,Color.YELLOW,Color.YELLOW,Color.YELLOW)));
        try {
            pocket.reduceAmmo(new ArrayList<Color>(Arrays.asList(Color.BLUE,Color.RED,Color.YELLOW)));
        } catch (InsufficientAmountException e) {
            fail();
        }
        assertEquals(2 , pocket.getAmmo().get(Color.BLUE));
        assertEquals(2 , pocket.getAmmo().get(Color.RED));
        assertEquals(2 , pocket.getAmmo().get(Color.YELLOW));

    }

    /**
     * This test checks if the removal of ammo in a pocket without them is handled correctly
     */
    @Test
    void testRemoveFail() {

        pocket = new AmmoPocket();
        pocket.addAmmo(new ArrayList<Color>(Arrays.asList(Color.BLUE)));
        try {
            pocket.reduceAmmo(new ArrayList<Color>(Arrays.asList(Color.BLUE,Color.RED,Color.YELLOW)));
        } catch (InsufficientAmountException e) {
            assertTrue(true);
        }
        assertEquals(1 , pocket.getAmmo().get(Color.BLUE));
        assertEquals(0 , pocket.getAmmo().get(Color.RED));
        assertEquals(0 , pocket.getAmmo().get(Color.YELLOW));

    }

}