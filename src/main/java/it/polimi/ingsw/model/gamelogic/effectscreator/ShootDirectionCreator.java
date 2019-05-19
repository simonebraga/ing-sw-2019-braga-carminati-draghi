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
import java.util.stream.Collectors;

/**
 * Creates and sets the effects that shoots in a single direction.
 */
public class ShootDirectionCreator implements EffectsCreator{
    /**
     * The player that shoots.
     */
    private Player player;

    /**
     * Represents if the shoot can pass through walls.
     */
    private Boolean throughWalls;

    /**
     * Represents if the shoot targets a player or if it's an AOE shoot.
     */
    private Boolean directShoot;

    /**
     * Represents if the target of the shooting is one player
     * or two players.
     */
    private Boolean twoTargets;

    /**
     * Default constructor. Sets all the attributes except the direction.
     */
    public ShootDirectionCreator(Player shooter, Boolean throughWalls, Boolean directShoot, Boolean twoTargets) {
        this.player = shooter;
        this.throughWalls = throughWalls;
        this.directShoot = directShoot;
        this.twoTargets = twoTargets;
    }

    public ShootDirectionCreator() {
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Controller controller, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<Square> squaresTarget;
        ArrayList<Player> playersTarget = new ArrayList<>();
        ArrayList<FunctionalEffect> effects = new ArrayList<>();
        Character direction;

        if(throughWalls){
            ArrayList<Square> map = new ArrayList<>(table.getGameMap().getSpawnSquares());
            map.addAll(table.getGameMap().getTileSquares());

            direction = controller.chooseDirection(player);

            switch(direction){
                case 'N':
                    squaresTarget = new ArrayList<>(map.stream().filter(square ->
                            table.getGameMap().getCoord(square).get(0).equals(table.getGameMap().getCoord(player.getPosition()).get(0)) &&
                                    table.getGameMap().getCoord(square).get(1) > table.getGameMap().getCoord(player.getPosition()).get(1)).collect(Collectors.toList()));
                    break;
                case 'S':
                    squaresTarget = new ArrayList<>(map.stream().filter(square ->
                            table.getGameMap().getCoord(square).get(0).equals(table.getGameMap().getCoord(player.getPosition()).get(0)) &&
                                    table.getGameMap().getCoord(square).get(1) < table.getGameMap().getCoord(player.getPosition()).get(1)).collect(Collectors.toList()));
                    break;
                case 'W':
                    squaresTarget = new ArrayList<>(map.stream().filter(square ->
                            table.getGameMap().getCoord(square).get(0) < table.getGameMap().getCoord(player.getPosition()).get(0) &&
                                    table.getGameMap().getCoord(square).get(1).equals(table.getGameMap().getCoord(player.getPosition()).get(1))).collect(Collectors.toList()));
                    break;
                default:
                    squaresTarget = new ArrayList<>(map.stream().filter(square ->
                            table.getGameMap().getCoord(square).get(0) > table.getGameMap().getCoord(player.getPosition()).get(0) &&
                                    table.getGameMap().getCoord(square).get(1).equals(table.getGameMap().getCoord(player.getPosition()).get(1))).collect(Collectors.toList()));
                    break;
            }

            squaresTarget.forEach(square -> playersTarget.addAll(square.getPlayers()));

            if(playersTarget.isEmpty()){
                throw new IllegalActionException();
            }

            Player target1;

            target1 = controller.choosePlayer(player, playersTarget);
            targets.getPlayersTargeted().add(target1);
            targets.getPlayersDamaged().add(target1);

            playersTarget.remove(target1);

            if(twoTargets){
                effects.add(new FunctionalFactory().createDamagePlayer(player, target1, 2, 0));
                if(!playersTarget.isEmpty()){
                    Player target2 = controller.choosePlayer(player, playersTarget);
                    effects.add(new FunctionalFactory().createDamagePlayer(player, target2, 2, 0));
                    targets.getPlayersTargeted().add(target2);
                    targets.getPlayersDamaged().add(target2);
                }
            }else{
                effects.add(new FunctionalFactory().createDamagePlayer(player, target1, 3, 0));
            }
        }else{ //FlameThrower
            boolean noPlayer = false;

            direction = controller.chooseDirection(player);

            squaresTarget = new SquaresVisibleInADirection(direction, 1, player).run(table);

            squaresTarget.forEach(square -> playersTarget.addAll(square.getPlayers()));
            if(playersTarget.isEmpty()){
                noPlayer = true;
            }else{
                if(directShoot){
                    Player target = controller.choosePlayer(player, playersTarget);
                    effects.add(new FunctionalFactory().createDamagePlayer(player, target, 1, 0));
                    targets.getPlayersTargeted().add(target);
                    targets.getPlayersDamaged().add(target);
                }else{
                    playersTarget.forEach(playerTarget -> {
                        new FunctionalFactory().createDamagePlayer(this.player, playerTarget, 2, 0);
                        if(!targets.getPlayersTargeted().contains(playerTarget)){
                            targets.getPlayersTargeted().add(playerTarget);
                        }
                        if(!targets.getPlayersDamaged().contains(playerTarget)){
                            targets.getPlayersDamaged().add(playerTarget);
                        }
                    });
                }
            }

            squaresTarget = new SquaresVisibleInADirection(direction, 2, player).run(table);

            playersTarget.clear();
            squaresTarget.forEach(square -> playersTarget.addAll(square.getPlayers()));
            if(playersTarget.isEmpty()) {
                if (noPlayer) {
                    throw new IllegalActionException();
                }
            }else{
                if(directShoot){
                    Player target = controller.choosePlayer(player, playersTarget);
                    effects.add(new FunctionalFactory().createDamagePlayer(player, target, 1, 0));
                    targets.getPlayersTargeted().add(target);
                    targets.getPlayersDamaged().add(target);
                }else{
                    playersTarget.forEach(playerTarget -> {
                        new FunctionalFactory().createDamagePlayer(player, playerTarget, 2, 0);
                        if(!targets.getPlayersTargeted().contains(playerTarget)){
                            targets.getPlayersTargeted().add(playerTarget);
                        }
                        if(!targets.getPlayersDamaged().contains(playerTarget)){
                            targets.getPlayersDamaged().add(playerTarget);
                        }
                    });
                }
            }
        }
        return effects;
    }

    public Player getPlayer() {
        return player;
    }

    public Boolean getThroughWalls() {
        return throughWalls;
    }

    public void setThroughWalls(Boolean throughWalls) {
        this.throughWalls = throughWalls;
    }

    public Boolean getDirectShoot() {
        return directShoot;
    }

    public void setDirectShoot(Boolean directShoot) {
        this.directShoot = directShoot;
    }

    public Boolean getTwoTargets() {
        return twoTargets;
    }

    public void setTwoTargets(Boolean twoTargets) {
        this.twoTargets = twoTargets;
    }
}