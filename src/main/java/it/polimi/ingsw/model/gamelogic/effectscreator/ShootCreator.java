package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.gamelogic.turn.MessageRetriever;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;

/**
 * Sets and creates the shooting effect.
 */
public class ShootCreator extends ShootCreatorAbstract implements EffectsCreator{

    /**
     * Represents the target of the shoot.
     */
    private Player target;

    /**
     * Represents if the shoot must arrive in a specific square.
     */
    private Boolean squareAssigned;

    /**
     * Represents the square where the shoot must arrive.
     */
    private Square squareTarget;

    /**
     * This constructor is used when there is no square assigned for the shooting and there are no max and min distance.
     */
    public ShootCreator(Player from, Player shooter, Boolean visibility, Integer damages, Integer marks, Boolean optional) {
        this.from = from;
        this.player = shooter;
        this.visibility = visibility;
        this.damages = damages;
        this.marks = marks;
        this.minDist = 0;
        this.maxDist = -1;
        this.optional = optional;
        this.target = null;
        this.squareAssigned = false;
        this.squareTarget = null;
    }

    /**
     * This constructor is used when there is no square assigned for the shooting but there are no max and min distance.
     */
    public ShootCreator(Player from, Player shooter, Boolean visibility, Integer damages, Integer marks, Integer minDist, Integer maxDist, Boolean optional) {
        this.from = from;
        this.player = shooter;
        this.visibility = visibility;
        this.damages = damages;
        this.marks = marks;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.optional = optional;
        this.target = null;
        this.squareAssigned = false;
        this.squareTarget = null;
    }

    /**
     * This constructor is used when there is a square assigned for the shooting.
     */
    public ShootCreator(Player shooter, Integer damages, Integer marks, Boolean squareAssigned, Square squareTarget) {
        this.from = null;
        this.player = shooter;
        this.visibility = null;
        this.damages = damages;
        this.marks = marks;
        this.minDist = null;
        this.maxDist = null;
        this.optional = false;
        this.target = null;
        this.squareAssigned = squareAssigned;
        this.squareTarget = squareTarget;
    }

    /**
     * Default constructor. Sets all the attributes except the target.
     */
    public ShootCreator(Player from, Player shooter, Boolean visibility, Integer damages,
                        Integer marks, Integer minDist, Integer maxDist, Boolean optional,
                        Boolean squareAssigned, Square squareTarget){
        this.from = from;
        this.player = shooter;
        this.visibility = visibility;
        this.damages = damages;
        this.marks = marks;
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.optional = optional;
        this.target = null;
        this.squareAssigned = squareAssigned;
        this.squareTarget = squareTarget;
    }

    public ShootCreator() {
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getTarget() {
        return target;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Server server, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<Square> squaresTarget = new ArrayList<>();
        ArrayList<Player> playersAvailable = new ArrayList<>();
        ArrayList<FunctionalEffect> effects= new ArrayList<>();

        if(optional){
            if(!server.booleanQuestion(player, new MessageRetriever().retrieveMessage("WantToShoot"))){
                return effects;
            }
        }

        if(!squareAssigned){
            findSquares(squaresTarget, table);
        }else{
            squaresTarget.add(squareTarget);
        }

        squaresTarget.forEach(square ->
            playersAvailable.addAll(square.getPlayers()));
        playersAvailable.remove(player);

        if(playersAvailable.isEmpty()){
            if(optional){
                return new ArrayList<>();
            }else{
                throw new IllegalActionException();
            }
        }

        target = server.choosePlayer(player, playersAvailable);
        targets.getPlayersTargeted().add(target);
        if(damages>0){
            targets.getPlayersDamaged().add(target);
        }


        effects.add(new FunctionalFactory().createDamagePlayer(player, target, damages, marks));
        targets.getPlayersTargeted().add(target);
        if(damages>0){
            targets.getPlayersDamaged().add(target);
        }
        return effects;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public Boolean getSquareAssigned() {
        return squareAssigned;
    }

    public void setSquareAssigned(Boolean squareAssigned) {
        this.squareAssigned = squareAssigned;
    }

    public Square getSquareTarget() {
        return squareTarget;
    }

    public void setSquareTarget(Square squareTarget) {
        this.squareTarget = squareTarget;
    }
}
