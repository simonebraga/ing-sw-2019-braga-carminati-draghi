package it.polimi.ingsw.model.gamelogic.actions;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.enumeratedclasses.PowerupName;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.gamelogic.effectscreator.PayCreator;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class that builds the action of using a powerUp.
 */
public class PowerUpAction {

    /**
     * Creates and sets the use for one or more Newton.
     * @param controller Contains the data to connect to the different players.
     * @param table Contains all the data on the current match.
     * @param player The player that uses the powerUps.
     * @return The list of FunctionalEffects that represents the use of the powerUps.
     * @throws UnavailableUserException Gets thrown when a player does't answer before the time limit.
     */
    public List<FunctionalEffect> newtonUse(Controller controller, GameTable table, Player player) throws UnavailableUserException{
        ArrayList<FunctionalEffect> effects = new ArrayList<>();

        ArrayList<Powerup> powerUps = (ArrayList<Powerup>)
                player.getPowerupPocket().getPowerups().
                        stream().filter(powerUp -> powerUp.getName() == PowerupName.NEWTON).
                        collect(Collectors.toList());

        if(powerUps.isEmpty()){
            powerUps = controller.chooseMultiplePowerup(player, powerUps);
            for (Powerup powerUp : powerUps){
                effects.add(() ->
                        table.getPowerupDeck().discard(
                                player.getPowerupPocket().removePowerup(
                                        player.getPowerupPocket().getPowerups().indexOf(powerUp))));
                ArrayList<Player> players = new ArrayList<>(table.getPlayers());
                players.remove(player);
                Player target = controller.choosePlayer(player, players);
                Square square = controller.chooseSquare(player, table.getGameMap().getDistance(target.getPosition(), 2));
                effects.add(new FunctionalFactory().createMove(player, square));
            }
        }
        return effects;
    }

    /**
     * Creates and sets the use for one Teleporter.
     * @param controller Contains the data to connect to the different players.
     * @param table Contains all the data on the current match.
     * @param player The player that uses the powerUp.
     * @return The list of FunctionalEffects that represents the use of the powerUp.
     * @throws UnavailableUserException Gets thrown when a player does't answer before the time limit.
     */
    public List<FunctionalEffect> teleporterUse(Controller controller, GameTable table, Player player) throws UnavailableUserException {
        ArrayList<FunctionalEffect> effects = new ArrayList<>();

        ArrayList<Powerup> powerUps = (ArrayList<Powerup>)
                player.getPowerupPocket().getPowerups().
                        stream().filter(powerUp -> powerUp.getName() == PowerupName.TELEPORTER).
                        collect(Collectors.toList());

        if(!powerUps.isEmpty()){
            Powerup powerup = controller.choosePowerup(player, powerUps);
            effects.add(() ->
                    table.getPowerupDeck().discard(
                            player.getPowerupPocket().removePowerup(
                                    player.getPowerupPocket().getPowerups().indexOf(powerup))));
            ArrayList<Square> squares = new ArrayList<>(table.getGameMap().getSpawnSquares());
            squares.addAll(table.getGameMap().getTileSquares());
            Square square = controller.chooseSquare(player, squares);
            effects.add(new FunctionalFactory().createMove(player, square));
        }
        return effects;
    }

    /**
     * Creates and sets the use for one or more Targeting Scope.
     * @param controller Contains the data to connect to the different players.
     * @param table Contains all the data on the current match.
     * @param player The player that uses the powerUps.
     * @param target The player that is the target of the powerUps.
     * @return The list of FunctionalEffects that represents the use of the powerUps.
     * @throws IllegalActionException Gets thrown when a player wants to use the powerUps but can't pay for them.
     * @throws UnavailableUserException Gets thrown when a player does't answer before the time limit.
     */
    public List<FunctionalEffect> targetingScopeUse(Controller controller, GameTable table, Player player, Player target) throws IllegalActionException, UnavailableUserException {
        ArrayList<FunctionalEffect> effects = new ArrayList<>();

        ArrayList<Powerup> powerUps = (ArrayList<Powerup>)
                player.getPowerupPocket().getPowerups().
                        stream().filter(powerUp -> powerUp.getName() == PowerupName.TARGETINGSCOPE).
                        collect(Collectors.toList());
        if (!powerUps.isEmpty()) {
            powerUps = controller.chooseMultiplePowerup(player, powerUps);
            for (Powerup powerUp : powerUps) {
                effects.add(() ->
                        table.getPowerupDeck().discard(
                                player.getPowerupPocket().removePowerup(
                                        player.getPowerupPocket().getPowerups().indexOf(powerUp))));
            }
            effects.addAll(new PayCreator(player).payAnyColor(controller, table, powerUps, powerUps.size()));
            for(int i = 0; i<powerUps.size(); i++){
                effects.add(new FunctionalFactory().createDamagePlayer(player, target, 1, 0));
            }
        }
        return effects;
    }

    /**
     * Creates and sets the use for one or more TagBack Grenades.
     * @param controller Contains the data to connect to the different players.
     * @param table Contains all the data on the current match.
     * @param player The player that uses the powerUps.
     * @param target The player that is the target of the powerUps.
     * @return The list of FunctionalEffects that represents the use of the powerUps.
     * @throws UnavailableUserException Gets thrown when a player does't answer before the time limit.
     */
    public List<FunctionalEffect> tagBackGrenadeUse(Controller controller, GameTable table, Player player, Player target) throws UnavailableUserException {
        ArrayList<FunctionalEffect> effects = new ArrayList<>();
        ArrayList<Powerup> powerUps = (ArrayList<Powerup>)
                player.getPowerupPocket().getPowerups().
                        stream().filter(powerUp -> powerUp.getName() == PowerupName.TAGBACKGRENADE).
                        collect(Collectors.toList());
        if(!powerUps.isEmpty() || table.getGameMap().getVisibility(player.getPosition()).contains(target.getPosition())) {
            powerUps = controller.chooseMultiplePowerup(player, powerUps);
            for (Powerup powerUp : powerUps) {
                effects.add(() ->
                        table.getPowerupDeck().discard(
                                player.getPowerupPocket().removePowerup(
                                        player.getPowerupPocket().getPowerups().indexOf(powerUp))));
            }
            for(int i = 0; i<powerUps.size(); i++){
                effects.add(new FunctionalFactory().createDamagePlayer(player, target, 0, 1));
            }
        }
        return effects;
    }
}
