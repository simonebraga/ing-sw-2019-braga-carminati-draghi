package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.management.PlatformLoggingMXBean;
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
        square = new Square(Border.NOTHING, Border.NOTHING, Border.NOTHING, Border.NOTHING);
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
}