package it.polimi.ingsw;

import it.polimi.ingsw.view.cli.CliMain;
import it.polimi.ingsw.view.gui.GuiMain;

public class MainClient {

    public static void main(String[] args) {
        if (Integer.valueOf(args[0]) == 0) {
            CliMain.main(args);
        }
        else if (Integer.valueOf(args[0]) == 1) {
            GuiMain.main(args);
        }
    }
}
