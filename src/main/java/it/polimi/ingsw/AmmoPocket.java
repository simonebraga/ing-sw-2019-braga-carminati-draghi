package it.polimi.ingsw;

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

    public EnumMap<Color, Integer> getAmmo() {
        return ammo;
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
     * @throws InsufficientAmountException is thrown if there are less ammo than how many should be removed
     */
    public void reduceAmmo(ArrayList<Color> cost) throws InsufficientAmountException {

        EnumMap<Color,Integer> convertedCost = new EnumMap<Color, Integer>(Color.class);
        for (Color color : Color.values())
            convertedCost.put(color,0);
        for (Color color : cost)
            convertedCost.put(color,convertedCost.get(color) + 1);

        for (Color color : Color.values())
            if (convertedCost.get(color) > ammo.get(color))
                throw new InsufficientAmountException();

        for (Color color : Color.values())
            ammo.put(color , ammo.get(color) - convertedCost.get(color));
    }

}
