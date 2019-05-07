package it.polimi.ingsw.model.effectclasses;

import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.cardclasses.AmmoTile;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.exceptionclasses.FullPocketException;
import it.polimi.ingsw.model.exceptionclasses.KilledPlayerException;
import it.polimi.ingsw.model.exceptionclasses.KilledSpawnSquareException;
import it.polimi.ingsw.model.exceptionclasses.OverKilledPlayerException;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.mapclasses.TileSquare;
import it.polimi.ingsw.model.playerclasses.Player;

import java.util.ArrayList;

/**
 * Factory class that contains the method to create different types of functionalEffect.
 */
public class FunctionalFactory {

    /**
     * Creates and returns a FunctionalEffect that grabs an AmmoTile in a AmmoSquare.
     * @param player The Player that does the action.
     * @param table The table that contains all the data from the current match.
     * @return The FunctionalEffect that grabs an AmmoTile in a AmmoSquare.
     */
    public FunctionalEffect createGrabAmmo(Player player, GameTable table){
        return () -> {
            TileSquare square = (TileSquare) player.getPosition();
            AmmoTile tileTemp = square.removeTile();
            player.getAmmoPocket().addAmmo(tileTemp.getAmmo());
            if(tileTemp.getPowerup() == 1){
                try {
                    player.getPowerupPocket().addPowerup(table.getPowerupDeck().draw());
                } catch (FullPocketException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Creates and returns a FunctionalEffect that grabs a Weapon in a SpawnSquare.
     * @param player The Player that does the action.
     * @param weapon The Weapon that the Player wants to take.
     * @return The FunctionalEffect that grabs a Weapon in a SpawnSquare.
     */
    public FunctionalEffect createGrabWeapon(Player player, Weapon weapon){
        return () -> {
            SpawnSquare square = (SpawnSquare) player.getPosition();
            player.getWeaponPocket().addWeapon(square.takeWeapon(weapon));
        };
    }

    /**
     * Creates and returns a FunctionalEffect that switches a Weapon with a SpawnSquare.
     * @param player The Player that does the action.
     * @param weaponToTake The Weapon that the Player wants to take.
     * @param weaponToGive The Weapon that the Player wants to put in the SpawnSquare.
     * @return The FunctionalEffect that switches a Weapon with a SpawnSquare.
     */
    public FunctionalEffect createSwitchWeapon(Player player, Weapon weaponToTake, Weapon weaponToGive){
        return () -> {
            player.getWeaponPocket().getWeapons().remove(weaponToGive);
            SpawnSquare square = (SpawnSquare) player.getPosition();
            try {
                player.getWeaponPocket().addWeapon(square.switchWeapon(weaponToTake, weaponToGive));
            } catch (FullPocketException e) {
                e.printStackTrace();
            }
        };
    }

    /**
     *This method returns a FunctionalEffect that performs a payment done by a player.
     *
     * @param player a Player that needs to pay a price.
     * @param price an ArraList of Color that represents the ammo boxes amount to pay.
     * @return a FunctionalEffect object that performs the payment.
     */
    public FunctionalEffect createPay(Player player, ArrayList<Color> price){
        return () -> player.getAmmoPocket().reduceAmmo(price);
    }

    /**
     * This method returns a FunctionalEffect object that performs a player movement.
     *
     * @param player a Player to be moved on the map grid.
     * @param destination the Square destination for a player.
     * @return a FunctionalEffect object that performs a player movement.
     */
    public FunctionalEffect createMove(Player player, Square destination){
        return () -> {
            player.getPosition().removePlayer(player);
            player.move(destination);
            destination.addPlayer(player);
        };
    }

    /**
     * Creates and returns a FunctionalEffect that adds damage to a player, and commutes the marks of the target player in damage, and adds new marks to the target player
     * @param killer the player that performs the damage
     * @param target the player that suffers the damage
     * @param damage the cardinality of the damage
     * @param marks the cardinality of the new damage
     * @return FunctionalEffect that adds damage and marks to a player
     */
    public FunctionalEffect createDamagePlayer(Player killer, Player target, Integer damage, Integer marks){
        return () -> {
            Integer toAdd = 0;
            if (damage > 0)
                toAdd = target.getMarkTrack().removeMarks(killer);
            try {
                target.getDamageTrack().addDamage(killer,damage + toAdd);
            } catch (KilledPlayerException e) {
                throw new KilledPlayerException();
            } catch (OverKilledPlayerException e) {
                throw new OverKilledPlayerException();
            }
            target.getMarkTrack().addMarks(killer,marks);
        };
    }

    /**
     * Creates and returns a FunctionalEffect that adds damage to a spawn square (is used only in domination mode)
     * The cardinality of the damage is not specified because only 1 damage per time can be done to a square
     * @param killer the player that performs the damage
     * @param spawnSquare the square that suffers the damage
     * @return FunctionalEffect that adds 1 damage to a spawn square
     */
    public FunctionalEffect createDamageSpawn(Player killer, DominationSpawnSquare spawnSquare){
        return () -> {
            try {
                spawnSquare.addDamage(killer);
            } catch (KilledSpawnSquareException e) {
                throw new KilledSpawnSquareException();
            }
        };
    }
}
