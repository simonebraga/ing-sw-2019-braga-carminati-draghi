package it.polimi.ingsw.model.playerclasses;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import it.polimi.ingsw.model.enumeratedclasses.Color;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

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
        this.ammo = new EnumMap<>(Color.class);
    }

    /**
     * This method returns the amount of the ammo of the specified color
     * @param color is the color which ammo amount is returne
     * @return amount of ammo of color
     */
    public Integer getAmmo(Color color) {
        if (this.ammo.containsKey(color))
            return ammo.get(color);
        return 0;
    }

    /**
     * This method sets a new value for ammo attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param ammo an EnumMap of Color objects associated to Integer objects that will be the new attribute value.
     * @author Draghi96
     */
    public void setAmmo(EnumMap<Color,Integer> ammo) {
        this.ammo=ammo;
    }

    /**
     * This method adds ammo to the ammo pocket of the player, considering the maximum limit, and handling locally the exceeding ammo
     * @param ammo is the ArrayList of ammo to add
     */
    public void addAmmo(ArrayList<Color> ammo) {

        for (Color color : ammo) {
            if (this.ammo.containsKey(color)) {
                if (this.ammo.get(color) < 3)
                    this.ammo.put(color , this.ammo.get(color) + 1);
            } else this.ammo.put(color,1);
        }
    }

    /**
     * This method removes from the pocket the ammo contained in the ArrayList in input
     * @param cost is the ArrayList with the ammo to be removed
     */
    public void reduceAmmo(ArrayList<Color> cost) {

        for (Color color : cost)
            if (this.ammo.containsKey(color) && (this.ammo.get(color) > 0))
                ammo.put(color , ammo.get(color) - 1);
    }

    /**
     * This method is used to serialize this class objects to JSON using Jackson.
     *
     * @return the ammo map to be serialized by Jackson.
     * @author Draghi96
     */
    @JsonAnyGetter
    public Map<Color, Integer> toJson() {
        return ammo;
    }

    /**
     * This method is used to deserialize a JSON file into an instance of the class using Jackson.
     *
     * @param value a String read from JSON that represents a key for this class map attribute.
     * @param integer an Integer value read from JSON that represents the value associated to the key for this class map attribute.
     * @author Draghi96
     */
    @JsonAnySetter
    public void fromJson(String value, Integer integer) {
        ammo.put(Color.valueOf(value),integer);
    }

}
