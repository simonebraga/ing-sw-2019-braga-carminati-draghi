package it.polimi.ingsw;

import it.polimi.ingsw.controller.Server;
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

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains the server main method.<br>
 *     It regulates network setup, game initialization, player turns and final scoring.
 *
 * @author Draghi96
 */
public class ServerMain {

    /**
     * This final attribute indicates the milliseconds to wait to check if the login phase has ended.
     */
    private static final int LOGIN_PHASE_TIMER = 1000;

    /**
     * This final attribute indicates the number of maps available.
     */
    private static final int MAPS_NUMBER = 4;

    /**
     * This final attribute indicates the minimum number of players to let the match continue.
     */
    private static final int MINIMUM_CONNECTED_USERS_THRESHOLD = 3;

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
     * This final attribute is the message shows when there is an error in args parsing
     */
    private static final String ERROR_PARSING = "Error parsing arguments";

    /**
     * This final attribute is the name of the network properties file
     */
    private static final String NETWORK_PROPERTIES = "network_settings.properties";

    /**
     * This method is the server main method.
     * <p>It quickly instantiates a Server object and runs its network setup,
     * then it waits until the login phase is done and proceeds to the next steps.</p>
     *
     * @param args an array of String which contains optional starting parameters:
     *
     *             Allowed parameters:
     *             -serverIp String containing the ip address of the server
     *             -loginTimer int representing the login phase timer
     *             -inactivityTime int representing the single move timeout
     *
     *             Not specified parameters are loaded from a default configuration file
     */
    public static void main(String[] args) {
        try {

            Properties networkProperties = new Properties();
            try {
                networkProperties.load(Objects.requireNonNull(ServerMain.class.getClassLoader().getResourceAsStream(NETWORK_PROPERTIES)));
            } catch (IOException e) {
                System.err.println("Error reading " + NETWORK_PROPERTIES);
                throw new Exception();
            }

            String serverIp = null;
            int loginTimer = -1;
            int inactivityTime = -1;
            for (int i = 0 ; i < args.length ; i++) {
                if ((i+1 < args.length) && (args[i+1].toCharArray()[0] == '-')) {
                    System.out.println(ERROR_PARSING);
                    System.exit(0);
                }
                switch (args[i]) {
                    case "-serverIp": {
                        if (serverIp != null) {
                            System.out.println(ERROR_PARSING);
                            System.exit(0);
                        } i++;
                        if (i < args.length)
                            serverIp = args[i];
                        else {
                            System.out.println(ERROR_PARSING);
                            System.exit(0);
                        }
                        break;
                    }
                    case "-loginTimer": {
                        if (loginTimer > 0) {
                            System.out.println(ERROR_PARSING);
                            System.exit(0);
                        } i++;
                        if (i < args.length)
                            loginTimer = Integer.parseInt(args[i]);
                        else {
                            System.out.println(ERROR_PARSING);
                            System.exit(0);
                        }
                        if (loginTimer < 0) {
                            System.out.println(ERROR_PARSING);
                            System.exit(0);
                        }
                        break;
                    }
                    case "-inactivityTime": {
                        if (inactivityTime > 0) {
                            System.out.println(ERROR_PARSING);
                            System.exit(0);
                        } i++;
                        if (i < args.length)
                            inactivityTime = Integer.parseInt(args[i]);
                        else {
                            System.out.println(ERROR_PARSING);
                            System.exit(0);
                        }
                        if (inactivityTime < 0) {
                            System.out.println(ERROR_PARSING);
                            System.exit(0);
                        }
                        break;
                    }
                    default: {
                        System.out.println(ERROR_PARSING);
                        System.exit(0);
                    }
                }
            }

            if (serverIp == null)
                serverIp = networkProperties.getProperty("serverIp");
            if (loginTimer < 0)
                loginTimer = Integer.parseInt(networkProperties.getProperty("loginTimerLength"));
            if (inactivityTime < 0)
                inactivityTime = Integer.parseInt(networkProperties.getProperty("inactivityTime"));

            System.out.println("Adrenaline Server running...\n");

            //sets up network
            Server server = new Server(serverIp,loginTimer,inactivityTime);
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

        GameTable gameTable;
        
        Integer mapIndex = null;
        Character gameMode = null;

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

            System.out.println("No response from users");
            System.exit(0);
        }


        //ask map index to administrator if he's still connected
        try {
            mapIndex = server.chooseMap(players.get(adminIndex), 0, MAPS_NUMBER-1);
        } catch (UnavailableUserException e) {

            System.out.println("No response from users");
            System.exit(0);

        }


        //initiate a new match
        System.out.println("Initializing new match...");
        GameInitializer gameInitializer = new GameInitializer(gameMode, mapIndex, players);
        gameTable = gameInitializer.run();
        System.out.println("Done");

        //if too many users dropped during this phase
        checkPlayers(server,gameTable,true);

        //finally created usable match

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
            }
        }

        if (gameTable.getGamePhase().equals(ROLLING_TURNS_PHASE)) {  //game is in rolling turns phase

            //match rolling...
            try {
                rollMatch(server, gameTable, currentPlayerIndex);      //would throw FrenzyModeException at some point or game will stop for loss of players
            } catch (FrenzyModeException e) {

                finalFrenzy(server,gameTable);
            }

            //game has ended before final frenzy because too many people disconnected
            System.out.println("Not enough players to continue the game");
            gameOver(server, gameTable);
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

        checkPlayers(server, gameTable, false);

        //execute first player turn, if he is still connected
        if (server.isConnected(gameTable.getPlayers().get(currentPlayerIndex))) {

            try {
                server.sendMessage(gameTable.getPlayers().get(currentPlayerIndex),"Choose a Power up to discard. You will be spawned to the spawn square having its same color.");
            } catch (UnavailableUserException e) {
                e.printStackTrace();
            }

            SpawnAction spawnAction0 = new SpawnAction(gameTable.getStartingPlayerMarker().getTarget());
            spawnAction0.run(server,gameTable);

            server.getSmartModel().update(gameTable);
            server.notifyModelUpdate();

            TurnManager turn0 = new TurnManager(gameTable.getStartingPlayerMarker().getTarget(),false,false);
            turn0.runTurn(server,gameTable);
        }

        //cycling array
        currentPlayerIndex++;
        if(currentPlayerIndex==gameTable.getPlayers().size()) currentPlayerIndex = 0;
        gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(currentPlayerIndex));

        //execute other first turns, from current player to starting player -1, if they are connected
        while (!gameTable.getPlayers().get(currentPlayerIndex).equals(gameTable.getStartingPlayerMarker().getTarget())) {

            checkPlayers(server, gameTable, false);

            if (server.isConnected(gameTable.getPlayers().get(currentPlayerIndex))) {

                SpawnAction spawnAction = new SpawnAction(gameTable.getPlayers().get(currentPlayerIndex));
                spawnAction.run(server,gameTable);

                server.getSmartModel().update(gameTable);
                server.notifyModelUpdate();

                TurnManager turn = new TurnManager(gameTable.getPlayers().get(currentPlayerIndex),false,false);
                turn.runTurn(server,gameTable);
            }

            //cycling array
            currentPlayerIndex++;
            if (currentPlayerIndex == gameTable.getPlayers().size()) currentPlayerIndex = 0;
            gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(currentPlayerIndex));

        }

        //transitioning match phase
        gameTable.setGamePhase(ROLLING_TURNS_PHASE);
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
        while(server.getActivePlayers().size() >= MINIMUM_CONNECTED_USERS_THRESHOLD) {   //there are at least 3 players still connected

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
        }
        //if this while stops without FinalFrenzy exception throw it's because there are less than 3 players connected
        checkPlayers(server, gameTable, false);
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
            gameOver(server, gameTable);

        } catch (FrenzyModeException e) {
            //normal execution
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

            checkPlayers(server, gameTable, false);

            if (server.isConnected(gameTable.getPlayers().get(currentPlayerIndex))) {
                TurnManager turn = new TurnManager(gameTable.getPlayers().get(currentPlayerIndex), true, isBefore);
                turn.runTurn(server, gameTable);
            }

            //cycling array
            currentPlayerIndex++;
            if (currentPlayerIndex == gameTable.getPlayers().size()) currentPlayerIndex = 0;
            gameTable.setCurrentTurnPlayer(gameTable.getPlayers().get(currentPlayerIndex));

        }
    }

    /**
     * This method checks if there are still enough active players on the server.
     *
     * @param server a Server method with all methods to connect with clients.
     * @param gameTable a GameTable with all match info.
     * @param isSetUpPhase a boolean saying if the check was made in the setup phase or in the match.
     */
    private static void checkPlayers(Server server, GameTable gameTable, boolean isSetUpPhase) {
        if (server.getActivePlayers().size() < MINIMUM_CONNECTED_USERS_THRESHOLD) {
            if (isSetUpPhase) {
                System.out.println("Too many players have disconnected. Restarting server...");
                for (Player player :  gameTable.getPlayers()) {
                    if (server.isConnected(player)) {
                        try {
                            server.sendMessage(player,"Too many players have disconnected. Restarting server...");
                        } catch (UnavailableUserException e) {
                            e.printStackTrace();
                        }
                    }
                }
                System.exit(0);
            } else {
                System.out.println("Too many players have disconnected. Ending game...");
                for (Player player : gameTable.getPlayers()) {
                    if (server.isConnected(player)) {
                        try {
                            server.sendMessage(player,"Too many players have disconnected. Ending game...");
                        } catch (UnavailableUserException e) {
                            e.printStackTrace();
                        }
                    }
                }
                gameOver(server, gameTable);
            }
        }
    }

    /**
     * This method runs all last operations before considering a match over.
     *
     * @param gameTable a GameTable object that holds all match information.
     */
    private static void gameOver(Server server, GameTable gameTable) {

        calculateFinalPoints(gameTable);

        String finalScoringMessage = new String();

        //announce scoreboard
        System.out.println("SCOREBOARD:");
        finalScoringMessage = "SCOREBOARD: ";

        Player winner = gameTable.getPlayers().get(0);
        for (Player player : gameTable.getPlayers()) {
            if (player.getPoints() > winner.getPoints())
                winner = player;
            System.out.println("\n" + player.getUsername() + ": " + player.getPoints() + " points");
            finalScoringMessage = finalScoringMessage + "\n" + player.getUsername() + ": " + player.getPoints() + " points";
        }
        System.out.println("\nThe winner is " + winner.getUsername() + "!");
        finalScoringMessage = finalScoringMessage + "\nThe winner is " + winner.getUsername() + "!";
        for (Player player : gameTable.getPlayers()) {
            if (server.isConnected(player)) {
                try {
                    server.sendMessage(player, finalScoringMessage);
                } catch (UnavailableUserException e) {}
            }
        }
        System.exit(0);
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
            ArrayList<Long> sortedKeys = new ArrayList<>(killsByPlayer.keySet().stream().sorted().collect(Collectors.toCollection(ArrayList::new)));

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