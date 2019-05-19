package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;

/**
 * Sets and creates the effect that shoots to all the players in a Square.
 */
public class ShootAOECreator extends ShootCreatorAbstract implements EffectsCreator{

    /**
     * Default constructor. Sets all the attributes except the target.
     */
    public ShootAOECreator(Player from, Player shooter, Boolean visibility, Integer damages,
                           Integer marks, Integer minDist, Integer maxDist, Boolean optional) {
        this.from = from;
        this.player = shooter;
        this.visibility = visibility;
        this.damages = damages;
        this.marks = marks;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.optional = optional;
    }

    public ShootAOECreator() {
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Controller controller, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<Square> squaresTarget = new ArrayList<>();
        ArrayList<FunctionalEffect> effects= new ArrayList<>();
        Square target;

        findSquares(squaresTarget, table);

        target = controller.chooseSquare(player, squaresTarget);

        target.getPlayers().forEach(a -> {
            if(a!= player) {
                effects.add(new FunctionalFactory().createDamagePlayer(player, a, damages, marks));
            }
            if(!targets.getPlayersTargeted().contains(a)){
                targets.getPlayersTargeted().add(a);
            }
            if(!targets.getPlayersDamaged().contains(a) && damages>0){
                    targets.getPlayersDamaged().add(a);
            }
        });
        if(!optional && effects.isEmpty()){
            throw new IllegalActionException();
        }
        return effects;
    }
}
