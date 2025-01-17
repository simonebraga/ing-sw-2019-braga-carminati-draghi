package it.polimi.ingsw.model.playerclasses;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the marks of the player
 *
 * @author simonebraga
 */
public class MarkTrack {

    @JsonDeserialize(keyUsing = MarkTrackMapKeyDeserializer.class)
    private HashMap<Player,Integer> marks;

    public MarkTrack() {
        this.marks = new HashMap<>();
    }

    /**
     * This method return the number of marks of a specific player
     * @param player is the player whose marks are returned
     * @return the number of marks of the player in input
     */
    public Integer getMarks(Player player) {
        if (marks.containsKey(player))
            return marks.get(player);
        return 0;
    }

    /**
     * This method returns the map containing all the marks of the player
     * @return the map containing all the marks of the player
     */
    public Map<Player,Integer> getMarks() {
        return marks;
    }

    /**
     * This method sets a new value for marks attribute.<br>
     *     It's mainly used for Jackson JSON files fetching.
     *
     * @param marks an HashMap of Player objects associated to Integer objects that will be the new attribute value.
     * @author Draghi96
     */
    public void setMarks(HashMap<Player,Integer> marks) {
        this.marks=marks;
    }

    /**
     * This method adds marks to the MarkTrack of the target player, handling locally the maximum number on the marks that every player can have
     * @param player is the player that marked the target
     * @param n is the number of marks
     */
    public void addMarks(Player player, Integer n) {
        if (marks.containsKey(player))
            marks.put(player, marks.get(player) + n);
        else
            marks.put(player, n);

        if (marks.get(player) > 3)
            marks.put(player, 3);
    }

    /**
     * This method remove the marks of a specific player and returns them to caller
     * @param player is the player whose marks are removed
     * @return the number of marks of the player in input
     */
    public Integer removeMarks(Player player) {
        if (marks.containsKey(player))
            return marks.remove(player);
        return 0;
    }
}
