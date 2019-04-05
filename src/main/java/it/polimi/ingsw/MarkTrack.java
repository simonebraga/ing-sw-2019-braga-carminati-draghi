package it.polimi.ingsw;

import java.util.HashMap;

/**
 * This class represents the marks of the player
 */
public class MarkTrack {

    private HashMap<Player,Integer> marks;

    public MarkTrack() {
        marks = new HashMap<>();
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
