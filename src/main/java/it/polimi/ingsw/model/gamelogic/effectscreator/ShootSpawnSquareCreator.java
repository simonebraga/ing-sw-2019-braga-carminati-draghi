package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.gamelogic.turn.MessageRetriever;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ShootSpawnSquareCreator extends ShootCreatorAbstract implements EffectsCreator {

    /**
     * The square target of the shooting
     */
    private DominationSpawnSquare target;

    public ShootSpawnSquareCreator() {
    }

    public DominationSpawnSquare getTarget() {
        return target;
    }

    public ShootSpawnSquareCreator(Player from, Player shooter, Boolean visibility, Integer damages,
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


    @Override
    public ArrayList<FunctionalEffect> run(Server server, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<FunctionalEffect> effects = new ArrayList<>();
        ArrayList<Square> squaresTarget = new ArrayList<>();

        if(optional){
            if(!server.booleanQuestion(player, new MessageRetriever().retrieveMessage("WantToShoot"))){
                return effects;
            }
        }

        findSquares(squaresTarget, table);
        squaresTarget = squaresTarget.stream().filter(square -> table.getGameMap().getSpawnSquares().contains(square)).collect(Collectors.toCollection(ArrayList::new));
        squaresTarget.removeAll(targets.getSquaresDamaged());

        if(squaresTarget.isEmpty()){
            if(optional) {
                return new ArrayList<>();
            }else{
                throw new IllegalActionException();
            }
        }

        target = (DominationSpawnSquare) server.chooseSquare(player, squaresTarget);

        effects.add(new FunctionalFactory().createDamageSpawn(player, target));
        targets.getSquaresDamaged().add(target);

        return effects;
    }
}
