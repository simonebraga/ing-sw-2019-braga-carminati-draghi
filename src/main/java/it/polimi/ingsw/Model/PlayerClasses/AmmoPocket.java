package it.polimi.ingsw.Model.PlayerClasses;

import it.polimi.ingsw.Model.EnumeratedClasses.Color;

import java.util.ArrayList;
import java.util.EnumMap;

/**
 * This class represents the ammo available to the player
 *
 * @author simonebraga
 */
public class AmmoPocket {

    /**
     * This attribute contains the ammo of the player
     */
    private EnumMap<Color,Integer> ammo;

    public AmmoPocket() {
        this.ammo = new EnumMap<Color,Integer>(Color.class);
        for (Color color : Color.values())
            ammo.put(color,0);
    }

    /**
     * This method returns the amount of the ammo of the specified color
     * @param color is the color which ammo amount is returne
     * @return amount of ammo of color
     */
    public Integer getAmmo(Color color) {
        return ammo.get(color);
    }

    /**
     * This method adds ammo to the ammo pocket of the player, considering the maximum limit, and handling locally the exceeding ammo
     * @param ammo is the ArrayList of ammo to add
     */
    public void addAmmo(ArrayList<Color> ammo) {

        for (Color color : ammo)
            if (this.ammo.get(color) < 3)
                this.ammo.put(color , this.ammo.get(color) + 1);

    }

    /**
     * This method removes from the pocket the ammo contained in the ArrayList in input
     * @param cost is the ArrayList with the ammo to be removed
     */
    public void reduceAmmo(ArrayList<Color> cost) {

        for (Color color : cost)
            ammo.put(color , ammo.get(color) - 1);
    }

}
