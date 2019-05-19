package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;

/**
 * Sets and creates the chain of shooting effects, used by the T.H.O.R.
 * weapon.
 */
public class ShootChainCreator implements EffectsCreator{

    /**
     * The player that shoots.
     */
    private Player player;

    /**
     * The number of times that shooting reflects on another player.
     */
    private Integer numberOfReflections;

    public ShootChainCreator(Player shooter, Integer numberOfReflection) {
        this.player = shooter;
        this.numberOfReflections = numberOfReflection;
    }

    public ShootChainCreator() {
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Controller controller, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<FunctionalEffect> effects;
        ShootCreator shootCreator;
        ShootCreator shootCreator1;
        ShootCreator shootCreator2;

        shootCreator = new ShootCreator(player, player, true, 2, 0, false);
        effects = new ArrayList<> (shootCreator.run(controller, table, targets));

        if(numberOfReflections < 1){
            return effects;
        }

        shootCreator1 = new ShootCreator(shootCreator.getTarget(), player, true, 1, 0, false);
        effects.addAll(shootCreator1.run(controller, table, targets));

        if(numberOfReflections <2){
            if(shootCreator1.getTarget() != player) {
                return effects;
            }else{
                throw new IllegalActionException();
            }
        }

        shootCreator2 =new ShootCreator(shootCreator1.getTarget(), player, true, 2, 0, false);
        effects.addAll(shootCreator2.run(controller, table, targets));

        if(shootCreator2.getTarget() != player && shootCreator.getTarget()!=shootCreator2.getTarget()){
            return effects;
        }else{
            throw new IllegalActionException();
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Integer getNumberOfReflections() {
        return numberOfReflections;
    }

    public void setNumberOfReflections(Integer numberOfReflections) {
        this.numberOfReflections = numberOfReflections;
    }
}
