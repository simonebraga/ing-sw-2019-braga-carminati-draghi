package it.polimi.ingsw.model.cardclasses;

import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.enumeratedclasses.PowerupName;

/**
 * This class represents all in-game powerups.
 * <p>Instances of this class will be used as components for Run-Time combinations of effects.</p>
 *
 * @author Draghi96
 * @see FunctionalFactory
 */
public class Powerup implements Card{

    /**
     * This attribute is the equivalent ammo value of a powerup.
     * <p>Players can choose to use a powerup card as ammo payments by considering its equivalent ammo value.</p>
     */
    private Color color;

    /**
     * This attribute is the powerup identifier.
     */
    private PowerupName name;

    /**
     * This constructor initializes all values to null.
     */
    public Powerup() {
        this.color=null;
        this.name=null;
    }

    /**
     * This constructor initializes a powerup instance and its attributes values.
     * <p>This constructor should be invoked only in a game setup phase.</p>
     *
     * @param color a Color enumeration representing the powerup equivalent ammo value.
     * @param name a PowerupName enumeration representing the powerup identifier.
     */
    public Powerup(Color color, PowerupName name) {
        this.color = color;
        this.name = name;
    }

    /**
     * This method returns the equivalent ammo value of a powerup card.
     *
     * @return a Color enumeration that represent the equivalent ammo value of a powerup card.
     */
    public Color getColor() {
        return color;
    }

    /**
     * This method returns the powerup identifier.
     *
     * @return a PowerupName enumeration that identifies powerups.
     */
    public PowerupName getName() {
        return name;
    }

    /**
     * This method sets a new value for color attribute.
     *
     * @param color a new Color to which the color attribute will be set.
     */
    public void setColor(Color color){
        this.color=color;
    }

    /**
     * This method sets a new value for the name attribute.
     *
     * @param name a new PowerupName to which the name attribute will be set.
     */
    public void setName(PowerupName name) {
        this.name=name;
    }
}
