package it.polimi.ingsw.model.gamelogic.actions;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.effectclasses.FunctionalFactory;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.gamelogic.effectscreator.PayCreator;
import it.polimi.ingsw.model.gamelogic.effectscreator.Targets;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.mapclasses.TileSquare;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.util.ArrayList;
import java.util.List;

/**
 * The class that builds the action of grabbing.
 */
public class GrabAction implements Action{
    public List<FunctionalEffect> run(Controller controller, GameTable table, Player player, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<FunctionalEffect> effects = new ArrayList<>();

        if (table.getGameMap().getTileSquares().contains(player.getPosition())) {
            TileSquare position = (TileSquare) player.getPosition();
            if (position.getTile() == null) {
                throw new IllegalActionException();
            } else {
                effects.add(new FunctionalFactory().createGrabAmmo(player, table));
            }
        }else{
            ArrayList<Weapon> weaponsAvailable = ((SpawnSquare) player.getPosition()).getWeapons();
            Weapon choice;

            choice = controller.chooseWeapon(player, weaponsAvailable);

            if(player.getWeaponPocket().getWeapons().size() == 3){
                Weapon weaponToDiscard = controller.chooseWeapon(player, player.getWeaponPocket().getWeapons());
                effects.add(new FunctionalFactory().createSwitchWeapon(player, choice, weaponToDiscard));
            }else{
                effects.add(new FunctionalFactory().createGrabWeapon(player, choice));
            }
            ArrayList<Color> price = new ArrayList<>(choice.getPrice());
            price.remove(0);
            effects.addAll(new PayCreator(player, price).run(controller, table, targets));
        }
        return effects;
    }
}
