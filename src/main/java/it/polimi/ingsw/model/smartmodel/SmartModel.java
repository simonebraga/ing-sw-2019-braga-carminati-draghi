package it.polimi.ingsw.model.smartmodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.gamelogic.settings.SettingsJSONParser;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.mapclasses.TileSquare;
import it.polimi.ingsw.model.playerclasses.Player;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
        InputStream file = SmartModel.class.getClassLoader().getResourceAsStream("game_settings.json");
        ObjectMapper mapper = new ObjectMapper();
        SettingsJSONParser settings = new SettingsJSONParser();
        try {
            settings = mapper.readValue(file,SettingsJSONParser.class);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (settings != null)
            defaultPointtrackSize = settings.getBounties().length;
        else
            defaultPointtrackSize = 0;
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

            // Setup pointTrack
            smartPlayer.setPointTrack(player.getPointTrack().getValue());

            // Setup points
            smartPlayer.setPoints(player.getPoints());

            smartPlayerMap.put(player.getUsername(),smartPlayer);
        }

        // Setup is domination
        isDomination = gameTable.getIsDomination();

        // Setup spawn weapons & spawn damage tracks if in Domination mode
        spawnWeaponMap = new HashMap<>();
        spawnDamageTrack = new HashMap<>();
        ArrayList<Square> spawnSquares = gameTable.getGameMap().getGridAsList().stream().filter(square -> gameTable.getGameMap().getSpawnSquares().contains(square)).collect(Collectors.toCollection(ArrayList::new));
        for (Square square : spawnSquares) {
            SpawnSquare square1 = (SpawnSquare) square;
            ArrayList<WeaponName> arrayList = new ArrayList<>();
            for (Weapon weapon : square1.getWeapons())
                arrayList.add(weapon.getName());
            spawnWeaponMap.put(square1.getColor(),arrayList);

            if (isDomination) {
                DominationSpawnSquare square2 = (DominationSpawnSquare) square1;
                ArrayList<Figure> arrayList1 = new ArrayList<>();
                for (Player player : square2.getDamage())
                    arrayList1.add(player.getFigure());
                spawnDamageTrack.put(square2.getColor(),arrayList1);
            }
        }

        // Setup map tiles
        mapTiles = new ArrayList<>();
        ArrayList<Square> tileSquares = gameTable.getGameMap().getGridAsList().stream().filter(square -> gameTable.getGameMap().getTileSquares().contains(square)).collect(Collectors.toCollection(ArrayList::new));
        for (Square square : tileSquares) {
            TileSquare square1 = (TileSquare) square;
            SmartTile smartTile = new SmartTile();
            smartTile.setPowerup(square1.getTile().getPowerup());
            smartTile.setAmmo(square1.getTile().getAmmo());
            smartTile.setPosX(square1.getX());
            smartTile.setPosY(square1.getY());
            mapTiles.add(smartTile);
        }

        // Setup killshottrack
        killshotTrack = new ArrayList<>();
        for (Player player : gameTable.getKillshotTrack().getKillTrack())
            killshotTrack.add(player.getFigure());

    }

    public static SmartModel fromString(String s) {
        return new Gson().fromJson(s,SmartModel.class);
    }

    @Override
    public synchronized String toString() {
        return new Gson().toJson(this);
    }
}
