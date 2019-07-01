package it.polimi.ingsw.model.gamelogic.turn;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.exceptionclasses.FrenzyModeException;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.gamelogic.actions.ActionManager;
import it.polimi.ingsw.model.gamelogic.actions.PowerUpAction;
import it.polimi.ingsw.model.gamelogic.actions.ReloadAction;
import it.polimi.ingsw.model.gamelogic.effectscreator.Targets;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.mapclasses.TileSquare;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents the turn of a player.
 */
public class TurnManager {

    /**
     * The time to do a turn.
     */
    private static final Integer TIME_MILLISEC = 240000;

    /**
     * The player that does the turn.
     */
    private Player player;

    /**
     * Represents if the turn happens during the final frenzy.
     */
    private Boolean finalFrenzy;

    /**
     * Represents if the action happens before or after the final player.
     */
    private Boolean beforeFirstPlayer;

    public TurnManager(Player player, Boolean finalFrenzy, Boolean beforeFirstPlayer) {
        this.player = player;
        this.finalFrenzy = finalFrenzy;
        this.beforeFirstPlayer = beforeFirstPlayer;
    }

    public void runTurn(Server server, GameTable table) throws FrenzyModeException {
        final String errorProperty = "error";

        TimerTurn timerTurn = new TimerTurn(server, TIME_MILLISEC, player);
        timerTurn.start();

        doPowerUps(server, table);

        //Do the actions
        Targets targets = new Targets();
        doAction(server, table, targets);
        doPowerUps(server, table);
        if(!(finalFrenzy && !beforeFirstPlayer) && server.isConnected(player)){
            doAction(server,table, targets);
            doPowerUps(server, table);
        }

        //Reload
        boolean resultAction = true;
        ArrayList<FunctionalEffect> reload = new ArrayList<>();
        try {
            reload.addAll(new ReloadAction().run(server, table, player, new Targets()));
        } catch (IllegalActionException | UnavailableUserException e) {
            resultAction = false;
        }
        while (!resultAction && server.isConnected(player)){
            try {
                server.sendMessage(player, new MessageRetriever().retrieveMessage(errorProperty));
            } catch (UnavailableUserException e) {
            }
            try {
                reload.addAll(new ReloadAction().run(server, table, player, new Targets()));
                resultAction = true;
            } catch (IllegalActionException | UnavailableUserException e) {
                resultAction = false;
            }
        }
        reload.forEach(FunctionalEffect::doAction);

        timerTurn.setStop(true);

        //Damage player on SpawnSquare (domination)
        if(table.getIsDomination() && table.getGameMap().getSpawnSquares().contains(player.getPosition())) {
            new FunctionalFactory().createDamagePlayer(player, player, 1, 0).doAction();
            if (player.getPosition().getPlayers().size() == 1) {
                new FunctionalFactory().createDamageSpawn(player, (DominationSpawnSquare) player.getPosition()).doAction();
            }
        }

        //Control if someone is dead
        new DeathsFinder().runDeathsFinder(server, table, player);

        //Replace all the AmmoTiles in the TileSquares.
        ArrayList<Square> tileSquares = table.getGameMap().getGridAsList().stream().filter(square1 -> table.getGameMap().getTileSquares().contains(square1)).collect(Collectors.toCollection(ArrayList::new));
        for (Square tileSquare : tileSquares) {
            TileSquare tileSquare1 = (TileSquare) tileSquare;
            if(tileSquare1.getTile()==null){
                tileSquare1.addTile(table.getAmmoTileDeck().draw());
            }
        }

        //Replace all the Weapons in the SpawnSquares.
        ArrayList<Square> spawnSquares = table.getGameMap().getGridAsList().stream().filter(square1 -> table.getGameMap().getSpawnSquares().contains(square1)).collect(Collectors.toCollection(ArrayList::new));
        for (Square spawnSquare : spawnSquares) {
            SpawnSquare spawnSquare1 = (SpawnSquare) spawnSquare;
            if(spawnSquare1.getWeapons().size()<3){
                while(spawnSquare1.getWeapons().size()<3 && !table.getWeaponDeck().getActiveCards().isEmpty()) {
                    spawnSquare1.addWeapon(table.getWeaponDeck().draw());
                }
            }
        }
    }

    private void doAction (Server server, GameTable table, Targets targets){
        final String errorProperty = "error";
        targets.reset();
        ArrayList<DominationSpawnSquare> targetsInitial = new ArrayList<>(targets.getSquaresDamaged());
        boolean resultAction = new ActionManager(player, finalFrenzy, beforeFirstPlayer).runAction(server, table, targets);
        while (!resultAction && server.isConnected(player)){
            targets = new Targets(new ArrayList<>(targetsInitial));
            try {
                server.sendMessage(player, new MessageRetriever().retrieveMessage(errorProperty));
            } catch (UnavailableUserException e) {
            }
            resultAction = new ActionManager(player, finalFrenzy, beforeFirstPlayer).runAction(server, table, targets);
        }
    }

    private void doPowerUps(Server server, GameTable table){
        ArrayList<FunctionalEffect> powerUpEffects = new ArrayList<>();
        try {
            powerUpEffects.addAll(new PowerUpAction().newtonUse(server, table, player));
        } catch (UnavailableUserException e) {
        }
        try {
            powerUpEffects.addAll(new PowerUpAction().teleporterUse(server, table, player));
        } catch (UnavailableUserException e) {
        }
        powerUpEffects.forEach(FunctionalEffect::doAction);
    }
}
