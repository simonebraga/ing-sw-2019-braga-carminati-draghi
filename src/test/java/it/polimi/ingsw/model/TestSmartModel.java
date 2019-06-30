package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cardclasses.AmmoTile;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.PowerupName;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.exceptionclasses.FrenzyModeException;
import it.polimi.ingsw.model.gameinitialization.GameInitializer;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.mapclasses.TileSquare;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.model.smartmodel.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestSmartModel {

    static GameTable gameTable;
    static Player player1;
    static Player player2;
    static Player player3;

    @BeforeAll
    static void setupModel() {
        player1 = new Player(Figure.DOZER,"Simone");
        player2 = new Player(Figure.BANSHEE,"Alessandro");
        player3 = new Player(Figure.VIOLET,"Samuele");
        ArrayList<Player> players = new ArrayList<>(Arrays.asList(player1,player2,player3));
        gameTable = new GameInitializer('N',2,players).run();
        for (Player player : gameTable.getPlayers()) {
            player.getPointTrack().setValue(new ArrayList<>(Arrays.asList(1, 1, 3)));
            player.getDamageTrack().setDamage(new ArrayList<>(Arrays.asList(player3,player2,player2)));
            player.getMarkTrack().addMarks(player1,2);
            player.getAmmoPocket().reduceAmmo(new ArrayList<>(Arrays.asList(Color.RED,Color.YELLOW,Color.BLUE)));
            player.getAmmoPocket().addAmmo(new ArrayList<>(Arrays.asList(Color.RED,Color.RED,Color.BLUE)));
            player.getWeaponPocket().addWeapon(new Weapon(new ArrayList<>(Arrays.asList(Color.RED,Color.BLUE,Color.YELLOW)),WeaponName.FURNACE,true));
            player.getPowerupPocket().setPowerups(new ArrayList<>(Arrays.asList(new Powerup(Color.BLUE,PowerupName.NEWTON))));
            player.setPosition(gameTable.getGameMap().getSquare(1, 1));
        }
        for (SpawnSquare spawnSquare : gameTable.getGameMap().getSpawnSquares()) {
            spawnSquare.addWeapon(new Weapon(new ArrayList<>(Arrays.asList(Color.RED,Color.BLUE,Color.YELLOW)),WeaponName.FURNACE,true));
        }
        for (TileSquare tileSquare : gameTable.getGameMap().getTileSquares()) {
            tileSquare.addTile(new AmmoTile(new ArrayList<>(Arrays.asList(Color.RED,Color.BLUE)),1));
        }
        try {
            gameTable.getKillshotTrack().kill(player1);
        } catch (FrenzyModeException ignored) {
        }
    }

    @Test
    void testSerialization() {
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTable);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        assertEquals(smartModel.toString(),smartModelAfterSerialization.toString());
    }

    @Test
    void testDamage() {
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTable);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        for (String nickname : smartModelAfterSerialization.getSmartPlayerMap().keySet())
            assertEquals(new ArrayList<>(Arrays.asList(player3.getFigure(),player2.getFigure(),player2.getFigure())),smartModelAfterSerialization.getSmartPlayerMap().get(nickname).getDamage());
    }

    @Test
    void testMarks() {
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTable);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        Map<Figure,Integer> expectedMarks = new HashMap<>();
        expectedMarks.put(player1.getFigure(),2);
        for (String nickname : smartModelAfterSerialization.getSmartPlayerMap().keySet())
            assertEquals(expectedMarks,smartModelAfterSerialization.getSmartPlayerMap().get(nickname).getMarks());
    }

    @Test
    void testAmmo() {
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTable);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        Map<Color,Integer> expectedAmmo = new HashMap<>();
        expectedAmmo.put(Color.RED,2);
        expectedAmmo.put(Color.BLUE,1);
        expectedAmmo.put(Color.YELLOW,0);
        for (String nickname : smartModelAfterSerialization.getSmartPlayerMap().keySet())
            assertEquals(expectedAmmo,smartModelAfterSerialization.getSmartPlayerMap().get(nickname).getAmmo());
    }

    @Test
    void testWeapon() {
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTable);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        ArrayList<SmartWeapon> expectedWeapon = new ArrayList<>();
        SmartWeapon smartWeapon1 = new SmartWeapon();
        smartWeapon1.setWeaponName(WeaponName.FURNACE);
        smartWeapon1.setLoaded(true);
        expectedWeapon.add(smartWeapon1);
        for (String nickname : smartModelAfterSerialization.getSmartPlayerMap().keySet())
            assertEquals(expectedWeapon,smartModelAfterSerialization.getSmartPlayerMap().get(nickname).getWeapons());
    }

    @Test
    void testPowerup() {
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTable);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        ArrayList<SmartPowerup> expectedPowerup = new ArrayList<>();
        SmartPowerup smartPowerup1 = new SmartPowerup();
        smartPowerup1.setColor(Color.BLUE);
        smartPowerup1.setPowerupName(PowerupName.NEWTON);
        expectedPowerup.add(smartPowerup1);
        for (String nickname : smartModelAfterSerialization.getSmartPlayerMap().keySet())
            assertEquals(expectedPowerup,smartModelAfterSerialization.getSmartPlayerMap().get(nickname).getPowerups());
    }

    @Test
    void testPosition() {
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTable);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        for (String nickname : smartModelAfterSerialization.getSmartPlayerMap().keySet()) {
            assertEquals(1,smartModelAfterSerialization.getSmartPlayerMap().get(nickname).getPosX());
            assertEquals(1,smartModelAfterSerialization.getSmartPlayerMap().get(nickname).getPosY());
        }
    }

    @Test
    void testMapIndex() {
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTable);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        assertEquals(2,smartModelAfterSerialization.getMapIndex());
    }

    @Test
    void testGameMode() {
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTable);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        assertEquals(false,smartModelAfterSerialization.getDomination());
    }

    @Test
    void testSpawnWeapon() {
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTable);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        WeaponName expectedWeapon = WeaponName.FURNACE;
        for (Color color : smartModelAfterSerialization.getSpawnWeaponMap().keySet())
            for (WeaponName weaponName : smartModelAfterSerialization.getSpawnWeaponMap().get(color))
                assertEquals(expectedWeapon,weaponName);
    }

    @Test
    void testMapTiles() {
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTable);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        SmartTile expectedTile = new SmartTile();
        expectedTile.setAmmo(new ArrayList<>(Arrays.asList(Color.RED,Color.BLUE)));
        expectedTile.setPowerup(1);
        for (SmartTile smartTile : smartModelAfterSerialization.getMapTiles()) {
            assertEquals(expectedTile,smartTile);
        }
    }

    @Test
    void testKillshotTrack() {
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTable);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        ArrayList<Figure> expectedKillshottrack = new ArrayList<>(Arrays.asList(Figure.DOZER));
        assertEquals(expectedKillshottrack,smartModelAfterSerialization.getKillshotTrack());
    }

    @Test
    void testspawnDamageTrack() {
        ArrayList<Player> players = new ArrayList<>(Arrays.asList(player1,player2,player3));
        GameTable gameTableDomination = new GameInitializer('D',2,players).run();
        for (SpawnSquare spawnSquare : gameTableDomination.getGameMap().getSpawnSquares()) {
            DominationSpawnSquare dominationSpawnSquare = (DominationSpawnSquare) spawnSquare;
            dominationSpawnSquare.addDamage(player2);
            dominationSpawnSquare.addDamage(player3);
        }
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTableDomination);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        ArrayList<Figure> expectedDamage = new ArrayList<>(Arrays.asList(Figure.BANSHEE,Figure.VIOLET));
        for (Color color : smartModelAfterSerialization.getSpawnDamageTrack().keySet()) {
            assertEquals(expectedDamage,smartModelAfterSerialization.getSpawnDamageTrack().get(color));
        }
    }

    @Test
    void testPointTrack() {
        SmartModel smartModel = new SmartModel();
        smartModel.update(gameTable);
        smartModel.setMapIndex(2);
        SmartModel smartModelAfterSerialization = SmartModel.fromString(smartModel.toString());
        ArrayList<Integer> expectedPointTrack = new ArrayList<>(Arrays.asList(1,1,3));
        for (SmartPlayer smartPlayer : smartModelAfterSerialization.getSmartPlayerMap().values())
            assertEquals(expectedPointTrack,smartPlayer.getPointTrack());
    }
}