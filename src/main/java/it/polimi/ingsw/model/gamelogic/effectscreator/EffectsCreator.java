package it.polimi.ingsw.model.gamelogic.effectscreator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;

/**
 * Represents all the classes that creates some effects.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MoveAndShootCreator.class, name = "MoveAndShoot"),
        @JsonSubTypes.Type(value = MoveCreator.class, name = "Move"),
        @JsonSubTypes.Type(value = PayCreator.class, name = "Pay"),
        @JsonSubTypes.Type(value = ShootAOECreator.class, name = "ShootAOE"),
        @JsonSubTypes.Type(value = ShootAndMarkAOECreator.class, name = "ShootAndMarkAOE"),
        @JsonSubTypes.Type(value = ShootChainCreator.class, name = "ShootChain"),
        @JsonSubTypes.Type(value = ShootCreator.class, name = "Shoot"),
        @JsonSubTypes.Type(value = ShootDirectionCreator.class, name = "ShootDirection"),
        @JsonSubTypes.Type(value = ShootRoomCreator.class, name = "ShootRoom"),
        @JsonSubTypes.Type(value = ShootShockWaveCreator.class, name = "ShootShockWave"),
        @JsonSubTypes.Type(value = ShootVortexCreator.class, name = "ShootVortex"),
        @JsonSubTypes.Type(value = ShootSpawnSquareCreator.class, name = "ShootSpawnSquare")
})
public interface EffectsCreator {
    /**
     * Sets the player that does the actions.
     * @param player The player that does the actions.
     */
    void setPlayer(Player player);

    /**
     * This method is used to set and creates the effects.
     * @param server The server where the game is running.
     * @param table Represents all the data of the game.
     * @param targets The targets of the shoots done during the turn.
     * @return A list of FunctionalEffects that represent all the modification that are done on the data.
     * @throws IllegalActionException The player selected an illegal action. This exception makes the turn restart.
     * @throws UnavailableUserException The player has disconnected or didn't answer within the time limit.
     */
    ArrayList<FunctionalEffect> run(Server server, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException;
}
