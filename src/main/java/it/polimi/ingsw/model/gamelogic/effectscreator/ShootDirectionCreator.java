package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.gamelogic.turn.MessageRetriever;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Creates and sets the effects that shoots in a single direction.
 */
public class ShootDirectionCreator implements EffectsCreator{

    private static final Integer RAILGUN_BASIC_DAMAGES = 3;
    private static final Integer RAILGUN_PIERCING_DAMAGES = 2;
    private static final Integer FLAME_THROWER_BARBECUE_FIRST_SQUARE_DAMAGES = 2;
    private static final Integer FLAME_THROWER_BARBECUE_SECOND_SQUARE_DAMAGES = 1;

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

    @Override
    public ArrayList<FunctionalEffect> run(Server server, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<Square> squaresTarget;
        ArrayList<Player> playersTarget = new ArrayList<>();
        ArrayList<FunctionalEffect> effects = new ArrayList<>();
        Character direction;

        if(throughWalls){ //RailGun
            ArrayList<Square> map = new ArrayList<>(table.getGameMap().getGridAsList());

            direction = server.chooseDirection(player);

            switch(direction){
                case 'N':
                    squaresTarget = map.stream().filter(square ->
                            table.getGameMap().getCoord(square).get(0).equals(table.getGameMap().getCoord(player.getPosition()).get(0)) &&
                                    table.getGameMap().getCoord(square).get(1) >= table.getGameMap().getCoord(player.getPosition()).get(1)).collect(Collectors.toCollection(ArrayList::new));
                    break;
                case 'S':
                    squaresTarget = map.stream().filter(square ->
                            table.getGameMap().getCoord(square).get(0).equals(table.getGameMap().getCoord(player.getPosition()).get(0)) &&
                                    table.getGameMap().getCoord(square).get(1) <= table.getGameMap().getCoord(player.getPosition()).get(1)).collect(Collectors.toCollection(ArrayList::new));
                    break;
                case 'W':
                    squaresTarget = map.stream().filter(square ->
                            table.getGameMap().getCoord(square).get(0) <= table.getGameMap().getCoord(player.getPosition()).get(0) &&
                                    table.getGameMap().getCoord(square).get(1).equals(table.getGameMap().getCoord(player.getPosition()).get(1))).collect(Collectors.toCollection(ArrayList::new));
                    break;
                default:
                    squaresTarget = map.stream().filter(square ->
                            table.getGameMap().getCoord(square).get(0) >= table.getGameMap().getCoord(player.getPosition()).get(0) &&
                                    table.getGameMap().getCoord(square).get(1).equals(table.getGameMap().getCoord(player.getPosition()).get(1))).collect(Collectors.toCollection(ArrayList::new));
                    break;
            }

            squaresTarget.forEach(square -> playersTarget.addAll(square.getPlayers()));
            playersTarget.remove(player);
            squaresTarget = squaresTarget.stream().filter(square -> table.getGameMap().getSpawnSquares().contains(square) &&
                    !targets.getSquaresDamaged().contains(square)).collect(Collectors.toCollection(ArrayList::new));

            if(playersTarget.isEmpty() && !(!table.getIsDomination() || squaresTarget.isEmpty())){
                throw new IllegalActionException();
            }

            Boolean playerOrSquare = true;
            if(table.getIsDomination()){
                playerOrSquare = server.booleanQuestion(player, new MessageRetriever().retrieveMessage("playerOrSquare"));
            }

            if(playerOrSquare) {
                if(playersTarget.isEmpty()){
                    throw new IllegalActionException();
                }

                Player target1 = server.choosePlayer(player, playersTarget);
                if(twoTargets){
                    effects.add(new FunctionalFactory().createDamagePlayer(player, target1, RAILGUN_PIERCING_DAMAGES, 0));
                }else{
                    effects.add(new FunctionalFactory().createDamagePlayer(player, target1, RAILGUN_BASIC_DAMAGES, 0));
                }
                targets.getPlayersTargeted().add(target1);
                targets.getPlayersDamaged().add(target1);

                playersTarget.remove(target1);
            }else{
                if(squaresTarget.isEmpty()){
                    throw new IllegalActionException();
                }

                DominationSpawnSquare target1 = (DominationSpawnSquare) server.chooseSquare(player, squaresTarget);
                effects.add(new FunctionalFactory().createDamageSpawn(player, target1));
                targets.getSquaresDamaged().add(target1);
                squaresTarget.remove(target1);
            }

            if(twoTargets){
                if(!server.booleanQuestion(player, new MessageRetriever().retrieveMessage("wantToShoot"))){
                    return effects;
                }

                if(playersTarget.isEmpty() && !(!table.getIsDomination() || squaresTarget.isEmpty())){
                    throw new IllegalActionException();
                }

                effects.addAll(directShoot(server, table, playersTarget, squaresTarget, targets));
            }
        }else{ //FlameThrower
            boolean noTargets = false;

            direction = server.chooseDirection(player);

            squaresTarget = new SquaresVisibleInADirection(direction, 1, player).run(table);

            squaresTarget.forEach(square -> playersTarget.addAll(square.getPlayers()));
            squaresTarget = squaresTarget.stream().filter(square -> table.getGameMap().getSpawnSquares().contains(square) &&
                    !targets.getSquaresDamaged().contains(square)).collect(Collectors.toCollection(ArrayList::new));
            if(playersTarget.isEmpty() && !(!table.getIsDomination() || squaresTarget.isEmpty())){
                noTargets = true;
            }else{
                if(directShoot){
                    effects.addAll(directShoot(server, table, playersTarget, squaresTarget, targets));
                }else{
                    playersTarget.forEach(playerTarget -> {
                        effects.add(new FunctionalFactory().createDamagePlayer(this.player, playerTarget, FLAME_THROWER_BARBECUE_FIRST_SQUARE_DAMAGES, 0));
                        if(!targets.getPlayersTargeted().contains(playerTarget)){
                            targets.getPlayersTargeted().add(playerTarget);
                        }
                        if(!targets.getPlayersDamaged().contains(playerTarget)){
                            targets.getPlayersDamaged().add(playerTarget);
                        }
                    });
                    if(table.getIsDomination()) {
                        squaresTarget.forEach(square -> {
                            effects.add(new FunctionalFactory().createDamageSpawn(player, (DominationSpawnSquare) square));
                            targets.getSquaresDamaged().add((DominationSpawnSquare) square);
                        });
                    }
                }
            }

            if(!noTargets && directShoot){
                if(!server.booleanQuestion(player, new MessageRetriever().retrieveMessage("wantToShoot"))){
                    return effects;
                }
            }

            squaresTarget = new SquaresVisibleInADirection(direction, 2, player).run(table);

            playersTarget.clear();
            squaresTarget.forEach(square -> playersTarget.addAll(square.getPlayers()));
            squaresTarget = squaresTarget.stream().filter(square -> table.getGameMap().getSpawnSquares().contains(square) &&
                    !targets.getSquaresDamaged().contains(square)).collect(Collectors.toCollection(ArrayList::new));
            if(playersTarget.isEmpty() && !(!table.getIsDomination() || squaresTarget.isEmpty())){
                if (noTargets) {
                    throw new IllegalActionException();
                }
            }else{
                if(directShoot){
                    effects.addAll(directShoot(server, table, playersTarget, squaresTarget, targets));
                }else{
                    playersTarget.forEach(playerTarget -> {
                        effects.add(new FunctionalFactory().createDamagePlayer(this.player, playerTarget, FLAME_THROWER_BARBECUE_SECOND_SQUARE_DAMAGES, 0));
                        if(!targets.getPlayersTargeted().contains(playerTarget)){
                            targets.getPlayersTargeted().add(playerTarget);
                        }
                        if(!targets.getPlayersDamaged().contains(playerTarget)){
                            targets.getPlayersDamaged().add(playerTarget);
                        }
                    });
                    if(table.getIsDomination()) {
                        squaresTarget.forEach(square -> {
                            effects.add(new FunctionalFactory().createDamageSpawn(player, (DominationSpawnSquare) square));
                            targets.getSquaresDamaged().add((DominationSpawnSquare) square);
                        });
                    }
                }
            }
        }
        return effects;
    }

    private ArrayList<FunctionalEffect> directShoot(Server server, GameTable table, ArrayList<Player> playersTarget, ArrayList<Square> squaresTarget, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<FunctionalEffect> effects = new ArrayList<>();
        Boolean playerOrSquare = true;
        if(table.getIsDomination()){
            playerOrSquare = server.booleanQuestion(player, new MessageRetriever().retrieveMessage("playerOrSquare"));
        }
        if(playerOrSquare) {
            if (playersTarget.isEmpty()) {
                throw new IllegalActionException();
            }
            Player target = server.choosePlayer(player, playersTarget);
            effects.add(new FunctionalFactory().createDamagePlayer(player, target, 1, 0));
            targets.getPlayersTargeted().add(target);
            targets.getPlayersDamaged().add(target);
            playersTarget.remove(target);
        }else {
            if (squaresTarget.isEmpty()) {
                throw new IllegalActionException();
            }

            DominationSpawnSquare target2 = (DominationSpawnSquare) server.chooseSquare(player, squaresTarget);
            effects.add(new FunctionalFactory().createDamageSpawn(player, target2));
            targets.getSquaresDamaged().add(target2);
            squaresTarget.remove(target2);
        }
        return effects;
    }
}