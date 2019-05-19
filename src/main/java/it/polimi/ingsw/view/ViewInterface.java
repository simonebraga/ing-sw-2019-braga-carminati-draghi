package it.polimi.ingsw.view;

import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;

public interface ViewInterface {

    Figure choosePlayer(Figure[] f);
    WeaponName chooseWeapon(WeaponName[] w);
    String chooseString(String[] s);
    Powerup choosePowerup(Powerup[] p);
    Boolean booleanQuestion(String s);
    Powerup[] chooseMultiplePowerups(Powerup[] p);
    WeaponName[] chooseMultipleWeapons(WeaponName[] w);
}
