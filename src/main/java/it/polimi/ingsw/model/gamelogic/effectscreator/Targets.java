package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
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

    /**
     * The spawn squares that have been damaged from a shoot.
     * <p>This attribute is used only in domination mode.</p>
     */
    private ArrayList<DominationSpawnSquare> squaresDamaged;

    public Targets() {
        playersDamaged = new ArrayList<>();
        playersTargeted = new ArrayList<>();
        squaresDamaged = new ArrayList<>();
    }

    public Targets(ArrayList<DominationSpawnSquare> squaresDamaged) {
        playersDamaged = new ArrayList<>();
        playersTargeted = new ArrayList<>();
        this.squaresDamaged = squaresDamaged;
    }

    public ArrayList<Player> getPlayersTargeted() {
        return playersTargeted;
    }

    public ArrayList<Player> getPlayersDamaged() {
        return playersDamaged;
    }

    public ArrayList<DominationSpawnSquare> getSquaresDamaged() {
        return squaresDamaged;
    }

    /**
     * Resets the targets list between two actions.
     * <p>It doesn't reset the list of spawn squares because each spawn square can be
     * hit only one time each turn.</p>
     */
    public void reset(){
        playersDamaged = new ArrayList<>();
        playersTargeted = new ArrayList<>();
    }
}
