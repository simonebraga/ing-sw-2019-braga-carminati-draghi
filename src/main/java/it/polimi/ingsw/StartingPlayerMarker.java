package it.polimi.ingsw;

/**
 * This singleton class marks the player who started the game, which is needed in the final frenzy phase
 *
 * @author simonebraga
 */
public class StartingPlayerMarker {

    private static StartingPlayerMarker instance = null;

    private Player target;

    private StartingPlayerMarker() {}

    public static StartingPlayerMarker getInstance() {

        if (instance == null)
            instance = new StartingPlayerMarker();
        return instance;

    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public Player getTarget() {
        return target;
    }

}
