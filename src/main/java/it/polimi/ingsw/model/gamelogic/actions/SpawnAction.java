package it.polimi.ingsw.model.gamelogic.actions;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.List;
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
        Powerup finalPowerup = powerup; //Assignment to have a final variable to use in lambda.

        FunctionalEffect discard = () ->
                table.getPowerupDeck().discard(
                        player.getPowerupPocket().removePowerup(
                                player.getPowerupPocket().getPowerups().indexOf(finalPowerup)));
        discard.doAction();

        ArrayList<Square> spawnSquares = table.getGameMap().getGridAsList().stream().filter(square -> table.getGameMap().getSpawnSquares().contains(square)).collect(Collectors.toCollection(ArrayList::new));
        SpawnSquare destination = new SpawnSquare();
        for (Square spawnSquare : spawnSquares) {
            SpawnSquare spawnSquare1 = (SpawnSquare) spawnSquare;
            if(spawnSquare1.getColor()==finalPowerup.getColor()){
                destination = spawnSquare1;
            }
        }
        if(player.getPosition()!=null){ //First spawn
            new FunctionalFactory().createMove(player, destination).doAction();
        }else{
            player.move(destination);
            destination.addPlayer(player);
        }
    }
}
