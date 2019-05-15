package it.polimi.ingsw.model.playerclasses;

import java.util.ArrayList;

/**
 * This class represents the damage of the player
 *
 * @author simonebraga
 */
public class DamageTrack {

    /**
     * This attribute contains the damage of the player
     */
    private ArrayList<Player> damage;

    public DamageTrack() {
        damage = new ArrayList<>();
    }

    public ArrayList<Player> getDamage() {
        return damage;
    }

    /**
     * This method resets to empty state the damage track
     */
    public void resetDamage() {
        damage = new ArrayList<>();
    }

    /**
     * This method adds damage to the damage track.
     * @param player is who made damage
     * @param n is how much damage the target player received
     */
    public void addDamage(Player player, Integer n) {

        for (int i = 0; i < n; i++)
            if (damage.size() < 12)
                damage.add(player);
    }
}
