package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.gamelogic.turn.MessageRetriever;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Sets and creates the chain of shooting effects, used by the T.H.O.R.
 * weapon.
 */
public class ShootChainCreator implements EffectsCreator{
    private static final Integer DAMAGES_FIRST_PLAYER = 2;
    private static final Integer DAMAGES_SECOND_PLAYER = 1;
    private static final Integer DAMAGES_THIRD_PLAYER = 2;

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

    public Player getPlayer() {
        return player;
    }

    public Integer getNumberOfReflections() {
        return numberOfReflections;
    }

    public void setNumberOfReflections(Integer numberOfReflections) {
        this.numberOfReflections = numberOfReflections;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Server server, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<FunctionalEffect> effects;
        ShootCreator shootCreator;
        ShootCreator shootCreator1;
        ShootCreator shootCreator2;


        shootCreator = new ShootCreator(player, player, true, DAMAGES_FIRST_PLAYER, 0, false);
        effects = new ArrayList<> (shootCreator.run(server, table, targets));

        if(numberOfReflections == 1) {
            Boolean playerOrSquare = true;
            if(table.getIsDomination()){
                playerOrSquare = server.booleanQuestion(player, new MessageRetriever().retrieveMessage("playerOrSquare"));
            }
            if(playerOrSquare) {
                shootCreator1 = new ShootCreator(shootCreator.getTarget(), player, true, DAMAGES_SECOND_PLAYER, 0, false);
                effects.addAll(shootCreator1.run(server, table, targets));
                if(shootCreator1.getTarget() != player) {
                    return effects;
                }else{
                    throw new IllegalActionException();
                }
            }else{
                ArrayList<Square> squaresTarget = table.getGameMap().getVisibility(shootCreator.getTarget().getPosition());
                squaresTarget= squaresTarget.stream().filter(square -> table.getGameMap().getSpawnSquares().contains(square) && !targets.getSquaresDamaged().contains(square)).
                        collect(Collectors.toCollection(ArrayList::new));
                if(squaresTarget.isEmpty()){
                    throw new IllegalActionException();
                }
                Square squareTarget = server.chooseSquare(player, squaresTarget);
                effects.add(new FunctionalFactory().createDamageSpawn(player, (DominationSpawnSquare) squareTarget));
                targets.getSquaresDamaged().add((DominationSpawnSquare) squareTarget);
                return effects;
            }
        }else {
            shootCreator1 = new ShootCreator(shootCreator.getTarget(), player, true, DAMAGES_SECOND_PLAYER, 0, false);
            effects.addAll(shootCreator1.run(server, table, targets));

            Boolean playerOrSquare = true;
            if (table.getIsDomination()) {
                playerOrSquare = server.booleanQuestion(player, new MessageRetriever().retrieveMessage("playerOrSquare"));
            }
            if (playerOrSquare) {
                shootCreator2 = new ShootCreator(shootCreator1.getTarget(), player, true, DAMAGES_THIRD_PLAYER, 0, false);
                effects.addAll(shootCreator2.run(server, table, targets));
                if (shootCreator2.getTarget() != player && shootCreator.getTarget() != shootCreator2.getTarget()) {
                    return effects;
                } else {
                    throw new IllegalActionException();
                }
            } else {
                ArrayList<Square> squaresTarget = table.getGameMap().getVisibility(shootCreator1.getTarget().getPosition());
                squaresTarget = squaresTarget.stream().filter(square -> table.getGameMap().getSpawnSquares().contains(square) && !targets.getSquaresDamaged().contains(square)).
                        collect(Collectors.toCollection(ArrayList::new));
                if (squaresTarget.isEmpty()) {
                    throw new IllegalActionException();
                }
                Square squareTarget = server.chooseSquare(player, squaresTarget);
                effects.add(new FunctionalFactory().createDamageSpawn(player, (DominationSpawnSquare) squareTarget));
                targets.getSquaresDamaged().add((DominationSpawnSquare) squareTarget);
                return effects;
            }
        }
    }
}
