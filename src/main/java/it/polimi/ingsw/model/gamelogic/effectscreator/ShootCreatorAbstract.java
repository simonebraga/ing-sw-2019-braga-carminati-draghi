package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Abstract class that represents the classes that creates a shoot effect.
 */
public abstract class ShootCreatorAbstract {

    /**
     * The player from which start the visibility.
     */
    protected Player from;

    /**
     * The player that shoots.
     */
    protected Player player;

    /**
     * Represents if the target must be visible or not.
     */
    protected Boolean visibility;

    /**
     * Represents the number of damages that the shoot does.
     */
    protected Integer damages;

    /**
     * Represents the number of marks that the shoot does.
     */
    protected Integer marks;

    /**
     * Represents the minimum distance of the target from the player.
     * <p>If a minimum distance is not required, this attribute must be put at 0.</p>
     * <p>Example: if a Weapon can shoot only in a Square different from the Square where the player is,
     * then minDist = 1.</p>
     */
    protected Integer minDist;

    /**
     * Represents the maximum distance of the target from the player.
     * <p>If a maximum distance is not required, this attribute must be put at -1.</p>
     */
    protected Integer maxDist;

    /**
     * Represents if the shoot must succeed or if it can have no target.
     */
    protected Boolean optional;

    public ShootCreatorAbstract() {
    }

    /**
     * Finds the squares when a player can shoot.
     * @param squares The ArrayList where the method inserts all the squares where the player can shoot.
     * @param table Represents all the data of the game.
     */
    protected void findSquares(ArrayList<Square> squares, GameTable table){
        ArrayList<Square> map = new ArrayList<>(table.getGameMap().getGridAsList());
        Player visibilityStarter;

        if(from != null){
            visibilityStarter = from;
        }else{
            visibilityStarter = player;
        }


        if(!visibility){
            squares.addAll(map.stream().filter(square -> !table.getGameMap().getVisibility(visibilityStarter.getPosition()).contains(square)).collect(Collectors.toList()));
        }else {
            if (maxDist >= 0){
                squares.addAll(map.stream().filter(square -> table.getGameMap().getRange(visibilityStarter.getPosition(), maxDist).contains(square) &&
                        table.getGameMap().getVisibility(visibilityStarter.getPosition()).contains(square)).collect(Collectors.toList()));
            }else{
                squares.addAll(table.getGameMap().getVisibility(visibilityStarter.getPosition()));
            }
            if(minDist > 0){
                squares.removeAll(table.getGameMap().getRange(visibilityStarter.getPosition(), minDist-1));
            }
        }
    }

    public Player getFrom() {
        return from;
    }

    public void setFrom(Player from) {
        this.from = from;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public Integer getDamages() {
        return damages;
    }

    public void setDamages(Integer damages) {
        this.damages = damages;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public Integer getMinDist() {
        return minDist;
    }

    public void setMinDist(Integer minDist) {
        this.minDist = minDist;
    }

    public Integer getMaxDist() {
        return maxDist;
    }

    public void setMaxDist(Integer maxDist) {
        this.maxDist = maxDist;
    }

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }
}
