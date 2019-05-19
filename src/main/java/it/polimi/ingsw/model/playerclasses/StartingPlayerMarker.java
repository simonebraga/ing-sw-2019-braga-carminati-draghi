package it.polimi.ingsw.model.playerclasses;

/**
 * This class marks the player who started the game, which is needed in the final frenzy phase
 *
 * @author simonebraga
 */
public class StartingPlayerMarker {

    private Player target;

    /**
     * This constructor initializes all attributes to null.<br>
     *     It is used mainly for Jackson JSON files fetching.
     *
     * @author Draghi96
     */
    public StartingPlayerMarker() {this.target=new Player();}

    public StartingPlayerMarker(Player target) {
        this.target = target;
    }

    public Player getTarget() {
        return target;
    }

    /**
     * This method is used to set a new value to the target attribute.<br>
     *     It is used mainly for Jackson JSON files fetching.
     *
     * @param target a Player object that will be the new target.
     * @author Draghi96
     */
    public void setTarget(Player target) {
        this.target=target;
    }

}