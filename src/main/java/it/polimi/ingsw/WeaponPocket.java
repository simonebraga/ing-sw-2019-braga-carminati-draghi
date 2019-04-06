package it.polimi.ingsw;

import java.util.ArrayList;

/**
 * This class represents the set of the weapons available to the player
 *
 * @author simonebraga
 */
public class WeaponPocket {

    /**
     * This attribute contains the weapons of the player
     */
    private ArrayList<Weapon> weapons;

    public WeaponPocket() {
        this.weapons = new ArrayList<>();
    }

    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }

    /**
     * This method tries to add a weapon in the weapon pocket of the player
     * @param weapon is the weapon to be added
     * @throws FullPocketException is thrown if the weapon pocket is full
     */
    public void addWeapon(Weapon weapon) throws FullPocketException {

        if (weapons.size() < 3)
            weapons.add(weapon);
        else
            throw new FullPocketException();

    }

    /**
     * This method is used to add a weapon in the weapon pocket of the player when this is full
     * @param weapon is the weapon to be added
     * @param n is the position of the weapon to be removed
     * @return the weapon to be removed
     */
    public Weapon switchWeapon(Weapon weapon, Integer n) {

        Weapon returnWeapon = weapons.get(n);
        weapons.remove( (int) n);
        weapons.add(weapon);
        return returnWeapon;

    }

}
