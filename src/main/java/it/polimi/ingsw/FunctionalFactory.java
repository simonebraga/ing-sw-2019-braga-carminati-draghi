package it.polimi.ingsw;

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
}
