package it.polimi.ingsw.model.mapclasses;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.exceptionclasses.KilledSpawnSquareException;
import it.polimi.ingsw.model.playerclasses.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents the SpawnSquare when the game mode is Domination Mode.
 */
public class DominationSpawnSquare extends SpawnSquare{

    /**
     * Represents the points that must be distributed to the Players when the game finishes.
     */
    private static final ArrayList<Integer> value = new ArrayList<>(Arrays.asList(8, 6, 4, 2, 1, 1));

    /**
     * Represents the damage inflicted by the other Players to the SpawnSquare. It can contain repetitions (when a Player inflicts more than one damage to the SpawnSquare).
     */
    private ArrayList<Player> damage;

    public DominationSpawnSquare(Border up, Border down, Border left, Border right, Color color) {
        super(up, down, left, right, color);
        damage = new ArrayList<>();
    }

    public static ArrayList<Integer> getValue() {
        return value;
    }

    public ArrayList<Player> getDamage() {
        return damage;
    }

    public void setDamage(ArrayList<Player> damage) {
        this.damage = damage;
    }

    /**
     * Adds one damage from the Player to the SpawnPoint.
     * <p>When the SpawnPoint gets the 8th damage it also throws a KilledSpawnSquareException.</p>
     * @param shooter The Player that has done the damage.
     * @throws KilledSpawnSquareException The SpawnSquare has been killed: it has received the 8th damage mark.
     */
    public void addDamage(Player shooter) throws KilledSpawnSquareException{
        getDamage().add(shooter);
        if(getDamage().size()==8){
            throw new KilledSpawnSquareException();
        }
    }
}
