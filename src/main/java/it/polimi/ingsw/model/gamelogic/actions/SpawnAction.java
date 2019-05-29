package it.polimi.ingsw.model.gamelogic.actions;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.Random;
import java.util.stream.Collectors;

/**
 * The class that builds the action of spawning.
 */
public class SpawnAction {

    /**
     * The player that must spawn
     */
    private Player player;

    public SpawnAction(Player player) {
        this.player = player;
    }

    public void run(Server server, GameTable table) {
        FunctionalEffect draw = () -> player.getPowerupPocket().addPowerup(table.getPowerupDeck().draw());
        draw.doAction();
        Powerup powerup;

        try {
            powerup = server.choosePowerup(player, player.getPowerupPocket().getPowerups());
        } catch (UnavailableUserException e) {
            Random random = new Random();
            int n = random.nextInt(player.getPowerupPocket().getPowerups().size());
            powerup = player.getPowerupPocket().getPowerups().get(n);
        }
        Powerup finalPowerup = powerup;

        FunctionalEffect discard = () ->
                table.getPowerupDeck().discard(
                        player.getPowerupPocket().removePowerup(
                                player.getPowerupPocket().getPowerups().indexOf(finalPowerup)));
        discard.doAction();

        if(player.getPosition()!=null){ //First spawn
            new FunctionalFactory().createMove(player, table.getGameMap().getSpawnSquares().stream().
                    filter(square -> square.getColor()== finalPowerup.getColor()).
                    collect(Collectors.toList()).get(0)).doAction();
        }else{
            FunctionalEffect spawn = () ->
                    player.move(table.getGameMap().getSpawnSquares().stream().
                            filter(square -> square.getColor()==finalPowerup.getColor()).
                            collect(Collectors.toList()).get(0));
            spawn.doAction();
        }
    }
}
