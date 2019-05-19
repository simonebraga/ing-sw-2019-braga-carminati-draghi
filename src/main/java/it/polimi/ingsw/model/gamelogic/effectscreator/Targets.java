package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.playerclasses.Player;

import java.util.ArrayList;

/**
 * Support class used by the action manager to see who has been hit during the action.
 */
public class Targets {


    /**
     * The players that have been targeted from a shoot.
     * <p>If this array contains repetitions, the action will not be valid because the player
     * chose the same target two times.</p>
     */
    private ArrayList<Player> playersTargeted;

    /**
     * The players that have been damaged from a shoot.
     * <p>This array will be contained in playersTargeted, but it will not contain the players that
     * have only been marked.</p>
     */
    private ArrayList<Player> playersDamaged;

    public Targets() {
        playersDamaged = new ArrayList<>();
        playersTargeted = new ArrayList<>();
    }

    public ArrayList<Player> getPlayersTargeted() {
        return playersTargeted;
    }

    public ArrayList<Player> getPlayersDamaged() {
        return playersDamaged;
    }
}
