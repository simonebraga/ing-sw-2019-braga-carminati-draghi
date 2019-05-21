package it.polimi.ingsw.model.gamelogic.actions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.effectclasses.FunctionalEffect;
import it.polimi.ingsw.model.exceptionclasses.IllegalActionException;
import it.polimi.ingsw.model.gamelogic.effectscreator.EffectsCreator;
import it.polimi.ingsw.model.gamelogic.effectscreator.Targets;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The class that builds the action of shooting.
 */
public class ShootAction implements Action{

    @Override
    public List<FunctionalEffect> run(Controller controller, GameTable table, Player player, Targets targets) throws IllegalActionException, UnavailableUserException {
        ArrayList<FunctionalEffect> effects = new ArrayList<>();

        ArrayList<Weapon> weapons = (ArrayList<Weapon>) player.getWeaponPocket().getWeapons().stream().filter(Weapon::getLoaded).collect(Collectors.toList());
        if (weapons.isEmpty()){
            throw new IllegalActionException();
        }

        Weapon weaponToUse = controller.chooseWeapon(player, weapons);

        //Import from Json
        ObjectMapper objectMapper = new ObjectMapper();
        File weaponFile = new File("src/main/resources/weapons/"+weaponToUse.getName()+".json");
        Map<String, ArrayList<EffectsCreator>> map = null;
        try {
            map = objectMapper.readValue(weaponFile, new TypeReference<Map<String,ArrayList<EffectsCreator>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> useCases = new ArrayList<>(map.keySet());
        String choice = controller.chooseString(player, useCases);

        for (EffectsCreator effectsCreator : map.get(choice)) {
            effectsCreator.setPlayer(player);
            effects.addAll(effectsCreator.run(controller, table, targets));
        }
        weaponToUse.setIsLoaded(false);

        if(targets.getPlayersTargeted().stream().distinct().count() != targets.getPlayersTargeted().size() || targets.getPlayersTargeted().contains(player)){
            throw new IllegalActionException();
        }

        return effects;
    }
}
