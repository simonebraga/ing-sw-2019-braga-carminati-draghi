package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * This class represents payment effect objects.
 *
 * @author Draghi96
 */
public class Pay implements Effect{

    /**
     * This attribute is a Player object that has to pay.
     */
    private Player player;

    /**
     * This attribute is a Color ArrayList that represents the amount of ammo boxes to be paid.
     */
    private ArrayList<Color> price;

    /**
     * This method is a constructor for a pay object.
     *
     * @param player is a Player object that has to pay.
     * @param price is a Color ArrayList that represents the amount of ammo boxes to be paid.
     */
    public Pay(Player player, ArrayList<Color> price) {
        this.player = player;
        this.price = price;
    }

    /**
     * This method implements a payment which is the relative action for this class effect.
     */
    public void doAction() {
        this.player.getAmmoPocket().reduceAmmo(this.price);
    }
}
