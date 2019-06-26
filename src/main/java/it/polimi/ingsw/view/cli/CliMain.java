package it.polimi.ingsw.view.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.mapclasses.*;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.model.smartmodel.SmartModel;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.ViewInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class contains the command line interface implementation of the client.
 *
 * @author Draghi96
 */
public class CliMain implements ViewInterface {

    ////////////////////////////////////////////////////////////// cli formatting constants ///////////////

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
     * This final attribute defines squares width in cli printing.
     */
    private static final int SQUARES_WIDTH = 22;

    /**
     * This final attribute defines squares high in cli printing.
     */
    private static final int SQUARES_HIGH = 10;

    ////////////////////////////////////////////////////////////// match related constants ///////////////

    /**
     * This constant represents the number of maps available in the game.
     */
    private static final int GAME_MAPS_NUMBER = 4;

    /**
     * This constant represents the JSON file path containing all game maps.
     */
    private static final String MAPS_RESOURCES_PATH = "maps.json";

    ////////////////////////////////////////////////////////////// network related constants ///////////////

    /**
     * This final attribute defines the value returned from client.login() method when the registration was successful.
     */
    private static final int SUCCESSFUL_REGISTRATION_SIGNAL = 0;

    /**
     * This final attribute defines the value returned from client.login() method when the nickname was already in use.
     */
    private static final int NICK_NOT_AVAILABLE_SIGNAL = 1;

    /**
     * This final attribute defines the value returned from client.login() method when the login was successful.
     */
    private static final int SUCCESSFUL_LOGIN_SIGNAL = 2;

    /**
     * This final attribute defines the value returned from client.login() method when another player is already logged in with that nickname.
     */
    private static final int NICK_ALREADY_LOGGED_IN_SIGNAL = 3;

    /**
     * This final attribute defines the value returned from client.login() method when it fails to create a new client.
     */
    private static final int LOGIN_FAILURE_SIGNAL = 4;

    ////////////////////////////////////////////////////////////// class attributes ///////////////

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
     * This attribute represents the game map in use for this match.
     */
    private GameMap map;

//////////////////////////////////////////////////////////////class  methods ///////////////////////

    /**
     * This method clears the console's content.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    //TODO(erase this method after finishing class)
    /**
     * This method is temporarily used to quickly run this class methods.
     * @param args
     */
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
    }

    /**
     * This method asks the user which nickname the user wish to use.
     */
    private String chooseNickName() {

        String nickname = null;
        int loginOutput;
        boolean success = false;
        do {
            System.out.println("Choose a nickname: ");
            if (scannerIn.hasNextLine())
                nickname = scannerIn.nextLine();
            loginOutput = client.login(nickname);

            switch (loginOutput) {
                case SUCCESSFUL_REGISTRATION_SIGNAL : {
                    System.out.println(ANSI_GREEN + "Successful registration" + ANSI_RESET);
                    success = true;
                    break;
                }
                case NICK_NOT_AVAILABLE_SIGNAL : {
                    System.out.println(ANSI_RED + "Nickname already chosen" + ANSI_RESET);
                    break;
                }
                case SUCCESSFUL_LOGIN_SIGNAL : {
                    System.out.println("Logged in");
                    success = true;
                    break;
                }
                case NICK_ALREADY_LOGGED_IN_SIGNAL : {
                    System.out.println(ANSI_RED + "Already logged in" + ANSI_RESET);
                    break;
                }
                case LOGIN_FAILURE_SIGNAL : {
                    System.out.println(ANSI_RED + "Something went wrong" + ANSI_RESET);
                    break;
                }
                default:
                    throw new IllegalStateException("Unexpected value: " + loginOutput);
            }
        } while (!success);
        return nickname;
    }

    /**
     * This method asks the user which network technology the user wish to use choosing between RMI and Socket technologies.
     *
     * @return 1 if user choose socket technology or 0 if user choose RMI technology.
     */
    private int chooseNetworkTechnology() {

        Integer rmiOrCli = -1;
        do {
            System.out.print("Choose network technology (0 - RMI | 1 - Socket): ");
            if (scannerIn.hasNextInt()){
                rmiOrCli = scannerIn.nextInt();
            }
            scannerIn.nextLine();
        } while (rmiOrCli != 0 && rmiOrCli != 1);
        return rmiOrCli;
    }

    private void printModel() {
        printKillShotTrack();
        printMap();
        printBoards();
    }

    private void printSquare(TileSquare square, boolean isLast) {
        ArrayList<Player> players = new ArrayList<>(square.getPlayers());
        ArrayList<Color> ammos = new ArrayList<>(square.getTile().getAmmo());
        int rowCounter = 0;
        int columnCounter = 0;

        //print first line
        switch (square.getUp()) {
            case NOTHING:{

                System.out.print("+-");
                columnCounter = 2;
                while (columnCounter < SQUARES_WIDTH - 2) {
                    System.out.print(" ");
                    columnCounter++;
                }
                System.out.print("-");
                columnCounter++;
                break;
            }
            case DOOR:{

                System.out.print("+--|");
                columnCounter = 4;
                while (columnCounter < SQUARES_WIDTH - 4) {
                    System.out.print(" ");
                    columnCounter++;
                }
                System.out.print("|--");
                columnCounter++;
                break;
            }
            case WALL:{

                System.out.println("+");
                columnCounter++;
                while (columnCounter< SQUARES_WIDTH - 2) {
                    System.out.print("-");
                    columnCounter++;
                }
                break;
            }
        }
        if (isLast)
            System.out.print("+");
        rowCounter++;

        //print next lines
        while (rowCounter < SQUARES_HIGH) {

            System.out.print("| ");
            columnCounter = 2;

            //TODO finish and revision
            if (rowCounter <= 3) {  //space reserved for tile info
                if (square.getTile().getAmmo().size() + square.getTile().getPowerup() < rowCounter) {   //there are less then 3 entities to be printed
                    while (columnCounter < SQUARES_WIDTH - 1) {
                        System.out.print(" ");
                        columnCounter++;
                    }
                } else {
                    switch (ammos.get(0)) {
                        case RED:{
                            System.out.print(ANSI_RED + "RED   " + ANSI_RESET);
                            break;
                        }
                        case BLUE:{
                            System.out.print(ANSI_BLUE + "BLUE  " + ANSI_RESET);
                            break;
                        }
                        case YELLOW:{
                            System.out.print(ANSI_YELLOW + "YELLOW" + ANSI_RESET);
                        }
                    }
                    ammos.remove(0);
                    while (columnCounter < SQUARES_WIDTH - 1) {
                        System.out.print(" ");
                    }
                }
                if (isLast){
                    System.out.println("|");
                }
            } else {    //space reserved for player names

            }
            rowCounter++;
        }
    }

    private void printSquare(SpawnSquare square, boolean isLast) {
        //TODO
    }

    private void printSquare(DominationSpawnSquare square, boolean isLast) {
        //TODO
    }

    private void printMap() {

        //TODO print grid with current player positions, ammo tiles positions and weapons

        if (map == null) {
            ObjectMapper mapper = new ObjectMapper();
            InputStream gameMapsInputStream = CliMain.class.getClassLoader().getResourceAsStream(MAPS_RESOURCES_PATH);
            GameMap[] gameMaps = null;
            try {
                gameMaps = mapper.readValue(gameMapsInputStream, GameMap[].class);
            } catch (IOException e) {
                System.out.println("Cannot fetch maps from file.");
                e.printStackTrace();
            }
            map = gameMaps[model.getMapIndex()];
        }

        for (Square[] squareRow : map.getGrid()) {
            for (Square square : squareRow) {

                //understand the square type
                boolean isSpawn = false;
                for (SpawnSquare spawn : map.getSpawnSquares())
                    if (spawn.equals(square)) isSpawn = true;

                if (square.equals(squareRow[squareRow.length])) {   //last square in row

                    //print square casting it to the right type
                    if (isSpawn && model.getDomination())
                        printSquare((DominationSpawnSquare) square, true);
                    else if (isSpawn && !model.getDomination())
                        printSquare((SpawnSquare) square,true);
                    else
                        printSquare((TileSquare) square,true);

                } else {    //square is not last in row

                    //print square casting it to the right type
                    if (isSpawn && model.getDomination())
                        printSquare((DominationSpawnSquare) square, false);
                    else if (isSpawn && !model.getDomination())
                        printSquare((SpawnSquare) square,false);
                    else
                        printSquare((TileSquare) square,false);
                }
            }
        }
    }

    private void printBoards() {
        //TODO print player boards with damage, points, marks, weapons, ammos, first player marker and
    }

    private void printKillShotTrack() {
        //TODO print killShotTrack
    }

    /////////////////////////////////////////// ViewInterface implementations //////////////////////////////////////////////

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

        try {
            model = client.getModelUpdate();
            printModel();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
