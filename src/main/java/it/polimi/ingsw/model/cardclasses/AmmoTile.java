package it.polimi.ingsw.model.cardclasses;

import it.polimi.ingsw.model.enumeratedclasses.Color;

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
     * This attribute contains the number of powerups that the tile allows to draw
     * <p>In case the ammo attribute is three elements long this attribute will be set to 0.</p>
     */
    private Integer powerup;

    /**
     * This constructor initializes all attributes to null.
     */
    public AmmoTile() {
        this.ammo=null;
        this.powerup=null;
    }

    /**
     * This constructor initializes all ammo tile's attributes.
     *
     * @param ammo an arraylist of Color enumeration that represents the ammo value of that ammo tile.
     * @param powerup an integer value that represents how many powerup cards the tile allows to draw.
     */
    public AmmoTile(ArrayList<Color> ammo, Integer powerup) {
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
     * This method returns the powerups number.
     *
     * @return an integer indicating the number powerups obtained by grabbing a tile.
     */
    public Integer getPowerup() {
        return powerup;
    }

    /**
     * This method sets a new ArrayList of Color as new ammo list.
     *
     * @param ammo a new ArrayList of Color to which ammo will be set.
     */
    public void setAmmo(ArrayList<Color> ammo) {
        this.ammo = ammo;
    }

    /**
     * This method sets a new value for the powerup attribute.
     *
     * @param powerup an Integer value to which powerup will be changed to.
     */
    public void setPowerup(Integer powerup) {
        this.powerup = powerup;
    }
}