package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.IllegalActionException;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;

/**
 * Creates and sets the effect that shoots a vortex.
 */
public class ShootVortexCreator implements EffectsCreator{
    /**
     * The player who does the damage.
     */
    private Player player;

    /**
     * Represents if the shoot targets multiple players or only one.
     */
    private Boolean multipleTargets;

    /**
     * Default constructor. Sets all the attributes except the vortex.
     */
    public ShootVortexCreator(Player shooter, Boolean multipleTargets) {
        this.player = shooter;
        this.multipleTargets = multipleTargets;
    }

    public ShootVortexCreator() {
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Controller controller, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<FunctionalEffect> effects = new ArrayList<>();
        ArrayList<Player> playersAvailable = new ArrayList<>();
        Player target;
        ArrayList<Square> squares= new ArrayList<>(table.getGameMap().getVisibility(player.getPosition()));
        squares.remove(player.getPosition());

        Square vortex = controller.chooseSquare(player, squares);

        squares = new ArrayList<>(table.getGameMap().getRange(vortex, 1));
        squares.forEach(square -> playersAvailable.addAll(square.getPlayers()));

        if(playersAvailable.isEmpty()){
            throw new IllegalActionException();
        }

        target = controller.choosePlayer(player, playersAvailable);

        effects.add(new FunctionalFactory().createDamagePlayer(player, target, 2, 0));
        targets.getPlayersTargeted().add(target);
        targets.getPlayersDamaged().add(target);
        effects.add(new FunctionalFactory().createMove(target, vortex));

        playersAvailable.remove(target);

        if (multipleTargets){
            if(playersAvailable.isEmpty()){
                throw new IllegalActionException();
            }

            target = controller.choosePlayer(player, playersAvailable);

            effects.add(new FunctionalFactory().createDamagePlayer(player, target, 1, 0));
            targets.getPlayersTargeted().add(target);
            targets.getPlayersDamaged().add(target);
            effects.add(new FunctionalFactory().createMove(target, vortex));

            playersAvailable.remove(target);

            if(!playersAvailable.isEmpty()){
                target = controller.choosePlayer(player, playersAvailable);
                effects.add(new FunctionalFactory().createDamagePlayer(player, target, 1, 0));
                targets.getPlayersTargeted().add(target);
                targets.getPlayersDamaged().add(target);
                effects.add(new FunctionalFactory().createMove(target, vortex));
            }
        }
        return effects;
    }

    public Player getPlayer() {
        return player;
    }

    public Boolean getMultipleTargets() {
        return multipleTargets;
    }

    public void setMultipleTargets(Boolean multipleTargets) {
        this.multipleTargets = multipleTargets;
    }
}
