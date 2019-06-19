package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.smartmodel.SmartModel;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.ViewInterface;

import java.util.Scanner;

/**
 * This class contains the command line interface implementation.
 *
 * @author Draghi96
 */
public class CliMain implements ViewInterface {

    /**
     * This final attribute represents the ANSI code for font reset.
     */
    public static final String ANSI_RESET = "\u001B[0m";

    /**
     * This final attribute represents the ANSI code black font color.
     */
    public static final String ANSI_BLACK = "\u001B[30m";

    /**
     * This final attribute represents the ANSI code red font color.
     */
    public static final String ANSI_RED = "\u001B[31m";

    /**
     * This final attribute represents the ANSI code green font color.
     */
    public static final String ANSI_GREEN = "\u001B[32m";

    /**
     * This final attribute represents the ANSI code yellow font color.
     */
    public static final String ANSI_YELLOW = "\u001B[33m";

    /**
     * This final attribute represents the ANSI code blue font color.
     */
    public static final String ANSI_BLUE = "\u001B[34m";

    /**
     * This final attribute represents the ANSI code purple font color.
     */
    public static final String ANSI_PURPLE = "\u001B[35m";

    /**
     * This final attribute represents the ANSI code cyan font color.
     */
    public static final String ANSI_CYAN = "\u001B[36m";

    /**
     * This final attribute represents the ANSI code white font color.
     */
    public static final String ANSI_WHITE = "\u001B[37m";



    /**
     * This attribute is the input scanner.
     */
    private static Scanner scannerIn = new Scanner(System.in);

    /**
     * This attribute is the Client object that communicates with the server.
     */
    private static Client client;

    /**
     * This attribute represents the client nickname.
     */
    private String nickname;

    /**
     * This attribute represents the client's simplified view of the model.
     */
    private SmartModel model;



    /**
     * This method clears the console's content.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) {
        CliMain temp = new CliMain();
        temp.launch(args);
    }

    /**
     * This method implements the application using CLI.
     */
    public void launch(String[] args) {

        clearScreen();
        System.out.println("Welcome to Adrenaline!\n");

        //create client
        try {
            client = new Client(chooseNetworkTechnology(),this);
            System.out.println("Client created");
        } catch (Exception e) {
            System.out.println("Server not responding.\n");
            launch(args);
        }

        nickname = chooseNickName();

        //retrieve match info
        try {
            model = client.getModelUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method asks the user which nickname the user wish to use.
     */
    private String chooseNickName() {

        String nickname;
        int loginOutput;
        boolean success = false;
        do {
            scannerIn.next();
            System.out.print("Choose a nickname: ");
            nickname = scannerIn.nextLine();
            loginOutput = client.login(nickname);

            switch (loginOutput) {
                case 0 : {
                    System.out.println(ANSI_GREEN + "Successful registration" + ANSI_RESET);
                    success = true;
                    break;
                }
                case 1 : {
                    System.out.println(ANSI_RED + "Nickname already chosen" + ANSI_RESET);
                    break;
                }
                case 2 : {
                    System.out.println("Logged in");
                    success = true;
                    break;
                }
                case 3 : {
                    System.out.println(ANSI_RED + "Already logged in" + ANSI_RESET);
                    break;
                }
                case 4 : {
                    System.out.println(ANSI_RED + "Something went wrong" + ANSI_RESET);
                    break;
                }
            }
        } while (!success);
        return nickname;
    }

    /**
     * This method asks the user which network technology the user wish to use choosing between RMI and Socket technologies.
     */
    private int chooseNetworkTechnology() {

        Integer rmiOrCli;
        do {
            System.out.print("Choose network technology (0 - RMI | 1 - Socket): ");
            if (scannerIn.hasNextInt())
                rmiOrCli = scannerIn.nextInt();
            else{
                rmiOrCli = -1;
                scannerIn.next();
            }
        } while (rmiOrCli != 0 && rmiOrCli != 1);
        return rmiOrCli;
    }

    @Override
    public void logout() {

    }

    @Override
    public void sendMessage(String s) {

    }

    @Override
    public void notifyEvent(String s) {

    }

    @Override
    public int choosePlayer(Figure[] f) {
        return 0;
    }

    @Override
    public int chooseWeapon(WeaponName[] w) {
        return 0;
    }

    @Override
    public int chooseString(String[] s) {
        return 0;
    }

    @Override
    public int chooseDirection(Character[] c) {
        return 0;
    }

    @Override
    public int chooseColor(Color[] c) {
        return 0;
    }

    @Override
    public int choosePowerup(Powerup[] p) {
        return 0;
    }

    @Override
    public int chooseMap(int[] m) {
        return 0;
    }

    @Override
    public int chooseMode(Character[] c) {
        return 0;
    }

    @Override
    public int chooseSquare(Square[] s) {
        return 0;
    }

    @Override
    public Boolean booleanQuestion(String s) {
        return null;
    }

    @Override
    public int[] chooseMultiplePowerup(Powerup[] p) {
        return new int[0];
    }

    @Override
    public int[] chooseMultipleWeapon(WeaponName[] w) {
        return new int[0];
    }

    @Override
    public void notifyModelUpdate() {

    }
}
