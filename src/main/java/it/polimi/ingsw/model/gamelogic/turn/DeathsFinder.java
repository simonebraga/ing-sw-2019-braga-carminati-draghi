package it.polimi.ingsw.model.gamelogic.turn;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.exceptionclasses.FrenzyModeException;
import it.polimi.ingsw.model.gamelogic.actions.SpawnAction;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * At the end of the turn, controls if some players are dead.
 * For the dead players, it makes them Spawn and gives the point to the other players.
 */
public class DeathsFinder {

    private static final Integer DAMAGES_TO_BE_KILLED = 11;
    private static final Integer DAMAGES_TO_BE_OVERKILLED = 12;
    private static final Integer KILLER_INDEX = 10;
    private static final Integer OVERKILL_INDEX = 11;

    public void runDeathsFinder(Server server, GameTable table, Player playerOfTurn) throws FrenzyModeException {
        int kills = 0;

        for (Player player : table.getPlayers()) {
            if(player.getDamageTrack().getDamage().size()>=DAMAGES_TO_BE_KILLED){
                givePoints(server, table, player);
                player.getDamageTrack().resetDamage();
                new SpawnAction(player).run(server, table);
                if(player != playerOfTurn) {
                    kills++;
                }
                if(kills==2){
                    table.getDoubleKillCounter().add(playerOfTurn);
                }
            }
        }
    }

    private void givePoints(Server server, GameTable table, Player deadPlayer) throws FrenzyModeException {
        ArrayList<Player> damageTrack = deadPlayer.getDamageTrack().getDamage();
        if(deadPlayer.getHasBoardFlipped()) {
            damageTrack.get(0).addPoints(1); //First blood
        }
        table.getKillshotTrack().kill(damageTrack.get(KILLER_INDEX)); //Kill

        //Overkill (normal and domination)
        if(damageTrack.size()==DAMAGES_TO_BE_OVERKILLED) {
            if(table.getIsDomination()) {
                Square square;
                try {
                    square = server.chooseSquare(damageTrack.get(OVERKILL_INDEX), table.getGameMap().getGridAsList().stream().filter(square1 -> table.getGameMap().getSpawnSquares().contains(square1)).collect(Collectors.toCollection(ArrayList::new)));
                } catch (UnavailableUserException e) {
                    Random random = new Random();
                    int n = random.nextInt(table.getGameMap().getSpawnSquares().size());
                    square = table.getGameMap().getGridAsList().stream().filter(square1 -> table.getGameMap().getSpawnSquares().contains(square1)).collect(Collectors.toCollection(ArrayList::new)).get(n);
                }
                new FunctionalFactory().createDamageSpawn(damageTrack.get(OVERKILL_INDEX), (DominationSpawnSquare) square);
            }else{
                table.getKillshotTrack().overKill(damageTrack.get(OVERKILL_INDEX));
            }
            new FunctionalFactory().createDamagePlayer(deadPlayer, damageTrack.get(OVERKILL_INDEX), 0, 1).doAction();
        }

        //Divide point to each player
        HashMap<Player, Integer> map = new HashMap<>();
        table.getPlayers().stream().filter(player -> player!=deadPlayer && damageTrack.contains(player)).forEach(
                player -> map.put(player, (int)damageTrack.stream().filter(player1 -> player1==player).count())
        );
        map.remove(deadPlayer); //Removes the damages done by the player to himself (domination)

        int pointTrackCounter = 0;
        boolean stop = false;

        if(pointTrackCounter==deadPlayer.getPointTrack().getValue().size()){ //It is possible that a player has an empty point track.
            stop = true;
            for (Player player : map.keySet()) { //Gives one point to the remaining players that have done damage.
                player.addPoints(1);
            }
        }

        while(!map.isEmpty() && !stop) {
            Integer maxDamages = 0;
            ArrayList<Player> playersMaxDamages = new ArrayList<>();
            for(Player player: map.keySet()){ //find the remaining players that have done the most damages
                if(maxDamages.equals(map.get(player))){
                    playersMaxDamages.add(player);
                }else if(maxDamages < map.get(player)){
                    playersMaxDamages = new ArrayList<>();
                    playersMaxDamages.add(player);
                    maxDamages = map.get(player);
                }
            }
            boolean found = false;
            if(playersMaxDamages.size()>1){
                for(int i=0; found;i++) {
                    if (playersMaxDamages.contains(damageTrack.get(i))) {
                        damageTrack.get(i).addPoints(deadPlayer.getPointTrack().getValue().get(pointTrackCounter));
                        map.remove(damageTrack.get(i));
                        found = true;
                    }
                }
            }else{
                playersMaxDamages.get(0).addPoints(deadPlayer.getPointTrack().getValue().get(pointTrackCounter));
                map.remove(playersMaxDamages.get(0));
            }

            pointTrackCounter++;

            if(pointTrackCounter==deadPlayer.getPointTrack().getValue().size()){
                stop = true;
                for (Player player : map.keySet()) { //Gives one point to the remaining players that have done damage.
                    player.addPoints(1);
                }
            }
        }

        deadPlayer.getPointTrack().reduceValue();
    }
}