package it.polimi.ingsw.model.gamelogic.turn;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.exceptionclasses.FrenzyModeException;
import it.polimi.ingsw.model.gamelogic.actions.SpawnAction;
import it.polimi.ingsw.model.playerclasses.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * At the end of the turn, controls if some players are dead.
 * For the dead players, it makes them Spawn and gives the point to the other players.
 */
public class DeathsFinder {

    public void runDeathsFinder(Controller controller, GameTable table, Player playerOfTurn) throws FrenzyModeException {
        int kills = 0;

        for (Player player : table.getPlayers()) {
            if(player.getDamageTrack().getDamage().size()>10){
                givePoints(table, player);
                player.getDamageTrack().resetDamage();
                new SpawnAction(player).run(controller, table);
                kills++;
                if(kills==2){
                    table.getDoubleKillCounter().add(playerOfTurn);
                }
            }
        }
    }

    private void givePoints(GameTable table, Player deadPlayer) throws FrenzyModeException {
        ArrayList<Player> damageTrack = deadPlayer.getDamageTrack().getDamage();
        damageTrack.get(0).addPoints(1); //First blood
        table.getKillshotTrack().kill(damageTrack.get(11));
        if(damageTrack.size()==12) {
            table.getKillshotTrack().overKill(damageTrack.get(12));
            new FunctionalFactory().createDamagePlayer(deadPlayer, damageTrack.get(12), 0, 1).doAction();
        }

        //Divide point to each player
        HashMap<Player, Integer> map = new HashMap<>();
        table.getPlayers().stream().filter(player -> player!=deadPlayer && damageTrack.contains(player)).forEach(
                player -> map.put(player, (int)damageTrack.stream().filter(player1 -> player1==player).count())
        );
        int pointTrackCounter = 0;
        boolean stop = false;
        while(!map.isEmpty() && !stop) {
            Integer maxDamages = 0;
            ArrayList<Player> playersMaxDamages = new ArrayList<>();
            for(Player player: map.keySet()){
                if(maxDamages == map.get(player)){
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
            }
            pointTrackCounter++;
            if(pointTrackCounter==deadPlayer.getPointTrack().getValue().size()){
                stop = true;
            }
        }

        deadPlayer.getPointTrack().reduceValue();
    }
}