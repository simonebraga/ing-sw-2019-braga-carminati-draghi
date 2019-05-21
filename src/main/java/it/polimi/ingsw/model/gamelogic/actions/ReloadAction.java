package it.polimi.ingsw.model.gamelogic.actions;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.gamelogic.effectscreator.PayCreator;
import it.polimi.ingsw.model.gamelogic.effectscreator.Targets;
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
    public List<FunctionalEffect> run(Controller controller, GameTable table, Player player, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<Weapon> weapons = (ArrayList<Weapon>) player.getWeaponPocket().getWeapons().stream().filter(weapon -> !weapon.getLoaded()).collect(Collectors.toList());
        ArrayList<FunctionalEffect> effects = new ArrayList<>();
        if(!weapons.isEmpty()){
            Boolean choice = controller.booleanQuestion(player, "Do you want to reload?");
            if(choice){
                weapons = controller.chooseMultipleWeapon(player, weapons);
                for (Weapon weapon : weapons) {
                    effects.addAll(new PayCreator(player, weapon.getPrice()).run(controller, table, targets));
                    effects.add(weapon::reload);
                }
            }
        }
        return effects;
    }
}
