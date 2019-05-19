package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;

/**
 * Sets and creates the effects that shoots to all the players in a room.
 * <p>The room must be different from the room where the player is.</p>
 */
public class ShootRoomCreator implements EffectsCreator{
    /**
     * The player that shoots.
     */
    private Player player;

    /**
     * Represents the number of damages that the shoot does.
     */
    private Integer damages;

    /**
     * Represents the number of marks that the shoot does.
     */
    private Integer marks;

    /**
     * Default constructor. Sets all the attributes except the target.
     */
    public ShootRoomCreator(Player shooter, Integer damages, Integer marks) {
        this.player = shooter;
        this.damages = damages;
        this.marks = marks;
    }

    public ShootRoomCreator() {
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Controller controller, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<Square> squaresTarget;
        ArrayList<Square> roomTarget;
        ArrayList<FunctionalEffect> effects = new ArrayList<>();

        squaresTarget = new ArrayList<>(table.getGameMap().getVisibility(player.getPosition()));

        Square target = controller.chooseSquare(player, squaresTarget);

        roomTarget = new ArrayList<>(table.getGameMap().getRoom(target));

        if (roomTarget.containsAll(table.getGameMap().getRoom(player.getPosition()))) {
            throw new IllegalActionException();
        }

        roomTarget.forEach(square ->
            square.getPlayers().forEach(playerTarget ->{
                    effects.add(new FunctionalFactory().createDamagePlayer(player, playerTarget, damages, marks));
                    if(!targets.getPlayersTargeted().contains(playerTarget)){
                        targets.getPlayersTargeted().add(playerTarget);
                    }
                    if(!targets.getPlayersDamaged().contains(playerTarget)){
                        targets.getPlayersDamaged().add(playerTarget);
                    }
            }));

        if(effects.isEmpty()){
            throw new IllegalActionException();
        }else{
            return effects;
        }
    }

    public Player getPlayer() {
        return player;
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
}
