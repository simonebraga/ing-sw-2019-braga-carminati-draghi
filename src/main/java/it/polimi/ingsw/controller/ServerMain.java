package it.polimi.ingsw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.GameTable;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.exceptionclasses.FrenzyModeException;
import it.polimi.ingsw.model.gameinitialization.GameInitializer;
import it.polimi.ingsw.model.gamelogic.actions.SpawnAction;
import it.polimi.ingsw.model.gamelogic.turn.TurnManager;
import it.polimi.ingsw.model.playerclasses.Player;
import it.polimi.ingsw.network.UnavailableUserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class contains the server main method.<br>
 *     It regulates network setup, game initialization, old saves loading, player turns and final scoring.
 *
 * @author Draghi96
 */
public class ServerMain {

    /**
     * This method is the server main method.
     * <p>It quickly instantiates a Server object and runs its network setup,
     * then it waits until the login phase is done and proceeds to the next steps.</p>
     *
     * @param args an array of strings containing hypothetical caller arguments.
     */
    public static void main(String[] args) {
        try {

            //sets up network
            Server server = new Server();
            server.startLoginPhase();

            while (server.isLoginPhase()) {
                Thread.sleep(1000);
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

        Integer currentPlayerIndex = 0;
        Integer startingPlayerIndex = 0;

        //create a new save_list.json file in working directory
        File file = new File("save_list.json");
        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //checks for compatible save files
        GameTable gameTable = oldSaveSearch(new ArrayList<>(server.getNicknameSet()));  //returns null if save files are not found

        try {

            if (gameTable == null) {    //create new match

                //binds each user to a unique player
                ArrayList<Player> players = new ArrayList<>();
                Figure[] allFigures = Figure.values();
                Integer i = 0;
                for (String nick : server.getNicknameSet()) {
                    players.add(new Player(allFigures[i], nick));    //should not overflow because users are never more than figures
                    i++;
                }

                //ask game mode and map index to administrator
                Character gameMode = server.chooseMode(players.get(adminIndex));
                Integer mapIndex = server.chooseMap(players.get(adminIndex), 0, 3);

                //initiate a new match
                GameInitializer gameInitializer = new GameInitializer(gameMode, mapIndex, players);
                gameTable = gameInitializer.run();

                //if too many users dropped during this phase
                if (players.size() < 3) {
                    server.resetClientMap();
                    main(args);
                }

                save(gameTable);
            }

            //calculate starting player and current player indexes
            while (!gameTable.getPlayers().get(currentPlayerIndex).equals(gameTable.getCurrentTurnPlayer())) currentPlayerIndex++;
            while (!gameTable.getPlayers().get(startingPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget())) startingPlayerIndex++;

            //start or continue match
            if (gameTable.getGamePhase().equals("ft")) {   //game is in first turns phase

                //make each player spawn and first turn
                firstTurns(server, gameTable, currentPlayerIndex);  //would never throw FrenzyModeException
            }

            if (gameTable.getGamePhase().equals("rll")) {  //game is in rolling turns phase

                //match rolling...
                rollMatch(server, gameTable, currentPlayerIndex);      //would throw FrenzyModeException at some point or game will stop for loss of players

                //game has ended before final frenzy because too many people disconnected
                System.out.println("Not enough players to continue the game.");
                proclaimWinner(gameTable);
                deleteSave(gameTable.getSaveFileName());

                //restart program
                main(args);
            }
        } catch (UnavailableUserException e) {

            //try to elect another match administrator
            adminIndex++;
            goOn(server, args, adminIndex);

        } catch (FrenzyModeException e) {
            finalFrenzy(server,gameTable);
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
        gameTable.setGamePhase("rll");
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
        while(gameTable.getPlayers().size()>=3) {   //there are at least 3 players still connected

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
            ObjectMapper mapper = new ObjectMapper();
            FileInputStream fileInput = new FileInputStream("save_list.json");
            ArrayList<String> fileNamesList = new ArrayList<>(Arrays.asList(mapper.readValue(fileInput,String[].class)));
            fileInput.close();

            if (mySaveName.equals("new")) {    //create new save file

                //find first available file name counter
                int fileCounter = 0;
                while (fileNamesList.contains("save" + fileCounter)) fileCounter++;

                //update game table attribute
                gameTable.setSaveFileName("save" + fileCounter);

                //create a new save file in /savefiles directory
                File file = new File("savefiles/save" + fileCounter + ".json");
                file.createNewFile();
                FileOutputStream fileOutput = new FileOutputStream("savefiles/save" + fileCounter + ".json");
                mapper.writeValue(fileOutput, gameTable);
                fileOutput.close();

                //update save_list.json to show new save file in the list
                fileNamesList.add("save_" + fileCounter);

                //rewrite list in save_list.json as an array of strings
                fileOutput = new FileOutputStream("save_list.json");
                mapper.writeValue(fileOutput, (String[]) fileNamesList.toArray());
                fileOutput.close();

            } else {    //overwrite on old save file

                FileOutputStream fileOutputStream = new FileOutputStream("savefiles/" + mySaveName + ".json");
                mapper.writeValue(fileOutputStream,gameTable);
                fileOutputStream.close();
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

        try {

            //get save files name list
            ObjectMapper mapper = new ObjectMapper();
            FileInputStream namesIn = new FileInputStream("save_list.json");
            String[] fileNamesList = mapper.readValue(namesIn,String[].class);
            namesIn.close();

            //search compatibles save files
            for (String fileName : fileNamesList) {

                //get save files 1 by 1
                FileInputStream tableIn = new FileInputStream("savefiles/" + fileName);
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
                    return oldTable;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //if this for stops no compatible save files where found
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
            FileInputStream input = new FileInputStream("save_list.json");
            ArrayList<String> nameList = new ArrayList<>(Arrays.asList(mapper.readValue(input,String[].class)));
            input.close();

            //delete save file if listed
            if (nameList.contains(fileName)) {
                File save = new File("savefiles/" + fileName);
                save.delete();

                //update save names list
                nameList.remove(fileName);
                FileOutputStream output = new FileOutputStream("save_list.json");
                mapper.writeValue(output, (String[]) nameList.toArray());
                output.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void finalFrenzy(Server server, GameTable gameTable) {

        //TODO(to fully review)
        //TODO(update to continue old final frenzy from save file)

        //match is now in final frenzy

        //change bounty value to each undamaged player
        Integer[] points = {2, 1, 1, 1, 1, 1};
        for (Player player : gameTable.getPlayers()) {
            if (player.getDamageTrack().getDamage().isEmpty()) {
                player.getPointTrack().setValue(new ArrayList<>(Arrays.asList(points)));
            }
        }

        //this will contain all frenzy turns already in the correct order
        ArrayList<TurnManager> frenzyTurns = new ArrayList<>();

        //find current player inside players list
        int currentPlayerIndex;
        for (currentPlayerIndex = 0; !gameTable.getPlayers().get(currentPlayerIndex).equals(gameTable.getCurrentTurnPlayer()); currentPlayerIndex++)
            ;
        //currentPlayerIndex is the current player index

        //find starting player inside players list
        int startingPlayerIndex;
        for (startingPlayerIndex = 0; !gameTable.getPlayers().get(startingPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget()); startingPlayerIndex++)
            ;
        //startingPlayerIndex is the starting player index

        //create frenzy turns considering players position relative to first player in the turn sequence
        int i;
        //from current player to starting player -1, all before starting player
        for (i = currentPlayerIndex; !gameTable.getPlayers().get(i).equals(gameTable.getStartingPlayerMarker().getTarget()); i++) {
            if (server.isConnected(gameTable.getPlayers().get(i))) {
                frenzyTurns.add(new TurnManager(gameTable.getPlayers().get(i), true, true));
            }

            //cycling array
            if (i == gameTable.getPlayers().size()) {
                i = -1;
            }
        }

        //from starting player to current player -1, all after starting player
        while (!gameTable.getPlayers().get(i).equals(gameTable.getCurrentTurnPlayer())) {
            if (server.isConnected(gameTable.getPlayers().get(i))) {
                frenzyTurns.add(new TurnManager(gameTable.getPlayers().get(i), true, false));
            }

            //cycling array
            if (i == gameTable.getPlayers().size()) {
                i = -1;
            }
            i++;
        }

        //execute final turns
        for (TurnManager turn : frenzyTurns) {
            try {
                turn.runTurn(server, gameTable);
                currentPlayerIndex++;

                //cycle array
                if (currentPlayerIndex == gameTable.getPlayers().size()) {
                    currentPlayerIndex = 0;
                }
                gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(currentPlayerIndex));
            } catch (FrenzyModeException ex) {
                //should never end up here
                ex.printStackTrace();
            }
        }

        //calculate winner
        Player winner = proclaimWinner(gameTable);

        System.out.println(winner.getUsername() + "won the game.\n");

        //program ends

    }

    /**
     * This private method calculates final points for each player and returns the player with the highest score.
     *
     * @param gameTable a GameTable object which contains all match information.
     * @return the player which has the highest score.
     */
    static private Player proclaimWinner(GameTable gameTable) {

        //TODO

        return null;
    }
}