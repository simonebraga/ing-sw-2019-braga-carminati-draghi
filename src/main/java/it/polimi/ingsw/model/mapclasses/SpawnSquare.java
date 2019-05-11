package it.polimi.ingsw.model.mapclasses;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.cardclasses.Weapon;

import java.util.ArrayList;

/**
 * Represent a Square that is also a SpawnPoint.
 */
public class SpawnSquare extends Square {

    /**
     * Represents the color of the SpawnPoint.
     */
    private final Color color;

    /**
     * Represents the Weapons that can be bought in the SpawnPoint.
     */
    private ArrayList<Weapon> weapons;


    public SpawnSquare(Border up, Border down, Border left, Border right, Color color) {
        super(up, down, left, right);
        this.color = color;
        this.weapons = new ArrayList<>();
    }


    public Color getColor() {
        return color;
    }

    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(ArrayList<Weapon> weapons) {
        this.weapons = weapons;
    }

    /**
     * Adds a new Weapon to the list of Weapons in the SpawnPoint.
     * @param weaponToGive The Weapon to add to the list of Weapons in the SpawnPoint.
     */
    public void addWeapon(Weapon weaponToGive){
        weaponToGive.reload();
        getWeapons().add(weaponToGive);
    }

    /**
     * Removes a Weapon from the list of Weapons in the SpawnPoint.
     * @param weaponToTake The Weapon to remove from the list of Weapons in the SpawnPoint.
     * @return The Weapon that is removed.
     */
    public Weapon takeWeapon(Weapon weaponToTake){
        weaponToTake.reload();
        getWeapons().remove(weaponToTake);
        return weaponToTake;
    }

    /**
     * Removes a Weapon from the list of Weapons in the SpawnPoint and adds a Weapon to the list of Weapons in the SpawnPoint.
     * @param weaponToTake The Weapon to remove from the list of Weapons in the SpawnPoint.
     * @param weaponToGive The Weapon to add to the list of Weapons in the SpawnPoint.
     * @return The Weapon that is removed.
     */
    public Weapon switchWeapon(Weapon weaponToTake, Weapon weaponToGive){
        weaponToGive.reload();
        weaponToTake.reload();
        addWeapon(weaponToGive);
        return takeWeapon(weaponToTake);
    }

    /**
     * This method compares two SpawnSquares objects and returns true if they are to be considered equals.
     *
     * @param obj a SpawnSquare to be compared with the one that calls this method.
     * @return true if the two SpawnSquares objects have same color, same weapons arrays,
     * same players lists and same borders values.
     * @author Draghi96
     */
    @Override
    public boolean equals(Object obj) {
        SpawnSquare spawnSquare = (SpawnSquare) obj;
        return super.equals(obj) && spawnSquare.getColor()==this.color && spawnSquare.getWeapons().equals(this.weapons);
    }
}
