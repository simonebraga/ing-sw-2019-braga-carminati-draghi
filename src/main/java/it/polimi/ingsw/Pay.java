package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * This class represents payment effect objects.
 *
 * @author Draghi96
 */
public class Pay implements Effect{

    /**
     * This method implements a payment which is the relative action for this class effect.
     *
     * @param player a Player object that has to pay.
     * @param price a Color ArrayList that represents the amount of ammo boxes to be paid.
     */
    public void doAction(Player player, ArrayList<Color> price) {
        player.getAmmoPocket().reduceAmmo(price);
    }
}
