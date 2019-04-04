package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * This class represents all in-game ammo tokens that can be placed on a square.
 * <p>Two kind of ammo tile exist: the first kind is a tile that gives access to three ammo;
 * the second kind is a tile that gives access to two ammo and one powerup.</p>
 *
 * @author Draghi96
 */
public class AmmoTile implements Card{

    /**
     * This attribute is a list containing ammunition boxes.
     * <p>This list can only have two or three ammunition boxes.</p>
     */
    private ArrayList<Color> ammo;

    /**
     * This attribute contains the enumeration identifier for the tile powerup.
     * <p>In case the ammo attribute is three elements long this attribute will be set to null.</p>
     */
    private PowerupName powerup;

    /**
     * This constructor initializes all ammo tile's attributes.
     *
     * @param ammo an arraylist of Color enumeration that represents the ammo value of that ammo tile.
     * @param powerup a Powerup enumeration that describes which powerup a tile gives a player access to. It can be null.
     */
    public AmmoTile(ArrayList<Color> ammo, PowerupName powerup) {
        this.ammo = ammo;
        this.powerup = powerup;
    }

    /**
     * This method returns the ammo list a tile indicates.
     *
     * @return an arraylist of Color enumeration indicating the ammo obtained by grabbing a tile.
     */
    public ArrayList<Color> getAmmo() {
        return ammo;
    }

    /**
     * This method returns the powerup identifier a tile indicates.
     *
     * @return a Color enumeration indicating the powerup obtained by grabbing a tile.
     */
    public PowerupName getPowerup() {
        return powerup;
    }
}
