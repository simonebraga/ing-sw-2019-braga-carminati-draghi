package it.polimi.ingsw.view;

import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.mapclasses.Square;

public interface ViewInterface {

    void logout();
    void sendMessage(String s);
    void notifyEvent(String s);
    int choosePlayer(Figure[] f);
    int chooseWeapon(WeaponName[] w);
    int chooseString(String[] s);
    int chooseDirection(Character[] c);
    int chooseColor(Color[] c);
    int choosePowerup(Powerup[] p);
    int chooseMap(int[] m);
    int chooseMode(Character[] c);
    int chooseSquare(Square[] s);
    Boolean booleanQuestion(String s);
    int[] chooseMultiplePowerup(Powerup[] p);
    int[] chooseMultipleWeapon(WeaponName[] w);
    void notifyModelUpdate();
}
