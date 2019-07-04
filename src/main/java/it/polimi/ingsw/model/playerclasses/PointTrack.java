package it.polimi.ingsw.model.playerclasses;

import java.util.ArrayList;

/**
 * This class contains the points for damage of the player
 *
 * @author simonebraga
 */
public class PointTrack {

    /**
     * This attribute contains the points obtained killing the player
     */
    private ArrayList<Integer> value;

    /**
     * This constructor sets all attributes to null.<br>
     *     It's mainly used in Jackson JSON files fetching.
     *
     * @author Draghi96
     */
    public PointTrack() {
        this.value=new ArrayList<>();
    }

    /**
     * This method is the constructor of the class
     * @param value is the default points-for-damage list, and must be specified by the caller
     */
    public PointTrack(ArrayList<Integer> value) {
        this.value = value;
    }

    public ArrayList<Integer> getValue() {
        return value;
    }

    public void setValue(ArrayList<Integer> value) {
        this.value = new ArrayList<>(value);
    }

    /**
     * This method reduces the maximum amount of points obtained killing the player
     */
    public void reduceValue() {
        if (this.value.size() > 0)
            this.value.remove(0);
    }

}
