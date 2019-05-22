package it.polimi.ingsw.model.gamelogic.effectscreator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.controller.Controller;
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
    void setPlayer(Player player);
    ArrayList<FunctionalEffect> run(Controller controller, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException;
}
