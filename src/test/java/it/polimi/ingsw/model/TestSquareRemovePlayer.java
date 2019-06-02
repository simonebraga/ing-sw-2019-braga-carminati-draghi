package it.polimi.ingsw.model;

import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.playerclasses.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the normal functioning of the method removePlayer: it has to remove the target player and the other players must not change
 */
class TestSquareRemovePlayer {

    Square square;
    Player playerToRemove;
    ArrayList<Player> players;

    @BeforeEach
    void setUp() {
        square = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING,0,0);
        players = new ArrayList<Player>();
        playerToRemove= new Player(Figure.DESTRUCTOR, "nick1");
        players.add(playerToRemove);
        players.add(new Player(Figure.DOZER, "nick2"));
        players.add(new Player(Figure.VIOLET, "nick3"));
        players.add(new Player(Figure.SPROG, "nick4"));
        square.setPlayers((ArrayList<Player>) players.clone());
        players.remove(playerToRemove);
    }

    @Test
    void removePlayer() {
        square.removePlayer(playerToRemove);
        assertTrue(players.containsAll(square.getPlayers()) && square.getPlayers().containsAll(players));
    }

    @AfterEach
    void tearDown() {
        square = null;
        playerToRemove = null;
        players = null;
    }
}