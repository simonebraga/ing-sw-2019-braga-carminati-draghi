package it.polimi.ingsw.model.cardclasses;

import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;

import java.util.ArrayList;

/**
 * This class represent all in-game weapons.
 * <p>Instances of this class will be used as components for Run-Time combinations of effects.</p>
 *
 * @author Draghi96
 * @see FunctionalFactory
 */
public class Weapon implements Card{

    /**
     * This attribute is an arraylist containing the list of ammo required to load the weapon.
     * It is initialized when the weapon is created, and it is final because weapon price does not change.
     */
    private ArrayList<Color> price;

    /**
     * This attribute is the enumeration representing the weapon identifier.
     * <p>Only one instance per identifier must exist in the game.</p>
     */
    private WeaponName name;

    /**
     * This attribute is a boolean that knows if the weapon is loaded or not.
     */
    private Boolean isLoaded;

    /**
     * This constructor initializes all values to null.
     */
    public Weapon() {
        this.price = null;
        this.name=null;
        this.isLoaded=null;
    }

    /**
     * This constructor initializes a weapon card instance.
     * <p>This constructor should only be invoked in a game setup phase.</p>
     *
     * @param price the arraylist of ammunition.
     * @param name the weapon identifier.
     * @param isLoaded a boolean that indicates if a weapon is loaded or not.
     */
    public Weapon(ArrayList<Color> price, WeaponName name, Boolean isLoaded) {
        this.price = price;
        this.name = name;
        this.isLoaded = isLoaded;
    }

    /**
     * This method return an arraylist containing all ammo boxes to be paid for a full reload of a weapon.
     * <p>The purchase price for a weapon shouldn't consider the ammo box in the first position of the list.</p>
     *
     * @return an arraylist of a Color enumeration representing the full reload price of a weapon.
     */
    public ArrayList<Color> getPrice() {
        return price;
    }

    /**
     * This method returns the weapon identifier.
     *
     * @return a WeaponName enumeration to identify all weapons.
     */
    public WeaponName getName() {
        return name;
    }

    /**
     * This method returns the loaded state of a gun.
     * <p>If a isLoaded attribute is false the weapon is not loaded and vice versa.</p>
     *
     * @return a boolean that indicates if the weapon is loaded or not.
     */
    public Boolean getLoaded() {
        return isLoaded;
    }

    /**
     * This method sets the name value to another enumerated WeaponName class.
     *
     * @param name is a WeaponName enum object to which set the new name.
     */
    public void setName(WeaponName name) {
        this.name = name;
    }

    /**
     * This method sets a new arraylist of Color as new price.
     *
     * @param price a new ArrayList of Color to which set the new price.
     */
    public void setPrice(ArrayList<Color> price) {
        this.price=price;
    }

    /**
     * This method sets the new Boolean value for the isLoaded attribute.
     *
     * @param value a Boolean to which isLoaded will be set.
     */
    public void setIsLoaded(Boolean value) {
        this.isLoaded=value;
    }

    /**
     * This method reloads a weapon.
     */
    public void reload() {
        isLoaded=true;
    }

    /**
     * This method compares two Weapon objects and returns true if they are to be considered equals.
     *
     * @param obj a Weapon object to be compared with the Weapon object that called this method.
     * @return true if two Weapon objects have the same name (There is only 1 sample of each weapon in a match).
     */
    @Override
    public boolean equals(Object obj) {
        Weapon weapon = (Weapon) obj;
        return weapon.name==this.name;
    }
}
