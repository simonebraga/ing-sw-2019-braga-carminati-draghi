package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.gamelogic.turn.MessageRetriever;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;

/**
 * Creates and sets the effect that shoots a vortex.
 */
public class ShootVortexCreator implements EffectsCreator{
    private static final Integer DAMAGES_BASIC = 2;
    private static final Integer DAMAGES_BLACK_HOLE = 1;

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

    public Player getPlayer() {
        return player;
    }

    public Boolean getMultipleTargets() {
        return multipleTargets;
    }

    public void setMultipleTargets(Boolean multipleTargets) {
        this.multipleTargets = multipleTargets;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Server server, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<FunctionalEffect> effects = new ArrayList<>();
        ArrayList<Player> playersAvailable = new ArrayList<>();
        Player target;
        ArrayList<Square> squares= new ArrayList<>(table.getGameMap().getVisibility(player.getPosition()));
        squares.remove(player.getPosition());

        Square vortex = server.chooseSquare(player, squares);

        Boolean canShootVortex = table.getIsDomination() &&
                table.getGameMap().getSpawnSquares().contains(vortex) &&
                !targets.getSquaresDamaged().contains(vortex);

        squares = new ArrayList<>(table.getGameMap().getRange(vortex, 1));
        squares.forEach(square -> playersAvailable.addAll(square.getPlayers()));
        playersAvailable.remove(player);

        if(playersAvailable.isEmpty() && !canShootVortex){
            throw new IllegalActionException();
        }

        canShootVortex = shootSomething(server, canShootVortex, playersAvailable, vortex, targets, DAMAGES_BASIC, effects);

        if (multipleTargets){
            if(playersAvailable.isEmpty() && !canShootVortex){
                throw new IllegalActionException();
            }
            canShootVortex = shootSomething(server, canShootVortex, playersAvailable, vortex, targets, DAMAGES_BLACK_HOLE, effects);

            if(!server.booleanQuestion(player, new MessageRetriever().retrieveMessage("wantToShoot"))){
                return effects;
            }

            if(!playersAvailable.isEmpty() || canShootVortex){
                shootSomething(server, canShootVortex, playersAvailable, vortex, targets, DAMAGES_BLACK_HOLE, effects);
            }
        }
        return effects;
    }

    private Boolean shootSomething(Server server, Boolean canShootVortex, ArrayList<Player> playersAvailable, Square vortex, Targets targets, Integer damages, ArrayList<FunctionalEffect> effects) throws UnavailableUserException, IllegalActionException {
        Boolean playerOrSquare = true;
        if(canShootVortex){
            playerOrSquare = server.booleanQuestion(player, new MessageRetriever().retrieveMessage("playerOrSquare"));
        }
        if(playerOrSquare) {
            if (playersAvailable.isEmpty()){
                throw new IllegalActionException();
            }
            Player target = server.choosePlayer(player, playersAvailable);

            effects.add(new FunctionalFactory().createDamagePlayer(player, target, damages, 0));
            targets.getPlayersTargeted().add(target);
            targets.getPlayersDamaged().add(target);
            effects.add(new FunctionalFactory().createMove(target, vortex));

            playersAvailable.remove(target);
        }else{
            effects.add(new FunctionalFactory().createDamageSpawn(player, (DominationSpawnSquare) vortex));
            canShootVortex = false;
            targets.getSquaresDamaged().add((DominationSpawnSquare) vortex);
        }
        return canShootVortex;
    }
}
