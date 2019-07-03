package it.polimi.ingsw;

import it.polimi.ingsw.view.cli.CliMain;
import it.polimi.ingsw.view.gui.GuiMain;

/**
 * This class represents a object which is the first object that interacts with the user on client side.
 *
 * @author simonebraga
 */
public class MainClient {

    /**
     * This method is the first method of the client application and it gets optional commands as arguments. It will invoke the CLI or the GUI.
     *
     * @param args an array of String which contains optional starting parameters:
     *
     * <p>Allowed parameters:<br>
     *  -serverIp "a string containing the server ip to which create a connection<br>
     *  -clientIp "a string containign the custom client ip to use client-side<br>
     *  -interface "GUI | CLI" to specify which interface to use<br><br>
     *   Parameters can be in any order. Parameters fields must be preceded by '-'.<br>
     *   Default parameters are read from file. Default value for -interface parameter is "GUI".</p>
     */
    public static void main(String[] args) {

        String[] newArgs = new String[2];
        int interfaceType = -1;

        for (int i = 0 ; i < args.length ; i++) {
            if ((i+1 < args.length) && (args[i+1].toCharArray()[0] == '-')) {
                System.out.println("Error parsing arguments");
                System.exit(0);
            }
            switch (args[i]) {
                case "-serverIp": {
                    if (newArgs[0] == null) {
                        i++;
                        if (i < args.length)
                            newArgs[0] = args[i];
                        else {
                            System.out.println("Error parsing arguments");
                            System.exit(0);
                        }
                    } else {
                        System.out.println("Error parsing arguments");
                        System.exit(0);
                    }
                    break;
                }
                case "-clientIp": {
                    if (newArgs[2] == null) {
                        i++;
                        if (i < args.length)
                            newArgs[1] = args[i];
                        else {
                            System.out.println("Error parsing arguments");
                            System.exit(0);
                        }
                    } else {
                        System.out.println("Error parsing arguments");
                        System.exit(0);
                    }
                    break;
                }
                case "-interface": {

                    if (interfaceType != -1) {
                        System.out.println("Error parsing arguments");
                        System.exit(0);
                    }

                    i++;
                    if (i < args.length) {
                        switch (args[i].toLowerCase()) {
                            case "gui": {
                                interfaceType = 0;
                                break;
                            }
                            case "cli": {
                                interfaceType = 1;
                                break;
                            }
                        }
                    } else {
                        System.out.println("Error parsing arguments");
                        System.exit(0);
                    }
                    break;
                }
                default: {
                    System.out.println("Error parsing arguments");
                    System.exit(0);
                }
            }
        }
        switch (interfaceType) {
            case 1: {
                CliMain.main(newArgs);
                break;
            }
            case 0:
            default: {
                GuiMain.main(newArgs);
                break;
            }
        }
    }
}
