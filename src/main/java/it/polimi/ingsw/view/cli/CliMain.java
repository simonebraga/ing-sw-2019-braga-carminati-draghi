package it.polimi.ingsw.view.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Border;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.gamelogic.settings.SettingsJSONParser;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.smartmodel.*;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.ViewInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
     * This finale attribute represents the UNICODE code for the "no ammo" symbol.
     */
    private static final String UNICODE_NO_AMMO = "\uD83D\uDEC7";

    /**
     * This finale attribute represents the UNICODE code for a skull symbol.
     */
    private static final String UNICODE_SKULL = "\u2620";

    /**
     * This final attribute defines squares width in cli printing.
     */
    private static final int SQUARES_WIDTH = 22;

    /**
     * This final attribute defines squares high in cli printing.
     */
    private static final int SQUARES_HIGH = 11;

    /**
     * This final attribute defines the maximum number of character of a player nickname that will be printed.
     */
    private static final int NICK_PRINT_SIZE = 20;

    /**
     * This final attribute defines the maximum number of squares per row.
     */
    private static final int MAX_SQUARES_BY_ROW = 4;

    /**
     * This final attribute defines the maximum number of squares per column.
     */
    private static final int MAX_SQUARES_BY_COLUMN = 3;

    /**
     * This final attribute defines how many characters compose a full horizontal grid line.
     */
    private static final int TOTAL_GRID_WIDTH = (SQUARES_WIDTH * MAX_SQUARES_BY_ROW) - MAX_SQUARES_BY_ROW + 1;

    /**
     * This final attribute defines the first line index of the content square section.
     */
    private static final int SQUARE_CONTENT_SECTION_STARTING_INDEX = 2;

    /**
     * This final attribute defines the first line index of the figure square section.
     */
    private static final int SQUARE_FIGURE_SECTION_STARTING_INDEX = 5;

    /**
     * This final attribute defines the maximum amount of character a generic info would be printed.
     */
    private static final int MAX_INFO_SIZE = 6;

    /**
     * This final attribute defines the invalid input message to send to the user.
     */
    private static final String INVALID_INPUT_MESSAGE = "Invalid input";

    /**
     * This final attribute defines the null info field.
     */
    private static final String VOID_INFO_SPACING = "      ";

    /**
     * This finale attribute represents the character for a the door threshold.
     */
    private static final String UPPER_DOOR_THRESHOLD_CHAR = "#";


    ////////////////////////////////////////////// match related constants /////////////////////////////////////////////

    /**
     * This final attribute defines the maximum amount of weapons stored in a single spawn square.
     */
    private static final int MAX_WEAPONS_BY_SQUARE = 3;

    /**
     * This final attribute represents the number of maps available in the game.
     */
    private static final int GAME_MAPS_NUMBER = 4;

    /**
     * This final attribute represents the JSON file path containing all game maps.
     */
    private static final String CLIENT_MAPS_RESOURCES_PATH = "client_maps.json";

    /**
     * This final attribute defines the maximum amount of player by square.
     */
    private static final int MAX_PLAYER_BY_SQUARE = 5;

    ////////////////////////////////////// network related constants ///////////////////////////////////////

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

    ////////////////////////////////////////////// class attributes /////////////////////////////////////////////////////

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
    private SmartMap map;

    /**
     * This attribute contains the game settings loaded from file.
     */
    private SettingsJSONParser settings;

    /**
     * This final attribute defines the 'game_settings.json' file path.
     */
    private static final String GAME_SETTINGS_PATH = "game_settings.json";

////////////////////////////////////////////////////////////// class  methods ////////////////////////////////////////////////

    /**
     * This method clears the console's content.
     */
    public synchronized static void clearScreen() throws IOException, InterruptedException{
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    //TODO(erase this method after finishing class)
    /**
     * This method is temporarily used to quickly run this class methods.
     */
    public synchronized static void main(String[] args) {
        CliMain temp = new CliMain();
        temp.launch(args);
    }

    /**
     * This method implements the application using CLI.
     */
    public synchronized void launch(String[] args) {

        //fetch default network properties
        Properties properties = new Properties();
        try {
            properties.load(Objects.requireNonNull(Client.class.getClassLoader().getResourceAsStream("network_settings.properties")));
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Cannot access network settings file." + ANSI_RESET);
            System.exit(0);
        }

        if (settings == null)
            fetchSettings();

        System.out.println("Welcome to Adrenaline!\n");

        //create client
        try {
            if (args[0] != null && args[1] != null)
                client = new Client(chooseNetworkTechnology(),this, args[0], args[1]);
            else if (args[0] != null && args[1] == null)
                client = new Client(chooseNetworkTechnology(),this, args[0], properties.getProperty("clientIp"));
            else if (args[0] == null && args[1] != null)
                client = new Client(chooseNetworkTechnology(),this, properties.getProperty("serverIp"), args[1]);
            else
                client = new Client(chooseNetworkTechnology(),this);

            System.out.println("Client created");
        } catch (Exception e) {
            System.out.println(ANSI_RED + "Server not responding.\n" + ANSI_RESET);
            System.exit(0);
        }
        chooseNickName();
    }

    /**
     * This method fetches game settings from a JSON file.
     */
    private void fetchSettings() {

        //load game settings from "game_settings.json" file
        InputStream settingsFile = CliMain.class.getClassLoader().getResourceAsStream(GAME_SETTINGS_PATH);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            settings = objectMapper.readValue(settingsFile, SettingsJSONParser.class);
            settingsFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method asks the user which nickname the user wish to use.
     */
    private void chooseNickName() {

        int loginOutput;
        boolean success = false;
        do {
            System.out.print("Choose a nickname: ");
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
            try {
                clearScreen();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (rmiOrCli != 0 && rmiOrCli != 1);
        return rmiOrCli;
    }

    /**
     * This method printf the match to the CLI.
     */
    private synchronized void printModel() {

        ArrayList<String> nicknames = new ArrayList<>(model.getSmartPlayerMap().keySet());

        if (map == null)
            fetchMaps();

        printKillShotTrack();
        printSceneSpacing();
        System.out.println("MAP:\n");
        printMap();
        printSceneSpacing();

        System.out.println("PLAYER BOARDS:\n");
        for (String nick : nicknames) {
            if (nick.equals(nickname))
                printBoard(nick, model.getSmartPlayerMap().get(nick), true);
            else
                printBoard(nick, model.getSmartPlayerMap().get(nick), false);
        }
    }

    /**
     * This methods prints a separation space with a bar in the command line.
     */
    private synchronized void printSceneSpacing() {

        System.out.println();
        for (int i = 0; i < 2 * TOTAL_GRID_WIDTH; i++) {
            System.out.print("-");
        }
        System.out.println();
        System.out.println();
    }

    /**
     * This method fetches maps from JSON and select the current match map.
     */
    private void fetchMaps() {
        ObjectMapper mapper = new ObjectMapper();
        InputStream gameMapsInputStream = CliMain.class.getClassLoader().getResourceAsStream(CLIENT_MAPS_RESOURCES_PATH);
        SmartMap[] gameMaps = null;
        try {
            gameMaps = mapper.readValue(gameMapsInputStream, SmartMap[].class);
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
    private synchronized void printMap() {

        for (int i = 0; i < MAX_SQUARES_BY_COLUMN; i++) {

            //retrieve players on this row
            ArrayList<SmartPlayer> players = new ArrayList<>();
            for (SmartPlayer player : model.getSmartPlayerMap().values()) {
                if (player.getPosY() == i)
                    players.add(player);
            }

            //retrieve tiles on this row
            ArrayList<SmartTile> tiles = new ArrayList<>();
            for (SmartTile tile : model.getMapTiles()) {
                if (tile.getPosX() == i)
                    tiles.add(tile);
            }

            Color spawnColor = map.getSpawnColors()[i];

            ArrayList<WeaponName> weapons = new ArrayList<>();
            for (WeaponName weapon : model.getSpawnWeaponMap().get(spawnColor))
                weapons.add(weapon);


            printRow(map.getUpperBorders()[i], map.getLeftBorders()[i], map.getRightMostBorders()[i],
                        map.getSquareTypes()[i], spawnColor, players, tiles, weapons);
        }


        //close last line
        ArrayList<Border> closingBorders = new ArrayList<>();
        for (int i = 0; i < MAX_SQUARES_BY_ROW; i++) {
            if (map.getSquareTypes()[map.getSquareTypes().length - 1][i].equals("void"))
                closingBorders.add(Border.NOTHING);
            else
                closingBorders.add(Border.WALL);
        }

        printFirstLine(closingBorders);
    }


    /**
     * This method prints a grid map row to the command line.
     *
     * @param upperBordersArray an array of Border with all row upper borders from left to right.
     * @param leftBordersArray an array of Border with all row left borders from left to right.
     * @param rightMostBorder a Border that indicates the row last square right border.
     * @param squaresTypeArray an array on String containing all square types in this row left to right.
     * @param spawnColor a Color that indicates the row spawn square color.
     * @param players an ArrayList of SmartPlayer containing all players on this row
     * @param tiles an ArrayList of SmartTile containing all tiles on this row.
     * @param weapons an ArrayList of WeaponName containing all weapons name of weapons of this row.
     */
    private synchronized void printRow(Border[] upperBordersArray, Border[] leftBordersArray, Border rightMostBorder, String[] squaresTypeArray,
                          Color spawnColor, ArrayList<SmartPlayer> players, ArrayList<SmartTile> tiles, ArrayList<WeaponName> weapons) {

        ArrayList<Border> upperBorders = new ArrayList<>(Arrays.asList(upperBordersArray));
        ArrayList<Border> leftBorders = new ArrayList<>(Arrays.asList(leftBordersArray));
        ArrayList<ArrayList<String>> figuresInsideSquares = getFiguresInsideRow(players);
        ArrayList<String> squareTypes = new ArrayList<>(Arrays.asList(squaresTypeArray));
        ArrayList<ArrayList<String>> squareContentInfo = getContentOfEachSquare(squareTypes, tiles, weapons);

        printFirstLine(upperBorders);
        printSpawnTagsLine(spawnColor, leftBorders, rightMostBorder, squareTypes, 1);

        //all square content info lines
        int k = 0;
        for (int i = SQUARE_CONTENT_SECTION_STARTING_INDEX; i < SQUARE_FIGURE_SECTION_STARTING_INDEX; i++) {

            ArrayList<String> contentInThisLine = new ArrayList<>();
            for (ArrayList<String> contents : squareContentInfo) {
                if (!contents.isEmpty())
                    contentInThisLine.add(contents.get(k));
            }
            printContentLine(leftBorders, contentInThisLine, i, rightMostBorder);
            k++;
        }

        k=0;
        //all players in square nicknames
        for (int i = SQUARE_FIGURE_SECTION_STARTING_INDEX; i < SQUARES_HIGH - 1; i++) {

            ArrayList<String> figuresInThisLine = new ArrayList<>();
            for (ArrayList<String> figures : figuresInsideSquares) {
                if (!figures.isEmpty())
                    figuresInThisLine.add(figures.get(k));
            }

            printFigureLine(leftBorders, figuresInThisLine, i, rightMostBorder);
            k++;
        }
    }

    /**
     * This method prints spaces from a row index to another. Indexes are included in the count.
     *
     * @param from integer index from where to start.
     * @param to integer index to where to stop.
     */
    private synchronized void printSpacesFromIndexToIndex(int from, int to) {

        for (int j = from; j <= to; j++) {
            System.out.print(" ");
        }
    }

    /**
     * This method prints the first characters line of a map grid row to the command line.
     *
     * @param upperBorders a list of Border containing the upper border of each square by the position of such square in the row.
     */
    private synchronized void printFirstLine(ArrayList<Border> upperBorders) {

        for (Border upperBorder : upperBorders) {

            switch (upperBorder) {
                case DOOR: {
                    System.out.print("+--#");
                    printSpacesFromIndexToIndex(4, SQUARES_WIDTH - 5);
                    System.out.print("#--");
                    break;
                }
                case NOTHING: {
                    System.out.print("+ ");
                    printSpacesFromIndexToIndex(2, SQUARES_WIDTH - 3);
                    System.out.print(" ");
                    break;
                }
                case WALL: {
                    System.out.print("+");
                    for (int k = 0; k < SQUARES_WIDTH - 2; k++) {
                        System.out.print("-");
                    }
                }
            }
        }
        System.out.println("+");
    }

    /**
     * This method prints the border character of a square box depending on the type of the left square's border.
     *
     * @param border a Border of a square.
     * @param rowIndex an integer which is the current printing row index relative to a square box high (0 is the first box line index).
     */
    private synchronized void printBorderChar(Border border, int rowIndex) {

        switch (border) {
            case DOOR:{
                if (rowIndex >= 3 && rowIndex <= SQUARES_HIGH - 4)
                    System.out.print(" ");
                else if (rowIndex == 1 || rowIndex == SQUARES_HIGH - 2)
                    System.out.print("|");
                else if (rowIndex == 2)
                    System.out.print(UPPER_DOOR_THRESHOLD_CHAR);
                else
                    System.out.print("#");
                break;
            }
            case WALL:{
                System.out.print("|");
                break;
            }
            case NOTHING:{
                if (rowIndex == 1)
                    System.out.print(" ");
                else if (rowIndex == SQUARES_HIGH - 2)
                    System.out.print(" ");
                else
                    System.out.print(" ");
                break;
            }
        }
    }

    /**
     * This method prints a full line that displays the color tag for each spawn square and spaces for each other square in the grid row.
     *
     * @param spawnColor a Color enum that defines of what color this row's spawn square is.
     * @param leftBorders an ArrayList of Border containing all squares left border from left to right square.
     * @param rightMostBorder a Border which is the last border on the grid row.
     * @param squareTypes an ArrayList of String containing all square types from left to right.
     * @param rowIndex the current printing line index.
     */
    private synchronized void printSpawnTagsLine(Color spawnColor, ArrayList<Border> leftBorders, Border rightMostBorder, ArrayList<String> squareTypes, int rowIndex) {

        //search for spawn square in this row
        int spawnSquareIndex = -1;
        for (int i = 0; i < MAX_SQUARES_BY_ROW; i++) {
            if (squareTypes.get(i).equals("spawn"))
                spawnSquareIndex = i;
        }

        for (int i = 0; i < MAX_SQUARES_BY_ROW; i++) {

            printBorderChar(leftBorders.get(i), rowIndex);
            System.out.print(" ");

            if (i == spawnSquareIndex) {

                //print color tag
                switch (spawnColor) {
                    case RED:{
                        System.out.print("SPAWN:RED   ");
                        break;
                    }
                    case BLUE:{
                        System.out.print("SPAWN:BLUE  ");
                        break;
                    }
                    case YELLOW:{
                        System.out.print("SPAWN:YELLOW");
                        break;
                    }
                }

                //fill to the next border
                printSpacesFromIndexToIndex(14, SQUARES_WIDTH - 2);
            } else {

                //void line to the next border
                printSpacesFromIndexToIndex(2, SQUARES_WIDTH - 2);
            }
        }
        //last border closing
        printBorderChar(rightMostBorder, rowIndex);
        System.out.println();
    }

    /**
     * This method collects all grid row players figures by square.
     *
     * @param players an ArrayList of SmartPlayers to be scanned.
     * @return an ArrayList of ArrayList of Figure that contains all players figure by each square.
     */
    private synchronized ArrayList<ArrayList<String>> getFiguresInsideRow(ArrayList<SmartPlayer> players) {

        ArrayList<ArrayList<String>> figuresBySquareInRow = new ArrayList<>();

        for (int i = 0; i < MAX_SQUARES_BY_ROW; i++) {  //for each square in this row

            ArrayList<String> figuresInSquare = new ArrayList<>();

            for (int j = 0; j < players.size(); j++) {    //scan each player
                if (players.get(j).getPosX() == i) {    //checks if this player is on same row square
                    figuresInSquare.add(parseFigure(players.get(j).getFigure()));
                }
            }

            int numberOfFiguresUntilNow = figuresInSquare.size();
            //fill the rest with null figures
            for (int j = 0; j < MAX_PLAYER_BY_SQUARE - numberOfFiguresUntilNow; j++) {
                figuresInSquare.add(VOID_INFO_SPACING);
            }

            figuresBySquareInRow.add(figuresInSquare);
        }

        return figuresBySquareInRow;
    }

    /**
     * This method parse info from a map grid row to strings and collects them into a list.
     *
     * @param squareTypes a list of String representing the type of each square by its position in the row.
     * @param tiles an ArrayList of SmartTile containing all row tiles.
     * @param weaponNames an ArrayList of WeaponName containing names of all weapon name in this row.
     */
    private synchronized ArrayList<ArrayList<String>> getContentOfEachSquare(ArrayList<String> squareTypes, ArrayList<SmartTile> tiles, ArrayList<WeaponName> weaponNames) {

        ArrayList<ArrayList<String>> squaresContentInfo = new ArrayList<>();

        //for each square in this row
        for (int i = 0; i < MAX_SQUARES_BY_ROW; i++) {

            ArrayList<String> singleSquareInfo = new ArrayList<>();

            if (squareTypes.get(i).equals("spawn")) {   //spawn square

                for (WeaponName name : weaponNames)
                    singleSquareInfo.add(parseWeaponName(name, true));

                int numberOfInfoUntilNow = singleSquareInfo.size();
                for (int j = 0; j < MAX_WEAPONS_BY_SQUARE - numberOfInfoUntilNow; j++) {
                    singleSquareInfo.add(VOID_INFO_SPACING);   //no weapon
                }

                squaresContentInfo.add(singleSquareInfo);

            } else if (squareTypes.get(i).equals("tile")) {     //tile square

                //scan every tile in this row
                for (SmartTile tile : tiles) {
                    singleSquareInfo = new ArrayList<>();
                    if (tile.getPosX() == i) {  //same column
                        if (tile.getPowerup() == 1)
                            singleSquareInfo.add(tile.getPowerup() + "PU   ");

                        for (Color color : tile.getAmmo())
                            singleSquareInfo.add(parseColorName(color));

                        squaresContentInfo.add(singleSquareInfo);
                    }
                }

            } else {            //void square

                for (int j = SQUARE_CONTENT_SECTION_STARTING_INDEX; j < SQUARE_FIGURE_SECTION_STARTING_INDEX; j++)
                    singleSquareInfo.add(VOID_INFO_SPACING);

                squaresContentInfo.add(singleSquareInfo);
            }
        }
        return squaresContentInfo;
    }

    /**
     * This method parse a weapon name into a shorten version of the same weapon name.
     *
     * @param name a WeaponName to be parsed to a 5 characters string.
     * @param isLoaded a boolean flag saying if the weapon is loaded.
     * @return a MAX_INFO_SIZE - 1 characters long String which represents the shorten version of a weapon name.
     */
    private String parseWeaponName(WeaponName name, boolean isLoaded) {
        switch (name) {
            case FURNACE:{
                if (isLoaded)
                    return "FURNC ";
                else
                    return "FURNC" + UNICODE_NO_AMMO;
            }
            case CYBERBLADE:{
                if (isLoaded)
                    return "CYBLD ";
                else
                    return "CYBLD" + UNICODE_NO_AMMO;
            }
            case ELECTROSCYTHE:{
                if (isLoaded)
                    return "ELECT ";
                else
                    return "ELECT" + UNICODE_NO_AMMO;
            }
            case SHOCKWAVE:{
                if (isLoaded)
                    return "SHOCK ";
                else
                    return "SHOCK" + UNICODE_NO_AMMO;
            }
            case ZX2:{
                if (isLoaded)
                    return "ZX2   ";
                else
                    return "ZX2  " + UNICODE_NO_AMMO;
            }
            case THOR:{
                if (isLoaded)
                    return "THOR  ";
                else
                    return "THOR " + UNICODE_NO_AMMO;
            }
            case SHOTGUN:{
                if (isLoaded)
                    return "SHOTG ";
                else
                    return "SHOTG" + UNICODE_NO_AMMO;
            }
            case WHISPER:{
                if (isLoaded)
                    return "WHISP ";
                else
                    return "WHISP" + UNICODE_NO_AMMO;
            }
            case HEATSEEKER:{
                if (isLoaded)
                    return "HTSKR ";
                else
                    return "HTSKR" + UNICODE_NO_AMMO;
            }
            case MACHINEGUN:{
                if (isLoaded)
                    return "MACHG ";
                else
                    return "MACHG" + UNICODE_NO_AMMO;
            }
            case TRACTORBEAM:{
                if (isLoaded)
                    return "TRTBM ";
                else
                    return "TRTBM" + UNICODE_NO_AMMO;
            }
            case VORTEXCANNON:{
                if (isLoaded)
                    return "VORTX ";
                else
                    return "VORTX" + UNICODE_NO_AMMO;
            }
            case POWERGLOVE:{
                if (isLoaded)
                    return "POWGV ";
                else
                    return "POWGV" + UNICODE_NO_AMMO;
            }
            case SLEDGEHAMMER:{
                if (isLoaded)
                    return "SLGHM ";
                else
                    return "SLGHM" + UNICODE_NO_AMMO;
            }
            case ROCKETLAUNCHER:{
                if (isLoaded)
                    return "RKTLC ";
                else
                    return "RKTLC" + UNICODE_NO_AMMO;
            }
            case HELLION:{
                if (isLoaded)
                    return "HELLN ";
                else
                    return "HELLN" + UNICODE_NO_AMMO;
            }
            case RAILGUN:{
                if (isLoaded)
                    return "RAILG ";
                else
                    return "RAILG" + UNICODE_NO_AMMO;
            }
            case LOCKRIFLE:{
                if (isLoaded)
                    return "LOCKR ";
                else
                    return "LOCKR" + UNICODE_NO_AMMO;
            }
            case PLASMAGUN:{
                if (isLoaded)
                    return "PLASM ";
                else
                    return "PLASM" + UNICODE_NO_AMMO;
            }
            case FLAMETHROWER:{
                if (isLoaded)
                    return "FLMTW ";
                else
                    return "FLMTW" + UNICODE_NO_AMMO;
            }
            case GRENADELAUNCHER:{
                if (isLoaded)
                    return "GNDLC ";
                else
                    return "GNDLC" + UNICODE_NO_AMMO;
            }
        }
        return null;
    }

    /**
     * This method parse a Color into a shorten version of the name of that color.
     *
     * @param color a Color to be parsed to a 5 characters string.
     * @return a MAX_INFO_SIZE characters long String which represents the shorten version of a color name.
     */
    private String parseColorName(Color color) {
        switch (color) {
            case BLUE: return "BLUE  ";
            case YELLOW: return "YELLOW";
            case RED: return "RED   ";
        }
        return null;
    }

    /**
     * This method prints a square box line with a figure name inside it.
     *
     * @param figure a Figure which name will be formatted and printed.
     */
    private synchronized String parseFigure(Figure figure) {
        switch (figure) {
            case DESTRUCTOR:{
                return ":D-STR";
            }
            case DOZER:{
                return "DOZER ";
            }
            case BANSHEE:{
                return "BANSHE";
            }
            case VIOLET:{
                return "VIOLET";
            }
            case SPROG:{
                return "SPROG ";
            }
        }
        return VOID_INFO_SPACING;
    }

    /**
     * This method prints a full line of player line type considering row index, borders and figures inside each square.
     *
     * @param leftBorders an ArrayList of Border containing each square left border from left square to right square.
     * @param figures an ArrayList of Figure containing a figure for each square box.
     * @param rowIndex an integer representing at which command line row idex is the method printing, relative to the square box high.
     * @param rightMostBorder a Border which is the last square's right border.
     */
    private synchronized void printFigureLine(ArrayList<Border> leftBorders, ArrayList<String> figures, int rowIndex, Border rightMostBorder) {

        for (int i = 0; i < figures.size() && i < leftBorders.size(); i++) {
            printBorderChar(leftBorders.get(i), rowIndex);
            System.out.print(" ");
            System.out.print(figures.get(i));
            printSpacesFromIndexToIndex(8, SQUARES_WIDTH - 2);
        }

        //print void if there are not enough info
        for (int i = figures.size(); i < leftBorders.size(); i++) {
            printBorderChar(leftBorders.get(i), rowIndex);
            System.out.print(" ");
            System.out.print(VOID_INFO_SPACING);
            printSpacesFromIndexToIndex(8, SQUARES_WIDTH - 2);
        }

        //closing line
        printBorderChar(rightMostBorder, rowIndex);
        System.out.println();
    }

    /**
     * This method prints a full line of content info about each square of a particular row.
     *
     * @param leftBorders an ArrayList of Border containing each square left border from left square to right square.
     * @param contentBySquare an ArrayList of String containing each square info to be printed.
     * @param rowIndex an integer representing at which command line row idex is the method printing, relative to the square box high.
     * @param rightMostBorder a Border representing the right border of the last square in the row.
     */
    private synchronized void printContentLine(ArrayList<Border> leftBorders, ArrayList<String> contentBySquare, int rowIndex, Border rightMostBorder) {

        for (int i = 0; i < contentBySquare.size() && i < leftBorders.size(); i++) {
            printBorderChar(leftBorders.get(i), rowIndex);
            System.out.print(" ");
            System.out.print(contentBySquare.get(i));
            printSpacesFromIndexToIndex(8, SQUARES_WIDTH - 2);  //fill with spaces to the next border
        }

        //print void if there are not enough info
        for (int i = contentBySquare.size(); i < leftBorders.size(); i++) {
            printBorderChar(leftBorders.get(i), rowIndex);
            System.out.print(" ");
            System.out.print(VOID_INFO_SPACING);
            printSpacesFromIndexToIndex(8, SQUARES_WIDTH - 2);
        }

        //closing line
        printBorderChar(rightMostBorder, rowIndex);
        System.out.println();
    }

    /**
     * This method prints each player board.
     *
     * @param nickname a String which is the users nickname.
     * @param player as SmartPlayer whom board will be printed.
     * @param isOwnBoard a boolean flag that says if the board printed is the current client player's board.
     */
    private synchronized void printBoard(String nickname, SmartPlayer player, boolean isOwnBoard) {

        printBoardHeader(nickname, isOwnBoard, player.getPoints(), player.getFigure());
        printAdrenalineLevel(player.getDamage().size());
        System.out.println("| Marks:");
        printMarkTrack(player.getMarks());
        System.out.print("| Damage: ");
        printDamageTrack(player.getDamage());
        System.out.println("| Ammunition:");
        printPlayerAmmo(player.getAmmo());
        System.out.print("| Bounties: ");
        printBountyTrack(player.getDeaths(), settings.getMaxKills());
        System.out.println("| Weapons:");
        printPlayerWeapons(player.getWeapons());
        if (isOwnBoard)
            printPlayerPowerups(player.getPowerups());
        System.out.println("+\n");
    }

    /**
     * This method prints a player's board header with nick and points.
     *
     * @param nickname a String which is the username of the printing board. It will be printed inside the header.
     * @param isOwnBoard a boolean flag that says if this board is the current player own board.
     * @param points an integer containing the player's points.
     */
    private synchronized void printBoardHeader(String nickname, boolean isOwnBoard, int points, Figure figure) {
        if (isOwnBoard) System.out.print(ANSI_GREEN);
        System.out.print("+ ");
        if (nickname.length() > NICK_PRINT_SIZE)
            System.out.print(nickname.substring(0, NICK_PRINT_SIZE - 1));
        else
            System.out.print(nickname);
        System.out.print("                    Figure: ");
        System.out.print(parseFigure(figure));
        //fill until next border
        printSpacesFromIndexToIndex(8, SQUARES_WIDTH - 1);
        System.out.println("      Points: " + points);
        System.out.print(ANSI_RESET);
    }

    /**
     * This method prints the player adrenaline level considering how many times this player died.
     *
     * @param damage an integer containing how many times this player was hit.
     */
    private synchronized void printAdrenalineLevel(int damage) {
        if (damage < 3) System.out.println("| Adrenaline level: " + UNICODE_NO_AMMO);
        else if (damage <= 5) System.out.println("| Adrenaline level: I");
        else System.out.println("| Adrenaline level: " + ANSI_RED + "MAX" + ANSI_RESET);
    }

    /**
     * This method prints the player marks.
     *
     * @param marks a Map of Figure and Integer to be printed.
     */
    private synchronized void printMarkTrack(Map<Figure, Integer> marks) {

        for (Figure f : marks.keySet()) {
            System.out.print("| ");
            System.out.print(parseFigure(f));
            //fill until next border
            printSpacesFromIndexToIndex(8, SQUARES_WIDTH - 1);
            System.out.println(" : " + marks.get(f));
        }
        if (marks.keySet().isEmpty())
            System.out.println("| No marks");
    }

    /**
     * This method prints the player damage.
     *
     * @param damage an ArrayList of Figure containing all players that damaged this player in the correct order.
     */
    private synchronized void printDamageTrack(ArrayList<Figure> damage) {
        System.out.print(" | ");

        for (Figure f : damage) {
            System.out.print(parseFigure(f));
            //fill until next border
            printSpacesFromIndexToIndex(8, SQUARES_WIDTH - 1);
            System.out.print(" | ");
        }

        for (int i = 0; i < 12 - damage.size(); i++) {
            System.out.print(VOID_INFO_SPACING + " | ");
        }
        System.out.println();
    }

    /**
     * This method prints all players ammo.
     *
     * @param ammo a Map of Color and Integer containing each ammo color amount.
     */
    private synchronized void printPlayerAmmo(Map<Color, Integer> ammo) {
        for (Color color : ammo.keySet()) {
            System.out.print("| ");
            System.out.println(color.name() + " : " + ammo.get(color));
        }
    }

    /**
     * This method prints all bounties remained on this player.
     *
     * @param deaths an integer saying how many times this player died.
     * @param maxKills an integer which represents the total bounty track size.
     */
    private synchronized void printBountyTrack(int deaths, int maxKills) {

        ArrayList<Integer> bountyValues = new ArrayList<>(Arrays.asList(settings.getBounties()));

        //print skulls
        int i = 0;
        while ( i < deaths && i < bountyValues.size()) {
            System.out.print("[" + ANSI_RED + UNICODE_SKULL + ANSI_RESET + "] ");
            bountyValues.get(i);
            i++;
        }


        //print remaining values
        for (int k = i; k < bountyValues.size(); k++) {
            if (bountyValues.get(k) != 1)
                System.out.print("[" + bountyValues.get(k) + "] ");
            else {
                for (int j = k; j < maxKills; j++) {

                }
            }
        }

        System.out.println();
    }

    /**
     * This method prints all weapons that a player owns.
     *
     * @param weapons an ArrayList of SmartWeapon that contains all player weapons.
     */
    private synchronized void printPlayerWeapons(ArrayList<SmartWeapon> weapons) {
        if (weapons.isEmpty())
            System.out.println("| Unarmed");
        for (SmartWeapon weapon : weapons) {
            System.out.print("| ");
            if (weapon.getLoaded())
                System.out.println(weapon.getWeaponName().name());
            else
                System.out.println(ANSI_RED + weapon.getWeaponName().name() + UNICODE_NO_AMMO + ANSI_RESET);
        }
    }

    /**
     * This method prints all powerups that a player owns.
     *
     * @param powerups an ArrayList of SmartPowerup that contains all player powerups.
     */
    private synchronized void printPlayerPowerups(ArrayList<SmartPowerup> powerups) {

        System.out.println("| Power-ups: ");
        if (powerups.isEmpty())
            System.out.println("| No power-ups");
        for (SmartPowerup pu : powerups) {
            System.out.println("| " + pu.getColor() + " " + pu.getPowerupName().name());
        }
    }

    /**
     * This method prints the killshot track if the game is in normal mode or each spawn damage if the game is in domination mode.
     */
    private synchronized void printKillShotTrack() {

        Integer maxKills = settings.getMaxKills();
        ArrayList<Figure> killShotTrack = model.getKillshotTrack();
        System.out.println();

        if (!model.getDomination()) {

            //print normal mode kill shot track
            System.out.print("KILL SHOT TRACK: ");
            for (Figure figure : killShotTrack) {
                System.out.print(" | ");
                System.out.print(parseFigure(figure));
                //fill until next border
                printSpacesFromIndexToIndex(8, SQUARES_WIDTH - 1);
            }
            for (int i = killShotTrack.size(); i < maxKills; i++) {
                System.out.print(" | " + ANSI_RED + UNICODE_SKULL + ANSI_RESET);
            }
            System.out.println(" |");
        } else {

            //print spawn squares damages by color
            for (Color color : model.getSpawnDamageTrack().keySet()) {
                System.out.println(color.name() + " SPAWN DAMAGES: ");
                for (Figure figure : model.getSpawnDamageTrack().get(color)) {
                    System.out.print(" | ");
                    System.out.print(parseFigure(figure));
                    //fill until next border
                    printSpacesFromIndexToIndex(8, SQUARES_WIDTH - 1);
                }
                System.out.println(" |");
            }
        }
    }

    /////////////////////////////////////////// ViewInterface implementations //////////////////////////////////////////////

    @Override
    public synchronized void logout() {
        System.out.println(ANSI_RED + "You were forcefully disconnected" + ANSI_RESET);
        System.exit(0);
    }

    @Override
    public synchronized void sendMessage(String s) {
        System.out.println();
        System.out.println(s);
    }

    @Override
    public synchronized void notifyEvent(String s) {
        sendMessage(s);
    }

    @Override
    public synchronized int choosePlayer(Figure[] f) {

        int choice = -1;

        System.out.println();
        while (choice < 1 || choice > f.length) {

            for (int i = 0; i < f.length; i++) {
                System.out.println((i + 1) + " - " + f[i].name());
            }
            System.out.print("\nChoose a figure by its number: ");
            choice = scannerIn.nextInt();
            if (choice < 1 || choice > f.length)
                System.out.println(ANSI_RED + INVALID_INPUT_MESSAGE + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public synchronized int chooseWeapon(WeaponName[] w) {

        int choice = -1;

        System.out.println();
        while (choice < 1 || choice > w.length) {

            for (int i = 0; i < w.length; i++) {
                System.out.println((i + 1) + " - " + w[i].name());
            }
            System.out.print("\nChoose a weapon by its number: ");
            if (scannerIn.hasNextInt())
                choice = scannerIn.nextInt();
            if (choice < 1 || choice > w.length)
                System.out.println(ANSI_RED + INVALID_INPUT_MESSAGE + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public synchronized int chooseString(String[] s) {

        int choice = -1;

        System.out.println();
        while (choice < 1 || choice > s.length) {

            for (int i = 0; i < s.length; i++) {
                System.out.println(i + 1 + " - " + s[i]);
            }
            System.out.print("\nChoose effect usage by its number: ");
            choice = scannerIn.nextInt();
            if (choice < 1 || choice > s.length)
                System.out.println(ANSI_RED + INVALID_INPUT_MESSAGE + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public synchronized int chooseDirection(Character[] c) {

        int choice = -1;

        System.out.println();
        while (choice < 1 || choice > c.length) {

            for (int i = 0; i < c.length; i++) {

                switch (c[i]) {
                    case 'N':{
                        System.out.println(i + 1  + "North");
                        break;
                    }
                    case 'S':{
                        System.out.println(i + 1 + "South");
                        break;
                    }
                    case 'E':{
                        System.out.println(i + 1 + "East");
                        break;
                    }
                    case 'W':{
                        System.out.println(i + 1 + "West");
                        break;
                    }
                }
            }
            System.out.print("\nChoose direction by its number: ");
            choice = scannerIn.nextInt();
            if (choice < 1 || choice > c.length)
                System.out.println(ANSI_RED + INVALID_INPUT_MESSAGE + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public synchronized int chooseColor(Color[] c) {
        int choice = -1;

        System.out.println();
        while (choice < 1 || choice > c.length) {

            for (int i = 0; i < c.length; i++) {
                System.out.println(i + 1 + " - " + c[i].name());
            }
            System.out.print("\nChoose color by its number: ");
            choice = scannerIn.nextInt();
            if (choice < 1 || choice > c.length)
                System.out.println(ANSI_RED + INVALID_INPUT_MESSAGE + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public synchronized int choosePowerup(Powerup[] p) {
        int choice = -1;

        System.out.println();
        while (choice < 1 || choice > p.length) {

            for (int i = 0; i < p.length; i++) {
                System.out.println(i + 1 + " - " + p[i].getColor().name() + " " + p[i].getName().name());
            }
            System.out.print("\nChoose power up by its number: ");
            choice = scannerIn.nextInt();
            if (choice < 1 || choice > p.length)
                System.out.println(ANSI_RED + INVALID_INPUT_MESSAGE + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public synchronized int chooseMap(int[] m) {

        int choice = -1;

        //boxing from primitive int[] to Integer[]
        Integer[] boxedArray = new Integer[m.length];
        for (int i = 0; i < m.length; i++) {
            boxedArray[i] = m[i];
        }

        List<Integer> maps = Arrays.asList(boxedArray);

        System.out.println();
        while (!maps.contains(choice - 1)) {

            for (int i = 0; i < m.length; i++) {
                System.out.println(m[i] + 1);
            }
            System.out.print("\nChoose map by its number: ");

            if (scannerIn.hasNextInt())
                choice = scannerIn.nextInt();

            if (!maps.contains(choice - 1))
                System.out.println(ANSI_RED + INVALID_INPUT_MESSAGE + ANSI_RESET);
        }
        return choice - 1;
    }

    @Override
    public synchronized int chooseMode(Character[] c) {

        String fullChoice;
        Character choice = '0';

        while (choice != 'n' && choice != 'd' && choice != 'N' && choice != 'D') {

            System.out.println("\nNormal Mode | Domination Mode");
            System.out.print("\nChoose game mode (n/d): ");

            fullChoice = scannerIn.nextLine();  //allow user to type in also the full case name
            choice = fullChoice.toCharArray()[0];

            if (choice != 'n' && choice != 'd' && choice != 'N' && choice != 'D')
                System.out.println(ANSI_RED + INVALID_INPUT_MESSAGE + ANSI_RESET);
        }

        for (int i = 0; i < c.length; i++)
            if (c[i].toString().equalsIgnoreCase(choice.toString())) return i;    //check upper and lower case
        return -1;
    }

    @Override
    public synchronized int chooseSquare(int[][] s) {

        int choice = 0;

        System.out.println();

        while (choice < 1 || choice > s[0].length) {
            for (int i = 0; i < s[0].length; i++)
                System.out.println(i+1 + " - (" + s[1][i] + "," + s[0][i] + ")");
            System.out.println("\nChoose a Square by its number: ");

            if (scannerIn.hasNextInt())
                choice = scannerIn.nextInt();

            if (choice < 1 || choice > s[0].length)
                System.out.println(ANSI_RED + INVALID_INPUT_MESSAGE + ANSI_RESET);
        }

        return choice - 1;
    }

    @Override
    public synchronized int booleanQuestion(String s) {

        System.out.println();
        System.out.println(s);
        System.out.print("Yes or No: ");
        String choice = scannerIn.nextLine();

        if (choice.equalsIgnoreCase("yes") || choice.equalsIgnoreCase("y") || choice.equalsIgnoreCase("yeah"))
            return 1;
        else if (choice.equalsIgnoreCase("no") || choice.equalsIgnoreCase("n") || choice.equalsIgnoreCase("nope"))
            return 0;
        else
            return booleanQuestion(s);      //wrong input then ask again
    }

    @Override
    public synchronized int[] chooseMultiplePowerup(Powerup[] p) {

        char wantToContinue = 'y';
        Powerup[] temp = new Powerup[p.length - 1];
        int[] out = new int[p.length];
        int i = 0;

        while (wantToContinue == 'y' && p.length != 0) {
            out[i] = choosePowerup(p);
            System.out.print("Do you want to pick another powerup? Yes or No: ");
            wantToContinue = (char) scannerIn.nextInt();
            if (wantToContinue == 'y') {
                for (int j = 0; j < p.length - 1; j++) {
                    temp[j] = p[j + 1];
                    if (j == p.length - 2)
                        temp[j + 1] = p[p.length - 1];
                }
                p = temp;
            }
            i++;
        }

        return out;
    }

    @Override
    public synchronized int[] chooseMultipleWeapon(WeaponName[] w) {

        char wantToContinue = 'y';
        WeaponName[] temp = new WeaponName[w.length - 1];
        int[] out = new int[w.length];
        int i = 0;

        while (wantToContinue == 'y' && w.length != 0) {
            out[i] = chooseWeapon(w);
            System.out.print("Do you want to pick another weapon? Yes or No: ");
            wantToContinue = (char) scannerIn.nextInt();
            if (wantToContinue == 'y') {
                for (int j = 0; j < w.length - 1; j++) {
                    temp[j] = w[j + 1];
                    if (j == w.length - 2)
                        temp[j + 1] = w[w.length - 1];
                }
                w = temp;
            }
            i++;
        }

        return out;
    }

    @Override
    public synchronized void notifyModelUpdate() {
        try {
            model=client.getModelUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        printModel();
    }
}
