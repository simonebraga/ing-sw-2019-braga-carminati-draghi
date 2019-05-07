package it.polimi.ingsw.model.playerclasses;

/**
 * This class marks the player who started the game, which is needed in the final frenzy phase
 *
 * @author simonebraga
 */
public class StartingPlayerMarker {

    private final Player target;

    public StartingPlayerMarker(Player target) {
        this.target = target;
    }

    public Player getTarget() {
        return target;
    }

}