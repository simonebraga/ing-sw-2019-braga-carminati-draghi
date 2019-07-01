package it.polimi.ingsw.model.gamelogic.actions;

import it.polimi.ingsw.controller.Server;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.gamelogic.effectscreator.PayCreator;
import it.polimi.ingsw.model.gamelogic.effectscreator.Targets;
import it.polimi.ingsw.model.gamelogic.turn.MessageRetriever;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The class that builds the action of reloading.
 */
public class ReloadAction implements Action{

    @Override
    public List<FunctionalEffect> run(Server server, GameTable table, Player player, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<Weapon> weapons = (ArrayList<Weapon>) player.getWeaponPocket().getWeapons().stream().filter(weapon -> !weapon.getIsLoaded()).collect(Collectors.toList());
        ArrayList<FunctionalEffect> effects = new ArrayList<>();
        if(!weapons.isEmpty()){
            server.sendMessage(player, new MessageRetriever().retrieveMessage("reload"));
            weapons = server.chooseMultipleWeapon(player, weapons);
            for (Weapon weapon : weapons) {
                effects.addAll(new PayCreator(player, weapon.getPrice()).run(server, table, targets));
                effects.add(weapon::reload);
            }
        }
        return effects;
    }
}
