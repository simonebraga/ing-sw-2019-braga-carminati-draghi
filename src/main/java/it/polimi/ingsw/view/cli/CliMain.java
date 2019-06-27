package it.polimi.ingsw.view.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.cardclasses.Weapon;
import it.polimi.ingsw.model.enumeratedclasses.Border;
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
import java.util.Arrays;
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

    /**
     * This final attribute defines the maximum number of character of a player nickname that will be printed.
     */
    private static final int NICK_PRINT_SIZE = 6;

    /**
     * This final attribute defines how many characters compose a full horizontal grid line.
     */
    private static final int TOTAL_GRID_WIDTH = (SQUARES_WIDTH * 4) - 4 + 1;

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

////////////////////////////////////////////////////////////// class  methods ////////////////////////////////////////////////

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

        chooseNickName();
    }

    /**
     * This method asks the user which nickname the user wish to use.
     */
    private void chooseNickName() {

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

    /**
     * This method printf the match to the CLI.
     */
    private void printModel() {
        printKillShotTrack();
        printMap();
        printBoards();
    }

    /**
     * This method resize each nickname to be 6 character or shorter.
     *
     * @param players an ArrayList of Players whom nickname will be trimmed.
     * @param trimSize an integer defining the character index to which nicknames will be trimmed.
     */
    private void trimNickSize(ArrayList<Player> players, int trimSize) {
        for (Player player : players) {
            player.setUsername(player.getUsername().substring(0, trimSize));
        }
    }

    /**
     * This method fetches maps from JSON and select the current match map.
     */
    private void fetchMaps(){
        ObjectMapper mapper = new ObjectMapper();
        InputStream gameMapsInputStream = CliMain.class.getClassLoader().getResourceAsStream(MAPS_RESOURCES_PATH);
        GameMap[] gameMaps = null;
        try {
            gameMaps = mapper.readValue(gameMapsInputStream, GameMap[].class);
            gameMapsInputStream.close();
        } catch (IOException e) {
            System.out.println("Cannot fetch maps from file.");
            e.printStackTrace();
        }
        map = gameMaps[model.getMapIndex()];
    }

    /**
     * This method prints the maps grid to the command line.
     */
    private void printMap() {

        if (map == null) {  //fetch maps from file
            fetchMaps();
        }

        for (int i = 0; i < map.getGrid().length; i++) {    //for each row
            if (Arrays.equals(map.getGrid()[i], map.getGrid()[map.getGrid().length]))     //if the i row on the map is the last row
                printRow(map.getGrid()[i],true, map.getSpawnSquares());
            else
                printRow(map.getGrid()[i], false, map.getSpawnSquares());
        }
    }

    /**
     * This method memorizes the borders of all squares in a row, from left to right.
     *
     * @param squares an array of Square to be scanned left to right.
     * @param leftOrUp a String flag representing which border to memorize (upper border or left border of the square).
     * @return an ArrayList of Border that contains all squares border (up or left border) from left to right.
     */
    private ArrayList<Border> retrieveBordersInfo(Square[] squares, String leftOrUp) {

        ArrayList<Border> borders = new ArrayList<>();

        if (leftOrUp.equals("up")) {
            for (Square square : squares) {

                if (square.getUp() != null) {
                    borders.add(square.getUp());

                } else {    //void square
                    if (square.getY() != 0)         //square is not part of the first row
                        borders.add(Border.WALL);
                    else
                        borders.add(Border.NOTHING);
                }
            }
        } else if (leftOrUp.equals("left")) {
            for (Square square : squares) {

                if (square.getUp() != null) {
                    borders.add(square.getLeft());

                } else {    //void square

                    if (square.getX() != 0)         //not the first square in the row
                        borders.add(Border.WALL);
                    else
                        borders.add(Border.NOTHING);
                }
            }
        }
        return borders;
    }

    /**
     * This method checks what kind of square a row of the map grid finishes with and associates a closing border to the row.
     *
     * @param squares an array of Square to be scanned.
     * @return a Border associated with the closing border of this map grid row.
     */
    private Border retrieveLastBorderInfo(Square[] squares) {

        if (squares[squares.length - 1].getUp() == null)        //last square is void
            return Border.NOTHING;
        else
            return Border.WALL;
    }

    /**
     * This method collects all players user names inside a map grid row in a list.
     *
     * @param squares an array of Square to be scanned.
     * @return an ArrayList of ArrayList of String that contains all user names by each square.
     */
    private ArrayList<ArrayList<String>> getUsernamesInsideRow(Square[] squares){

        ArrayList<ArrayList<String>> playersBySquareInRow = new ArrayList<>();
        for (Square square : squares) {
            ArrayList<String> playersInSquare = new ArrayList<>();
            for (Player player : square.getPlayers()) {
                playersInSquare.add(player.getUsername());
            }
            playersBySquareInRow.add(playersInSquare);
        }
        return playersBySquareInRow;
    }

    /**
     * This method parse info from a map grid row to strings and collects them into a list.
     *
     * @param squares an array of Square to be scanned.
     * @param spawnSquares an ArrayList of SpawnSquare that represent all squares that are not TileSquare or void squares.
     * @param squareContentInfo the list in which data will be collected.
     * @param squareTypes a list of String representing the type of each square by its position in the row.
     */
    private void getContentOfEachSquare(Square[] squares, ArrayList<SpawnSquare> spawnSquares, ArrayList<ArrayList<String>> squareContentInfo, ArrayList<String> squareTypes) {

        for (Square square : squares) {
            if (spawnSquares.contains(square)) {    //square is a spawnSquare

                SpawnSquare spawnSquare = (SpawnSquare) square;

                ArrayList<String> infoArray = new ArrayList<>();
                infoArray.add(spawnSquare.getColor().name());

                for (Weapon weapon : spawnSquare.getWeapons()) {
                    infoArray.add(weapon.getName().name());
                    infoArray.add(weapon.getIsLoaded().toString());
                }

                squareContentInfo.add(infoArray);
                squareTypes.add("spawn");
            } else {

                if (square.getUp() == null) {   //void square

                    ArrayList<String> infoArray = new ArrayList<>();
                    infoArray.add("null");
                    squareContentInfo.add(infoArray);
                    squareTypes.add("void");

                } else {    //tile square

                    TileSquare tileSquare = (TileSquare) square;

                    ArrayList<String> infoArray = new ArrayList<>();
                    infoArray.add(tileSquare.getTile().getPowerup().toString());

                    for (Color color : tileSquare.getTile().getAmmo()) {
                        infoArray.add(color.name());
                    }

                    squareContentInfo.add(infoArray);
                    squareTypes.add("tile");
                }
            }
        }
    }

    /**
     * This method prints the first characters line of a map grid row to the command line.
     *
     * @param upperBorders a list of Border containing the upper border of each square by the position of such square in the row.
     * @param squareTypes a list of String representing the type of each square by its position in the row.
     * @param squareContentInfo a list in which strings about each square content are stored.
     */
    private void printFirstLine(ArrayList<Border> upperBorders, ArrayList<String> squareTypes, ArrayList<ArrayList<String>> squareContentInfo) {

        for (int j = 0; j < upperBorders.size(); j++) {

            if (squareTypes.get(j).equals("spawn")){
                switch (squareContentInfo.get(j).get(0)){
                    case "RED":{
                        System.out.print(ANSI_RED);
                        break;
                    }
                    case "BLUE":{
                        System.out.print(ANSI_BLUE);
                        break;
                    }
                    case "YELLOW":{
                        System.out.print(ANSI_YELLOW);
                        break;
                    }
                }
            }

            switch (upperBorders.get(j)) {
                case DOOR:{
                    System.out.print("+--|");
                    for (int k = 0; k < SQUARES_WIDTH - 8 ; k++) {
                        System.out.print(" ");
                    }
                    System.out.print("|--");
                    break;
                }
                case NOTHING:{
                    System.out.print("+-");
                    for (int i = 0; i < SQUARES_WIDTH - 4; i++) {
                        System.out.print(" ");
                    }
                    System.out.print("-");
                    break;
                }
                case WALL:{
                    System.out.print("+");
                    for (int k = 0; k < SQUARES_WIDTH - 2; k++) {
                        System.out.print("-");
                    }
                }
            }
            System.out.print(ANSI_RESET);
        }
        System.out.println("+");
    }

    /**
     * This method prints a grid map row to the command line.
     *
     * @param squares an array of Square to be formatted and printed.
     * @param isLastRow a boolean flag that says if the row to be printed is the last row in the map grid.
     * @param spawnSquares a list of SpawnSquare that contains all squares that are not TileSquare nor void squares.
     */
    private void printRow(Square[] squares, boolean isLastRow, ArrayList<SpawnSquare> spawnSquares) {

        ArrayList<Border> upperBorders = retrieveBordersInfo(squares,"up");
        ArrayList<Border> leftBorders = retrieveBordersInfo(squares,"left");
        Border rightMostBorder = retrieveLastBorderInfo(squares);
        ArrayList<ArrayList<String>> nicknamesPlayersInsideSquares = getUsernamesInsideRow(squares);
        ArrayList<ArrayList<String>> squareContentInfo = new ArrayList<>();
        ArrayList<String> squareTypes = new ArrayList<>();

        getContentOfEachSquare(squares,spawnSquares,squareContentInfo,squareTypes);

        printFirstLine(upperBorders,squareTypes,squareContentInfo);

        //all square content info lines
        for (int i = 0; i < 4; i++) {

            printContentLine(squares, spawnSquares);

            //close last column
            if (rightMostBorder.equals(Border.WALL)) {
                System.out.println("|");
            } else {    //rightMostBorder == Border.NOTHING
                System.out.println(" ");
            }
        }

        //all players in square nicknames
        for (int i = 0; i < 6; i++) {

            printPlayerLine(squares, spawnSquares);

            //close last column
            if (rightMostBorder.equals(Border.WALL)) {
                System.out.println("|");
            } else {    //rightMostBorder == Border.NOTHING
                System.out.println(" ");
            }
        }

        //all remaining lines
    }

    private void printPlayerLine(Square[] squares, ArrayList<SpawnSquare> spawnSquares) {
        //TODO

    }

    private void printContentLine(Square[] squares, ArrayList<SpawnSquare> spawnSquares) {
        //TODO
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
