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
import java.util.stream.Collectors;

/**
 * Sets and creates the effect that moves and shoots a players.
 */
public class MoveAndShootCreator implements EffectsCreator{

    /**
     * The player that shoots.
     */
    private Player player;

    /**
     * The damages done by the shoot.
     */
    private Integer damages;

    /**
     * The marks done by the shoot.
     */
    private Integer marks;

    /**
     * Represents the minimum distance of the target from the player.
     * <p>If a minimum distance is not required, this attribute must be put at 0.</p>
     * <p>Example: if a Weapon can shoot only in a Square different from the Square where the player is,
     * then minDist = 1.</p>
     */
    private Integer minDistShoot;

    /**
     * Represents the maximum distance of the target from the player.
     * <p>If a maximum distance is not required, this attribute must be put at -1.</p>
     */
    private Integer maxDistShoot;

    /**
     * The maximum number of moves for the movement effect.
     */
    private Integer maxMoves;

    /**
     * Represents if the move must be done before or after the shoot.
     */
    private Boolean moveBeforeShoot;

    /**
     * Represents if the player that has to be moved is the target or the player.
     * <p>The value true represents that the target must be moved, the value false
     * represents that the player must be moved.</p>
     */
    private Boolean moveTargetOrShooter;

    public MoveAndShootCreator() {
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Default constructor. Sets all the attributes.
     */
    public MoveAndShootCreator(Player shooter, Integer damages, Integer marks, Integer minDistShoot,
                               Integer maxDistShoot, Integer maxMoves, Boolean moveBeforeShoot, Boolean moveTargetOrShooter) {
        this.player = shooter;
        this.damages = damages;
        this.marks = marks;
        this.minDistShoot = minDistShoot;
        this.maxDistShoot = maxDistShoot;
        this.maxMoves = maxMoves;
        this.moveBeforeShoot = moveBeforeShoot;
        this.moveTargetOrShooter = moveTargetOrShooter;
    }

    public Player getPlayer() {
        return player;
    }

    public Integer getDamages() {
        return damages;
    }

    public void setDamages(Integer damages) {
        this.damages = damages;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public Integer getMinDistShoot() {
        return minDistShoot;
    }

    public void setMinDistShoot(Integer minDistShoot) {
        this.minDistShoot = minDistShoot;
    }

    public Integer getMaxDistShoot() {
        return maxDistShoot;
    }

    public void setMaxDistShoot(Integer maxDistShoot) {
        this.maxDistShoot = maxDistShoot;
    }

    public Integer getMaxMoves() {
        return maxMoves;
    }

    public void setMaxMoves(Integer maxMoves) {
        this.maxMoves = maxMoves;
    }

    public Boolean getMoveBeforeShoot() {
        return moveBeforeShoot;
    }

    public void setMoveBeforeShoot(Boolean moveBeforeShoot) {
        this.moveBeforeShoot = moveBeforeShoot;
    }

    public Boolean getMoveTargetOrShooter() {
        return moveTargetOrShooter;
    }

    public void setMoveTargetOrShooter(Boolean moveTargetOrShooter) {
        this.moveTargetOrShooter = moveTargetOrShooter;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Server server, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<Player> playersTarget;
        Player playerTarget;
        ArrayList<FunctionalEffect> effects = new ArrayList<>();

        if (moveBeforeShoot){
            if(moveTargetOrShooter){ //Tractor Beam
                playersTarget = table.getPlayers().stream().filter(player1 -> player1.getPosition() != null).collect(Collectors.toCollection(ArrayList::new));
                playersTarget.remove(player);

                if(playersTarget.isEmpty()){
                    throw new IllegalActionException();
                }

                playerTarget = server.choosePlayer(player, playersTarget);
                new MoveAnotherPlayerCreator(playerTarget, maxMoves, false, player).run(server, table, targets);
                if((maxDistShoot < 0 && table.getGameMap().getVisibility(player.getPosition()).contains(playerTarget.getPosition()))
                        || (maxDistShoot == 0 && player.getPosition().getPlayers().contains(playerTarget))){
                    effects.add(new FunctionalFactory().createDamagePlayer(player, playerTarget, damages, marks));
                    targets.getPlayersTargeted().add(playerTarget);
                    if(damages>0){
                        targets.getPlayersDamaged().addAll(playersTarget);
                    }
                }else{
                    throw new IllegalActionException();
                }
            }else{ //PowerGlove (Rocket Fist)
                Character direction;
                boolean noTargets = true;

                direction = server.chooseDirection(player);

                ArrayList<Square> squaresAssigned;
                squaresAssigned = new SquaresVisibleInADirection(direction, 1, player).run(table);
                if(squaresAssigned.isEmpty()){
                    throw new IllegalActionException();
                }
                boolean canShootSpawnSquare = table.getIsDomination() && table.getGameMap().getSpawnSquares().contains(squaresAssigned.get(0)) && targets.getSquaresDamaged().contains(squaresAssigned.get((0)));
                if(server.booleanQuestion(player, new MessageRetriever().retrieveMessage("wantToShoot"))) {
                    if (!squaresAssigned.get(0).getPlayers().isEmpty() || canShootSpawnSquare) {
                        Boolean playerOrSquare = true;
                        if (table.getIsDomination()) {
                            playerOrSquare = server.booleanQuestion(player, new MessageRetriever().retrieveMessage("playerOrSquare"));
                        }
                        if (playerOrSquare) {
                            if (!squaresAssigned.get(0).getPlayers().isEmpty()) {
                                effects.addAll(new ShootCreator(player, damages, marks, true, squaresAssigned.get(0)).run(server, table, targets));
                                noTargets = false;
                            }
                        } else {
                            if (canShootSpawnSquare) {
                                effects.add(new FunctionalFactory().createDamageSpawn(player, (DominationSpawnSquare) squaresAssigned.get(0)));
                                targets.getSquaresDamaged().add((DominationSpawnSquare) squaresAssigned.get(0));
                                noTargets = false;
                            }
                        }
                    }
                }

                if(!noTargets){
                    if(!server.booleanQuestion(player, new MessageRetriever().retrieveMessage("wantToMove"))){
                        return effects;
                    }
                }

                squaresAssigned = new SquaresVisibleInADirection(direction, 2, player).run(table);
                if(squaresAssigned.isEmpty()){
                    throw new IllegalActionException();
                }
                canShootSpawnSquare = table.getIsDomination() && table.getGameMap().getSpawnSquares().contains(squaresAssigned.get(0)) && targets.getSquaresDamaged().contains(squaresAssigned.get((0)));

                //Move the Player
                new FunctionalFactory().createMove(player, squaresAssigned.get(0)).doAction();

                if(!noTargets){
                    if(!server.booleanQuestion(player, new MessageRetriever().retrieveMessage("wantToShoot"))){
                        return effects;
                    }
                }

                Boolean playerOrSquare = true;
                if (table.getIsDomination()) {
                    playerOrSquare = server.booleanQuestion(player, new MessageRetriever().retrieveMessage("playerOrSquare"));
                }
                if (playerOrSquare) {
                    if (!squaresAssigned.get(0).getPlayers().isEmpty()) {
                        effects.addAll(new ShootCreator(player, damages, marks, true, squaresAssigned.get(0)).run(server, table, targets));
                    }else {
                        throw new IllegalActionException();
                    }
                } else {
                    if (canShootSpawnSquare) {
                        effects.add(new FunctionalFactory().createDamageSpawn(player, (DominationSpawnSquare) squaresAssigned.get(0)));
                        targets.getSquaresDamaged().add((DominationSpawnSquare) squaresAssigned.get(0));
                    }else{
                        throw new IllegalActionException();
                    }
                }
            }
        }else{ //Shotgun, Rocket and Grenade Launcher
            if(maxDistShoot == 0){ //Shotgun
                playersTarget = new ArrayList<> (player.getPosition().getPlayers());
                playersTarget.remove(player);
            }else{ //Rocket launcher and grenade launcher
                playersTarget = new ArrayList<>();
                table.getGameMap().getVisibility(player.getPosition()).forEach(square ->
                        playersTarget.addAll(square.getPlayers()));
                playersTarget.remove(player);
                if (minDistShoot == 1){ //Rocket Launcher
                    playersTarget.removeAll(player.getPosition().getPlayers());
                }
            }
            if(playersTarget.isEmpty()){
                throw new IllegalActionException();
            }
            playerTarget = server.choosePlayer(player, playersTarget);
            effects.add(new FunctionalFactory().createDamagePlayer(player, playerTarget, damages, marks));

            if(!targets.getPlayersTargeted().contains(playerTarget)) { //Needed if the player decides to shootAOE and then shoot to the player
                targets.getPlayersTargeted().add(playerTarget);
            }
            if (damages > 0 && !targets.getPlayersDamaged().contains(playerTarget)) {
                targets.getPlayersDamaged().add(playerTarget);
            }


            new MoveAnotherPlayerCreator(playerTarget, maxMoves, true, player).run(server, table, targets);
        }

        return effects;
    }
}
