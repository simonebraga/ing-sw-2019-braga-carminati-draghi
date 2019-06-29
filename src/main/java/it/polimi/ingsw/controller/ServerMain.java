package it.polimi.ingsw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.exceptionclasses.FrenzyModeException;
import it.polimi.ingsw.model.gameinitialization.GameInitializer;
import it.polimi.ingsw.model.gamelogic.actions.SpawnAction;
import it.polimi.ingsw.model.gamelogic.turn.TurnManager;
import it.polimi.ingsw.model.mapclasses.DominationSpawnSquare;
import it.polimi.ingsw.model.mapclasses.SpawnSquare;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.model.smartmodel.SmartModel;
import it.polimi.ingsw.network.UnavailableUserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class contains the server main method.<br>
 *     It regulates network setup, game initialization, old saves loading, player turns and final scoring.
 *
 * @author Draghi96
 */
public class ServerMain {

    /**
     * This final attribute indicates the milliseconds to wait to check if the login phase has ended.
     */
    private static final int LOGIN_PHASE_TIMER = 1000;

    /**
     * This final attribute indicates the name of the file containing all save files names.
     */
    private static final String SAVE_LIST_PATH = "save_list.json";

    /**
     * This final attribute indicates the number of maps available.
     */
    private static final int MAPS_NUMBER = 4;

    /**
     * This final attribute indicates the minimum number of players to let the match continue.
     */
    private static final int MINIMUM_CONNECTED_USERS_THRESHOLD = 3;

    /**
     * This final attribute indicates the prefix of every save file name.
     */
    private static final String SAVE_FILE_PREFIX = "save";

    /**
     * This final attribute indicates the name of the directory in which will be stored all save files.
     */
    private static final String SAVE_FILES_DIRECTORY = "savefiles";

    /**
     * This final attribute indicates the first turns game phase.
     */
    private static final String FIRST_TURNS_PHASE = "ft";

    /**
     * This final attribute indicates the central turns game phase.
     */
    private static final String ROLLING_TURNS_PHASE = "rll";

    /**
     * This final attribute indicates the final frenzy phase.
     */
    private static final String FINAL_FRENZY_PHASE = "ff";

    /**
     * This method is the server main method.
     * <p>It quickly instantiates a Server object and runs its network setup,
     * then it waits until the login phase is done and proceeds to the next steps.</p>
     *
     * @param args an array of strings containing hypothetical caller arguments.
     */
    public static void main(String[] args) {
        try {
            System.out.println("Adrenaline Server running...\n");

            //sets up network
            Server server = new Server();
            server.startLoginPhase();

            while (server.isLoginPhase()) {
                Thread.sleep(LOGIN_PHASE_TIMER);
            }
            //server has now connected users

            goOn(server,args,0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when the login phase is done and it goes on with the game initialization and turn management.<br>
     *     The final frenzy is executed when a FrenzyModeException is thrown by a turn execution.
     *
     * @param server is a Server object to get access to network related methods.
     * @param args an array of strings containing hypothetical caller arguments.
     * @param adminIndex an integer representing the match administrator index.
     */
    private static void goOn(Server server, String[] args, int adminIndex) {

        System.out.println();

        Integer currentPlayerIndex = 0;
        Integer startingPlayerIndex = 0;

        //create a new directory for save files if it doesn't exist yet
        File savesDir = new File(SAVE_FILES_DIRECTORY);
        if (!savesDir.exists()) {
            boolean wasCreated = savesDir.mkdir();
            if (wasCreated) {
                System.out.println("New '" + SAVE_FILES_DIRECTORY + "' directory created");
            }
        }

        //create a new save_list.json file in working directory if it doesn't exist yet
        File savesListFile = new File(SAVE_LIST_PATH);
        if (!savesListFile.exists()) {
            try {
                boolean wasCreated = savesListFile.createNewFile();
                if (wasCreated) {
                    System.out.println("New '" + SAVE_LIST_PATH + "' file created");
                    ArrayList<String> saveListInitialization = new ArrayList<>();
                    ObjectMapper mapper = new ObjectMapper();
                    FileOutputStream saveListOutput = new FileOutputStream(savesListFile);
                    mapper.writeValue(saveListOutput,saveListInitialization);
                    saveListOutput.close();
                    System.out.println("'" + SAVE_LIST_PATH + "' file initialized");
                }
            } catch (IOException e) {
                System.out.println(" 'save_list.json' file cannot be created correctly");
                e.printStackTrace();
            }
        }

        //search in old save files a compatible match
        GameTable gameTable = oldSaveSearch(new ArrayList<>(server.getNicknameSet()));  //returns null if save files are not found

        Integer mapIndex = null;
        Character gameMode = null;


        if (gameTable == null) {    //create new match

            System.out.println("Creating new match...");

            System.out.println("Binding clients to figures...");

            //binds each user to a unique player
            ArrayList<Player> players = new ArrayList<>();
            Figure[] allFigures = Figure.values();
            Integer i = 0;
            for (String nick : server.getNicknameSet()) {
                players.add(new Player(allFigures[i], nick));    //should not overflow because users are never more than figures
                i++;
            }
            System.out.println("Done");

            //ask game mode to administrator if he's still connected
            try {
                gameMode = server.chooseMode(players.get(adminIndex));
            } catch (UnavailableUserException e) {

                //try to elect another match administrator
                server.resetClientMap();
                goOn(server, args, adminIndex);
            }


            //ask map index to administrator if he's still connected
            try {
                mapIndex = server.chooseMap(players.get(adminIndex), 0, MAPS_NUMBER-1);
            } catch (UnavailableUserException e) {

                //try to elect another match administrator
                server.resetClientMap();
                goOn(server, args, adminIndex);

            }


            //initiate a new match
            System.out.println("Initializing new match...");
            GameInitializer gameInitializer = new GameInitializer(gameMode, mapIndex, players);
            gameTable = gameInitializer.run();
            System.out.println("Done");

            //if too many users dropped during this phase
            if (players.size() < MINIMUM_CONNECTED_USERS_THRESHOLD) {
                System.out.println("Too many players have disconnected. Relaunching server...");
                server.resetClientMap();
                main(args);
            }

            //finally created usable match
            save(gameTable);
        }

        //calculate starting player and current player indexes
        while (!gameTable.getPlayers().get(currentPlayerIndex).equals(gameTable.getCurrentTurnPlayer())) currentPlayerIndex++;
        while (!gameTable.getPlayers().get(startingPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget())) startingPlayerIndex++;

        //sync smart model with model
        SmartModel smartModel = new SmartModel();
        smartModel.setMapIndex(mapIndex);
        server.setSmartModel(smartModel);
        smartModel.update(gameTable);
        server.notifyModelUpdate();
        System.out.println("Smart model created");

        System.out.println("Starting game...");
        //start or continue match
        if (gameTable.getGamePhase().equals(FIRST_TURNS_PHASE)) {   //game is in first turns phase

            //make each player spawn and first turn
            try {
                firstTurns(server, gameTable, currentPlayerIndex);  //would never throw FrenzyModeException
            } catch (FrenzyModeException e) {

                finalFrenzy(server,gameTable);
                main(args);
            }
        }

        if (gameTable.getGamePhase().equals(ROLLING_TURNS_PHASE)) {  //game is in rolling turns phase

            //match rolling...
            try {
                rollMatch(server, gameTable, currentPlayerIndex);      //would throw FrenzyModeException at some point or game will stop for loss of players
            } catch (FrenzyModeException e) {

                finalFrenzy(server,gameTable);
                main(args);
                e.printStackTrace();
            }

            //game has ended before final frenzy because too many people disconnected
            System.out.println("Not enough players to continue the game");
            gameOver(gameTable);

            //restart program
            main(args);
        }

    }

    /**
     * This private method creates and executes all first spawn actions and turn, considering the first player.
     *
     * @param server a Server object that ensures communication with users about choices on their turn options.
     * @param gameTable a GameTable object that represents the match status.
     * @param currentPlayerIndex an Integer that marks the current player index inside the players list.
     * @throws FrenzyModeException when a turn execution sets all conditions for the final frenzy.
     */
    private static void firstTurns(Server server, GameTable gameTable, Integer currentPlayerIndex) throws FrenzyModeException {

        //execute first player turn, if he is still connected
        if (server.isConnected(gameTable.getPlayers().get(currentPlayerIndex))) {

            SpawnAction spawnAction0 = new SpawnAction(gameTable.getStartingPlayerMarker().getTarget());
            spawnAction0.run(server,gameTable);
            TurnManager turn0 = new TurnManager(gameTable.getStartingPlayerMarker().getTarget(),false,false);
            turn0.runTurn(server,gameTable);
        }

        //cycling array
        currentPlayerIndex++;
        if(currentPlayerIndex==gameTable.getPlayers().size()) currentPlayerIndex = 0;
        gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(currentPlayerIndex));

        //auto save
        save(gameTable);

        //execute other first turns, from current player to starting player -1, if they are connected
        while (!gameTable.getPlayers().get(currentPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget())) {

            if (server.isConnected(gameTable.getPlayers().get(currentPlayerIndex))) {

                SpawnAction spawnAction = new SpawnAction(gameTable.getPlayers().get(currentPlayerIndex));
                spawnAction.run(server,gameTable);
                TurnManager turn = new TurnManager(gameTable.getPlayers().get(currentPlayerIndex),false,false);
                turn.runTurn(server,gameTable);
            }

            //cycling array
            currentPlayerIndex++;
            if (currentPlayerIndex == gameTable.getPlayers().size()) currentPlayerIndex = 0;
            gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(currentPlayerIndex));

            //auto save
            save(gameTable);
        }

        //transitioning match phase
        gameTable.setGamePhase(ROLLING_TURNS_PHASE);
        save(gameTable);
    }

    /**
     * This private method executes all turns following the players list order until a turn execution throws a FrenzyModeException.
     *
     * @param server a Server object that ensures communication with users about choices on their turn options.
     * @param currentPlayerIndex an Integer that marks the current player index inside the players list.
     * @throws FrenzyModeException when a turn execution sets all conditions for the final frenzy.
     */
    private static void rollMatch(Server server, GameTable gameTable, Integer currentPlayerIndex) throws FrenzyModeException {

        //this while will break at some point because of FrenzyModeException throw
        int i=currentPlayerIndex;
        while(gameTable.getPlayers().size() >= MINIMUM_CONNECTED_USERS_THRESHOLD) {   //there are at least 3 players still connected

            //execute turn if player is still connected
            if (server.isConnected(gameTable.getPlayers().get(i))) {

                TurnManager turn = new TurnManager(gameTable.getPlayers().get(i),false,true);
                turn.runTurn(server,gameTable);
            }

            //circular list approach for players
            i++;
            if (i==gameTable.getPlayers().size()) i=0;

            //pass turn to next player
            gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(i));

            save(gameTable);
        }
        //if this while stops without FinalFrenzy exception throw it's because there are less than 3 players connected
    }

    /**
     * This private method serializes all match information into a json file using a parameter as name.<br>
     *     It also updates the save_list.json file to show the latest save.
     *
     * @param gameTable a GameTable object that captures all match information.
     */
    private static void save(GameTable gameTable) {

        String mySaveName = gameTable.getSaveFileName();
        try {

            //retrieve save_list.json and parse it into a list
            ArrayList<String> fileNamesList = new ArrayList<>();
            FileInputStream fileNamesInputStream;
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            File saveListFile = new File(SAVE_LIST_PATH);

            //initialize file if it is empty
            if (saveListFile.length() == 0) {
                mapper.writeValue(saveListFile,fileNamesList.toArray());
                System.out.println("New 'save_list.json' file created");
            }
            fileNamesInputStream = new FileInputStream(SAVE_LIST_PATH);
            fileNamesList = new ArrayList<>(Arrays.asList(mapper.readValue(fileNamesInputStream,String[].class)));
            fileNamesInputStream.close();

            if (mySaveName.equals("new")) {    //create new save file

                //find first available file name counter
                int fileCounter = 0;
                while (fileNamesList.contains(SAVE_FILE_PREFIX + fileCounter)) fileCounter++;

                //update game table attribute
                gameTable.setSaveFileName(SAVE_FILE_PREFIX + fileCounter);

                //create a new save file in savefiles directory
                File file = new File(SAVE_FILES_DIRECTORY + "/" + SAVE_FILE_PREFIX + fileCounter + ".json");
                file.createNewFile();
                System.out.println("New '" + SAVE_FILE_PREFIX + fileCounter + ".json' save file created");
                FileOutputStream fileOutput = new FileOutputStream(  SAVE_FILES_DIRECTORY + "/" + SAVE_FILE_PREFIX + fileCounter + ".json");
                mapper.writeValue(fileOutput, gameTable);
                fileOutput.close();
                System.out.println("New match saved");

                //update save_list.json to show new save file in the list
                fileNamesList.add(SAVE_FILE_PREFIX + fileCounter);

                //rewrite list in save_list.json as an array of strings
                fileOutput = new FileOutputStream(SAVE_LIST_PATH);
                mapper.writeValue(fileOutput, fileNamesList.toArray());
                fileOutput.close();

            } else {    //overwrite on old save file

                FileOutputStream fileOutputStream = new FileOutputStream(  SAVE_FILES_DIRECTORY + "/" + mySaveName + ".json");
                mapper.writeValue(fileOutputStream,gameTable);
                fileOutputStream.close();
                System.out.println("Match save overwritten");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method checks if there are compatible old games save files given a collection of users nicknames.
     *
     * @param nicks the String collection by which users saved in old matches will be confronted.
     * @return a GameTable object to be used as game table if compatible save files are found.<br>
     *     It returns null if no file matches current users nicknames.
     */
    private static GameTable oldSaveSearch(ArrayList<String> nicks) {

        System.out.println("Scanning old save files...");

        try {

            //get save files name list
            ObjectMapper mapper = new ObjectMapper();
            FileInputStream namesIn = new FileInputStream(SAVE_LIST_PATH);
            String[] fileNamesList = mapper.readValue(namesIn,String[].class);
            namesIn.close();

            //search compatibles save files
            for (String fileName : fileNamesList) {

                //get save files 1 by 1
                FileInputStream tableIn = new FileInputStream(SAVE_FILES_DIRECTORY + "/" + fileName + ".json");
                GameTable oldTable = mapper.readValue(tableIn, GameTable.class);
                tableIn.close();

                //consider old match users in a collection
                ArrayList<String> oldMatchUsers = new ArrayList<>();
                for (Player player : oldTable.getPlayers()) {
                    oldMatchUsers.add(player.getUsername());
                }

                //check if new users are compatible with old match users nicks
                boolean everyNickMatches = true;
                for (String nickname : nicks) {
                    if (!oldMatchUsers.contains(nickname)) {
                        everyNickMatches = false;
                    }
                }
                if (everyNickMatches) {
                    System.out.println("Compatible match found");
                    return oldTable;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //no compatible save files where found
        System.out.println("No compatible previous matches were found");
        return null;
    }

    /**
     * This method deletes a terminated match save file and removes it from the save_list.json file.
     *
     * @param fileName a String that represents the name of the file to be deleted.
     */
    private static void deleteSave(String fileName) {

        try {

            //retrieve save names list
            ObjectMapper mapper = new ObjectMapper();
            FileInputStream input = new FileInputStream(SAVE_LIST_PATH);
            ArrayList<String> nameList = new ArrayList<>(Arrays.asList(mapper.readValue(input,String[].class)));
            input.close();

            //delete save file if listed
            if (nameList.contains(fileName)) {
                File save = new File(SAVE_FILES_DIRECTORY + "/" + fileName + ".json");
                save.delete();

                //update save names list
                nameList.remove(fileName);
                FileOutputStream output = new FileOutputStream(SAVE_LIST_PATH);
                mapper.writeValue(output, (String[]) nameList.toArray());
                output.close();

                System.out.println("Match save file deleted");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method evolves all last final frenzy turns and ends the match.
     *
     * @param server a Server object to get access to network interaction.
     * @param gameTable a GameTable object with all match information inside.
     */
    private static void finalFrenzy(Server server, GameTable gameTable) {

        //if this match was never in final frenzy before...
        if (!gameTable.getGamePhase().equals(FINAL_FRENZY_PHASE)) {

            //change bounty value to each undamaged player
            Integer[] points = {2, 1, 1, 1, 1, 1};
            for (Player player : gameTable.getPlayers()) {
                if (player.getDamageTrack().getDamage().isEmpty()) {
                    player.getPointTrack().setValue(new ArrayList<>(Arrays.asList(points)));
                }
            }

            gameTable.setFrenzyBeginner(gameTable.getCurrentTurnPlayer());

            //match is now in final frenzy
            gameTable.setGamePhase(FINAL_FRENZY_PHASE);
        }

        try {

            //find frenzy beginner, current player and starting player indexes inside players list
            int frenzyBeginnerIndex = 0;
            int currentPlayerIndex = 0;
            int startingPlayerIndex = 0;
            while (!gameTable.getPlayers().get(currentPlayerIndex).equals(gameTable.getCurrentTurnPlayer())) currentPlayerIndex++;
            while (!gameTable.getPlayers().get(startingPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget())) startingPlayerIndex++;
            while (!gameTable.getPlayers().get(frenzyBeginnerIndex).equals(gameTable.getFrenzyBeginner())) frenzyBeginnerIndex++;

            int tempPlayerIndex;
            for (tempPlayerIndex = currentPlayerIndex;
                 !gameTable.getPlayers().get(tempPlayerIndex).equals(gameTable.getFrenzyBeginner()) &&
                 !gameTable.getPlayers().get(tempPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget());
                 tempPlayerIndex++) {

                if (tempPlayerIndex == gameTable.getPlayers().size() - 1) tempPlayerIndex = -1; //cycling array
            }

            if (gameTable.getPlayers().get(tempPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget()) &&
                !gameTable.getPlayers().get(tempPlayerIndex).equals(gameTable.getFrenzyBeginner())) {

                //from current player to starting player -1, all before
                cycleHalfFrenzyTurn(gameTable,server,currentPlayerIndex,gameTable.getStartingPlayerMarker().getTarget(),true);

                //from current player to frenzy beginner -1, all after
                cycleHalfFrenzyTurn(gameTable,server,currentPlayerIndex,gameTable.getFrenzyBeginner(),false);

            } else if (!gameTable.getPlayers().get(tempPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget()) &&
                        gameTable.getPlayers().get(tempPlayerIndex).equals(gameTable.getFrenzyBeginner())) {

                //from current player to frenzy beginner -1, all after
                cycleHalfFrenzyTurn(gameTable,server,currentPlayerIndex,gameTable.getFrenzyBeginner(),false);

                //from frenzy beginner to starting player -1, all before
                cycleHalfFrenzyTurn(gameTable,server,currentPlayerIndex,gameTable.getStartingPlayerMarker().getTarget(),true);

            } else {    //frenzy beginner is starting player

                //from starting player to starting player -1, all after
                cycleHalfFrenzyTurn(gameTable,server,currentPlayerIndex, gameTable.getStartingPlayerMarker().getTarget(),false);

            }

            //end game
            gameOver(gameTable);

        } catch (FrenzyModeException e) {
            //should never go here
            System.out.println("Something went terribly wrong inside turn management.\n");
            e.printStackTrace();
        }
    }

    /**
     * This method runs final frenzy turns from a player to another excluding the last one, considering if player are before or after the starting player.
     *
     * @param gameTable a GameTable object with all match information.
     * @param server a Server object to get access to network.
     * @param currentPlayerIndex a player index representing the player that holds the current turn.
     * @param stopPlayer the Player reference to get to by running final frenzy turns for players, in the players array order.
     * @param isBefore a boolean flag that indicates if this half of final frenzy turns are to be run as before or after starting player.
     * @throws FrenzyModeException if running a turn throws this exception.
     */
    private static void cycleHalfFrenzyTurn(GameTable gameTable, Server server, int currentPlayerIndex, Player stopPlayer, boolean isBefore) throws FrenzyModeException {

        while (!gameTable.getPlayers().get(currentPlayerIndex).equals(stopPlayer)) {

            if (server.isConnected(gameTable.getPlayers().get(currentPlayerIndex))) {
                TurnManager turn = new TurnManager(gameTable.getPlayers().get(currentPlayerIndex), true, isBefore);
                turn.runTurn(server, gameTable);
            }

            //cycling array
            currentPlayerIndex++;
            if (currentPlayerIndex == gameTable.getPlayers().size()) currentPlayerIndex = 0;
            gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(currentPlayerIndex));

            save(gameTable);
        }
    }

    /**
     * This method runs all last operations before considering a match over.
     *
     * @param gameTable a GameTable object that holds all match information.
     */
    private static void gameOver(GameTable gameTable) {

        calculateFinalPoints(gameTable);
        deleteSave(gameTable.getSaveFileName());

        //announce scoreboard
        Player winner = gameTable.getPlayers().get(0);
        for (Player player : gameTable.getPlayers()) {
            if (player.getPoints() > winner.getPoints())
                winner = player;
            System.out.println("\n" + player.getUsername() + ": " + player.getPoints());
        }
        System.out.println("\nThe winner is " + winner.getUsername() + "!");
    }

    /**
     * This private method calculates final points for each player and adds them to the total points of each player.
     *
     * @param gameTable a GameTable object containing all match information.
     */
    private static void calculateFinalPoints(GameTable gameTable) {

        ArrayList<Player> players = gameTable.getPlayers();
        ArrayList<Player> doubleKillers = gameTable.getDoubleKillCounter().getList();
        ArrayList<Integer> pointsByKillNumber = gameTable.getKillshotTrack().getValue();
        Integer doubleKillValue = gameTable.getDoubleKillCounter().getDoubleKillValue();
        ArrayList<ArrayList<Player>> killTracks = new ArrayList<>();

        //link killTracks with all killTracks in match
        if (gameTable.getIsDomination()) {

            //cast SpawnSquares to get DominationSpawnSquares attributes
            ArrayList<DominationSpawnSquare> dominationSpawnSquaresList = new ArrayList<>();
            for (SpawnSquare spawnSquare : gameTable.getGameMap().getSpawnSquares()) {
                dominationSpawnSquaresList.add((DominationSpawnSquare) spawnSquare);
            }

            for (DominationSpawnSquare square : dominationSpawnSquaresList) {
                killTracks.add(square.getDamage());
            }

        } else {
            killTracks.add(gameTable.getKillshotTrack().getKillTrack());
        }

        for (ArrayList<Player> killTrack : killTracks) {

            //count players presence on killshot track
            Map<Long, ArrayList<Player>> killsByPlayer = new LinkedHashMap<>();
            for (Player player : killTrack) {

                Long count = killTrack.stream().filter(player1 -> player.equals(player)).count();

                if (killsByPlayer.get(count) == null) {

                    ArrayList<Player> arrayList = new ArrayList<>();
                    arrayList.add(player);
                    killsByPlayer.put(count, arrayList);

                } else if (!killsByPlayer.get(count).contains(player)) {
                    killsByPlayer.get(count).add(player);
                }
            }

            //sort keys in descending order
            ArrayList<Long> sortedKeys = (ArrayList<Long>) Arrays.asList(killsByPlayer.keySet().toArray()).stream().sorted();

            //give points away to each player
            int scoreValuesCounter = 0;
            int sortedKeysCounter = 0;
            while (!killsByPlayer.isEmpty()) {
                int tiesCounter = 0;
                while (tiesCounter < killsByPlayer.get(sortedKeys.get(sortedKeysCounter)).size()) {
                    if (pointsByKillNumber.get(scoreValuesCounter) != 1) {
                        killsByPlayer.get(sortedKeys.get(sortedKeysCounter)).get(tiesCounter).addPoints(pointsByKillNumber.get(scoreValuesCounter));
                        if (!gameTable.getIsDomination())
                            scoreValuesCounter++;
                    } else {    // if scores are already gone down to 1 point per killer
                        killsByPlayer.get(sortedKeys.get(sortedKeysCounter)).get(tiesCounter).addPoints(1);
                    }
                    tiesCounter++;
                }
                killsByPlayer.remove(sortedKeys.get(sortedKeysCounter));
                sortedKeysCounter++;
                if (gameTable.getIsDomination())
                    scoreValuesCounter+=tiesCounter;
            }
        }

        //assign double kills points
        for (Player killer : doubleKillers) {
            killer.addPoints(doubleKillValue);
        }
    }
}