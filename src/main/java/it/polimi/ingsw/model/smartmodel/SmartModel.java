package it.polimi.ingsw.model.smartmodel;

import com.google.gson.Gson;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.mapclasses.TileSquare;
import it.polimi.ingsw.model.playerclasses.Player;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * This class contains all the useful information of the model that must be available to the player.
 * This class can be easily serialized and de-serialized, which is a very important feature for an object that must surf the network.
 */
public class SmartModel {
    private Map<String,SmartPlayer> smartPlayerMap;
    private int mapIndex;
    private Boolean isDomination;
    private final int defaultPointtrackSize;
    private Map<Color,ArrayList<WeaponName>> spawnWeaponMap;
    private ArrayList<SmartTile> mapTiles;
    private ArrayList<Figure> killshotTrack;
    private Map<Color,ArrayList<Figure>> spawnDamageTrack;

    public Map<String, SmartPlayer> getSmartPlayerMap() {
        return smartPlayerMap;
    }

    public int getMapIndex() {
        return mapIndex;
    }

    public Boolean getDomination() {
        return isDomination;
    }

    public int getDefaultPointtrackSize() {
        return defaultPointtrackSize;
    }

    public Map<Color, ArrayList<WeaponName>> getSpawnWeaponMap() {
        return spawnWeaponMap;
    }

    public ArrayList<SmartTile> getMapTiles() {
        return mapTiles;
    }

    public ArrayList<Figure> getKillshotTrack() {
        return killshotTrack;
    }

    public Map<Color, ArrayList<Figure>> getSpawnDamageTrack() {
        return spawnDamageTrack;
    }

    public void setMapIndex(int mapIndex) {
        this.mapIndex = mapIndex;
    }

    public SmartModel() {
        //TODO Setup the reading of default point track
        defaultPointtrackSize = 7;
    }

    public synchronized void update(GameTable gameTable) {
        smartPlayerMap = new HashMap<>();
        for (Player player : gameTable.getPlayers()) {
            SmartPlayer smartPlayer = new SmartPlayer();

            // Setup figure
            smartPlayer.setFigure(player.getFigure());

            // Setup damage
            ArrayList<Figure> damage = new ArrayList<>();
            for (Player player1 : player.getDamageTrack().getDamage())
                damage.add(player1.getFigure());
            smartPlayer.setDamage(damage);

            // Setup marks
            Map<Figure,Integer> marks = new HashMap<>();
            for (Player player1 : player.getMarkTrack().getMarks().keySet())
                marks.put(player1.getFigure(),player.getMarkTrack().getMarks(player1));
            smartPlayer.setMarks(marks);

            // Setup ammo
            EnumMap<Color,Integer> ammo = new EnumMap<>(Color.class);
            for (Color color : Color.values())
                ammo.put(color,player.getAmmoPocket().getAmmo(color));
            smartPlayer.setAmmo(ammo);

            // Setup weapons
            ArrayList<SmartWeapon> smartWeapons = new ArrayList<>();
            for (Weapon weapon : player.getWeaponPocket().getWeapons()) {
                SmartWeapon smartWeapon = new SmartWeapon();
                smartWeapon.setWeaponName(weapon.getName());
                smartWeapon.setLoaded(weapon.getIsLoaded());
                smartWeapons.add(smartWeapon);
            }
            smartPlayer.setWeapons(smartWeapons);

            // Setup powerups
            ArrayList<SmartPowerup> smartPowerups = new ArrayList<>();
            for (Powerup powerup : player.getPowerupPocket().getPowerups()) {
                SmartPowerup smartPowerup = new SmartPowerup();
                smartPowerup.setPowerupName(powerup.getName());
                smartPowerup.setColor(powerup.getColor());
                smartPowerups.add(smartPowerup);
            }
            smartPlayer.setPowerups(smartPowerups);

            // Setup position
            if (player.getPosition() != null) {
                smartPlayer.setPosX(player.getPosition().getX());
                smartPlayer.setPosY(player.getPosition().getY());
            } else {
                smartPlayer.setPosX(-1);
                smartPlayer.setPosY(-1);
            }

            // Setup deaths
            smartPlayer.setDeaths(defaultPointtrackSize - player.getPointTrack().getValue().size());

            // Setup points
            smartPlayer.setPoints(player.getPoints());

            smartPlayerMap.put(player.getUsername(),smartPlayer);
        }

        // Setup is domination
        isDomination = gameTable.getIsDomination();

        // Setup spawn weapons
        spawnWeaponMap = new HashMap<>();
        for (SpawnSquare spawnSquare : gameTable.getGameMap().getSpawnSquares()) {
            ArrayList<WeaponName> arrayList = new ArrayList<>();
            for (Weapon weapon : spawnSquare.getWeapons())
                arrayList.add(weapon.getName());
            spawnWeaponMap.put(spawnSquare.getColor(),arrayList);
        }

        // Setup map tiles
        mapTiles = new ArrayList<>();
        for (TileSquare tileSquare : gameTable.getGameMap().getTileSquares()) {
            if (tileSquare.getTile() != null) {
                SmartTile smartTile = new SmartTile();
                smartTile.setPowerup(tileSquare.getTile().getPowerup());
                smartTile.setAmmo(tileSquare.getTile().getAmmo());
                smartTile.setPosX(tileSquare.getX());
                smartTile.setPosY(tileSquare.getY());
                mapTiles.add(smartTile);
            }
        }

        // Setup killshottrack
        killshotTrack = new ArrayList<>();
        for (Player player : gameTable.getKillshotTrack().getKillTrack())
            killshotTrack.add(player.getFigure());

        // Setup spawn damage track
        spawnDamageTrack = new HashMap<>();
        if (isDomination) {
            for (SpawnSquare spawnSquare : gameTable.getGameMap().getSpawnSquares()) {
                DominationSpawnSquare dominationSpawnSquare = (DominationSpawnSquare) spawnSquare;
                ArrayList<Figure> arrayList = new ArrayList<>();
                for (Player player : dominationSpawnSquare.getDamage())
                    arrayList.add(player.getFigure());
                spawnDamageTrack.put(dominationSpawnSquare.getColor(),arrayList);
            }
        }

    }

    public static SmartModel fromString(String s) {
        return new Gson().fromJson(s,SmartModel.class);
    }

    @Override
    public synchronized String toString() {
        return new Gson().toJson(this);
    }
}
