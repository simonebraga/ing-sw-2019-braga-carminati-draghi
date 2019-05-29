package it.polimi.ingsw.model.gamelogic.effectscreator;

import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.gamelogic.turn.MessageRetriever;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;

/**
 * Sets and creates the effect that shoots to a player and marks
 * all the other targets.
 */
public class ShootAndMarkAOECreator implements EffectsCreator{
    /**
     * The player that shoots.
     */
    private Player player;

    /**
     * Represents the number of damages that the shoot does.
     */
    private Integer damages;

    /**
     * Represents the number of marks that the shootAOE does.
     */
    private Integer marks;

    /**
     * Default constructor. Sets all the attributes.
     */
    public ShootAndMarkAOECreator(Player shooter, Integer damages, Integer marks) {
        this.player = shooter;
        this.damages = damages;
        this.marks = marks;
    }

    public ShootAndMarkAOECreator() {
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public ArrayList<FunctionalEffect> run(Server server, GameTable table, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<FunctionalEffect> effects;
        Boolean playerOrSquare = true;

        if (table.getIsDomination()) {
            playerOrSquare = server.booleanQuestion(player, new MessageRetriever().retrieveMessage("playerOrSquare"));
        }

        if(playerOrSquare) {
            ShootCreator shootCreator = new ShootCreator(player, player, true, damages, 0, 1, -1, false);
            effects = new ArrayList<> (shootCreator.run(server, table, targets));

            shootCreator.getTarget().getPosition().getPlayers().forEach(player -> {
                effects.add(new FunctionalFactory().createDamagePlayer(this.player, player, 0, marks));
                if(!targets.getPlayersTargeted().contains(player)){
                    targets.getPlayersTargeted().add(player);
                }
            });
        }else{
            ShootSpawnSquareCreator shootSpawnSquareCreator = new ShootSpawnSquareCreator(player, player, true, damages, 0, 1, -1, false);
            effects = new ArrayList<> (shootSpawnSquareCreator.run(server, table, targets));

            shootSpawnSquareCreator.getTarget().getPlayers().forEach(player -> {
                effects.add(new FunctionalFactory().createDamagePlayer(this.player, player, 0, marks));
                if(!targets.getPlayersTargeted().contains(player)){
                    targets.getPlayersTargeted().add(player);
                }
            });
        }
        return effects;
    }

    public Player getPlayer() {
        return player;
    }

    public Integer getDamages() {
        return damages;
    }

    public void setDamages(Integer damages) {
        this.damages = damages;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }
}
