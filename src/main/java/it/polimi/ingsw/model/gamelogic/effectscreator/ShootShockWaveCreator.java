package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.gamelogic.turn.MessageRetriever;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Creates and sets the effect that shoots around the player to targets on different squares
 * or to all the targets.
 */
public class ShootShockWaveCreator implements EffectsCreator{

    /**
     * The player that shoots.
     */
    private Player player;

    /**
     * Represents if the shoot is is normal mode or in tsunami mode.
     */
    private Boolean tsunami;

    /**
     * Default constructor. Sets all the attributes.
     */
    public ShootShockWaveCreator(Player shooter, Boolean tsunami) {
        this.player = shooter;
        this.tsunami = tsunami;
    }

    public ShootShockWaveCreator() {
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Boolean getTsunami() {
        return tsunami;
    }

    public void setTsunami(Boolean tsunami) {
        this.tsunami = tsunami;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Controller controller, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<FunctionalEffect> effects =new ArrayList<>();
        ArrayList<Square> squaresTarget = new ArrayList<>(table.getGameMap().getDistance(player.getPosition(), 1));
        ArrayList<Player> playersTarget = new ArrayList<>();

        squaresTarget.forEach(square -> playersTarget.addAll(square.getPlayers()));
        squaresTarget = squaresTarget.stream().
                filter(square -> table.getGameMap().getSpawnSquares().contains(square) &&
                        !targets.getSquaresDamaged().contains(square))
                .collect(Collectors.toCollection(ArrayList::new));
        if(playersTarget.isEmpty() && !(table.getIsDomination() && !squaresTarget.isEmpty())){
            throw new IllegalActionException();
        }

        if(tsunami){
            playersTarget.forEach(playerTarget -> {
                effects.add(new FunctionalFactory().createDamagePlayer(player, playerTarget, 1, 0));
                if(!targets.getPlayersTargeted().contains(playerTarget)){
                    targets.getPlayersTargeted().add(playerTarget);
                }
                if(!targets.getPlayersDamaged().contains(playerTarget)){
                    targets.getPlayersDamaged().add(playerTarget);
                }
            });
            if(table.getIsDomination()) {
                squaresTarget.forEach(target -> {
                    if(table.getIsDomination() &&
                            !targets.getSquaresDamaged().contains(target)){
                        effects.add(new FunctionalFactory().createDamageSpawn(player, (DominationSpawnSquare) target));
                        targets.getSquaresDamaged().add((DominationSpawnSquare) target);
                    }
                });
            }
        }else{
            effects.addAll(shootSomething(controller, table, playersTarget, squaresTarget, targets));

            if(!playersTarget.isEmpty() || (table.getIsDomination() && !squaresTarget.isEmpty())){
                effects.addAll(shootSomething(controller, table, playersTarget, squaresTarget, targets));

                if(!playersTarget.isEmpty() || (table.getIsDomination() && !squaresTarget.isEmpty())) {
                    effects.addAll(shootSomething(controller, table, playersTarget, squaresTarget, targets));
                }
            }
        }
        return effects;
    }

    private ArrayList<FunctionalEffect> shootSomething(Controller controller, GameTable table, ArrayList<Player> playersTarget, ArrayList<Square> squaresTarget, Targets targets) throws  UnavailableUserException, IllegalActionException{
        Boolean playerOrSquare = true;
        ArrayList<FunctionalEffect> effects = new ArrayList<>();

        if(table.getIsDomination()){
            playerOrSquare = controller.booleanQuestion(player, new MessageRetriever().retrieveMessage("playerOrSquare"));
        }
        if(playerOrSquare) {
            if(playersTarget.isEmpty()) {
                throw new IllegalActionException();
            }
            Player target1 = controller.choosePlayer(player, playersTarget);
            effects.add(new FunctionalFactory().createDamagePlayer(player, target1, 1, 0));
            targets.getPlayersTargeted().add(target1);
            targets.getPlayersDamaged().add(target1);
            playersTarget.removeAll(target1.getPosition().getPlayers());
            squaresTarget.remove(target1.getPosition());
        }else{
            if(squaresTarget.isEmpty()){
                throw new IllegalActionException();
            }
            DominationSpawnSquare target1 = (DominationSpawnSquare) controller.chooseSquare(player, squaresTarget);
            if (targets.getSquaresDamaged().contains(target1)){
                throw new IllegalActionException();
            }
            effects.add(new FunctionalFactory().createDamageSpawn(player, target1));
            targets.getSquaresDamaged().add(target1);
            playersTarget.removeAll(target1.getPlayers());
            squaresTarget.remove(target1);
        }
        return effects;
    }
}
