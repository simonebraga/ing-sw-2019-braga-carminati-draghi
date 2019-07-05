package it.polimi.ingsw.view.gui;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.CustomStream;
import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.smartmodel.*;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.ViewInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class contains the graphic implementation of the view to use the application
 */
public class GuiMain extends Application implements ViewInterface {

    private static final double DEFAULT_HEIGHT = 720.0;
    private static final double TOP_BAR_DEFAULT_HEIGHT = 36.0;
    private static final double BOTTOM_BAR_DEFAULT_HEIGHT = 16.0;
    private static final double WIDTH_HEIGHT_RATIO = 320.0/167;
    private static final double ABSOLUTE_SPACING = 10.0;
    private static final double SPACING_SCALING = 72.0;
    private static final double MEDIUM_BUTTON_WIDTH_RATIO = 1.0 / 8;
    private static final double LITTLE_BUTTON_WIDTH_RATIO = 1.0 / 10;
    private static final double BIG_BUTTON_WIDTH_RATIO = 1.0 / 6;
    private static final double POWERUP_HEIGHT_WIDTH_RATIO = 1.56;
    private static final double WEAPON_HEIGHT_WIDTH_RATIO = 1.7;
    private static final double MAPICON_HEIGHT_WIDTH_RATIO = 1.0 / 1.3;
    private static final int TEXT_SIZE = 20;
    private static final Color EVENT_COLOR = Color.FIREBRICK;
    private static final String TITLE = "Adrenaline";
    private static final String TEXT_FONT = "Tahoma";
    private static final String NETWORK_SETTINGS = "network_settings.properties";
    private static final String GRAPHIC_SETTINGS = "graphics/references.properties";
    private static final String LOGOUT_BUTTON_TEXT = "Logout";
    private static final String SHOWTABLE_BUTTON_TEXT = "Show table";
    private static final String SHOWREQUEST_BUTTON_TEXT = "Show request";
    private static final String WELCOME_TEXT = "Welcome to Adrenaline!";
    private static final String RMI_BUTTON_TEXT = "RMI";
    private static final String SOCKET_BUTTON_TEXT = "Socket";
    private static final String LOGIN_BUTTON_TEXT = "Login";
    private static final String STARTWAIT_TEXT = "Waiting for the game to start";
    private static final String LOGOUTWAIT_TEXT = "Logging out...";
    private static final String LOGIN_OUTCOME_1 = "Nickname already chosen";
    private static final String LOGIN_OUTCOME_3 = "Nickname already logged in";
    private static final String LOGIN_OUTCOME_4 = "Nickname not registered";
    private static final String LOGIN_OUTCOME_DEFAULT = "Something very bad went wrong";
    private static final String LOGIN_OUTCOME_FAILED = "Server not available";
    private static final String SELECT_BUTTON_TEXT = "Select";
    private static final String MAP_SELECTION_TEXT = "Select the map";
    private static final String GAMEMODE_SELECTION_TEXT = "Select the game mode";
    private static final String NORMALMODE_BUTTON_TEXT = "Normal";
    private static final String DOMINATIONMODE_BUTTON_TEXT = "Domination";
    private static final String BOOLEAN_BUTTON_TRUE = "Yes";
    private static final String BOOLEAN_BUTTON_FALSE = "No";

    private double height;
    private double width;
    private double spacing;
    private Stage primaryStage;
    private Scene primaryScene;
    private StackPane rootPane;
    private StackPane requestPane;
    private Pane gamePane;
    private Client client;
    private String nickname;
    private Text textEvent;
    private AtomicInteger currentScenario;
    private AtomicBoolean pendingRequest;
    private SmartModel smartModel;
    private Properties properties;
    private Gson gson;
    private static String serverIp;
    private static String clientIp;
    private static double staticHeight;

    /**
     * @return a StackPane containing a button that allows the user to logout
     */
    private StackPane getTopBar() {
        Button buttonLogout = new Button(LOGOUT_BUTTON_TEXT);
        buttonLogout.setOnAction(behavior -> {
            setLogoutScenario();
            client.logout();
        });
        AnchorPane.setTopAnchor(buttonLogout,ABSOLUTE_SPACING);
        AnchorPane.setRightAnchor(buttonLogout,ABSOLUTE_SPACING);
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(buttonLogout);

        return new StackPane(anchorPane);
    }

    /**
     * @return a top bat with also a button that allows the user to switch to the map scenario
     */
    private StackPane getTopBarLinkedMap() {
        Button buttonLogout = new Button(LOGOUT_BUTTON_TEXT);
        buttonLogout.setOnAction(behavior -> {
            setLogoutScenario();
            client.logout();
        });
        AnchorPane.setTopAnchor(buttonLogout,ABSOLUTE_SPACING);
        AnchorPane.setRightAnchor(buttonLogout,ABSOLUTE_SPACING);

        Button buttonGameMap = new Button(SHOWTABLE_BUTTON_TEXT);
        buttonGameMap.setOnAction(behavior -> {
            setGameMapScenario();
        });
        AnchorPane.setTopAnchor(buttonGameMap,ABSOLUTE_SPACING);
        AnchorPane.setLeftAnchor(buttonGameMap,ABSOLUTE_SPACING);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(buttonLogout,buttonGameMap);

        return new StackPane(anchorPane);
    }

    /**
     * @return a top bat with also a button that allows the user to switch to the request scenario
     */
    private StackPane getTopBarLinkedRequest() {
        Button buttonLogout = new Button(LOGOUT_BUTTON_TEXT);
        buttonLogout.setOnAction(behavior -> {
            setLogoutScenario();
            client.logout();
        });
        AnchorPane.setTopAnchor(buttonLogout,ABSOLUTE_SPACING);
        AnchorPane.setRightAnchor(buttonLogout,ABSOLUTE_SPACING);

        Button buttonRequest = new Button(SHOWREQUEST_BUTTON_TEXT);
        buttonRequest.setOnAction(behavior -> {
            setRequestScenario();
        });
        AnchorPane.setTopAnchor(buttonRequest,ABSOLUTE_SPACING);
        AnchorPane.setLeftAnchor(buttonRequest,ABSOLUTE_SPACING);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().addAll(buttonLogout,buttonRequest);

        return new StackPane(anchorPane);
    }

    /**
     * @return a StackPane containing the notifications from the server
     */
    private StackPane getBottomBar() {
        return new StackPane(textEvent);
    }

    /**
     * This method sets the game pane with all the useful information to the user
     * @throws Exception if something goes wrong with the smartmodel parsing
     */
    private void setupGamePane() throws Exception {
        cleanGamePane();

        double mapWidth = width * 0.5 - spacing;
        double mapHeight = (height - 36 - 16) * 0.73 - spacing;
        double mapOffsetX = spacing;
        double mapOffsetY = spacing;

        double myBoardWidth = width * 0.5 - spacing;
        double myBoardHeight = (height - 36 - 16) * 0.27 - spacing;
        double myBoardOffsetX = spacing;
        double myBoardOffsetY = (height - 36 - 16) * 0.73 + spacing;

        double playerBoardWidth = (width * 0.5 * 0.65) - spacing;
        double playerBoardHeight = (height - 36 - 16) * 0.73 * 0.25 - spacing;
        double playerBoardOffsetX = width * 0.5 + spacing;
        double playerBoardOffsetY = spacing;

        double myWeaponWidth = ((width * 0.5 - spacing) / 6) - spacing;
        double myWeaponHeight = myBoardHeight;
        double myWeaponOffsetX = playerBoardOffsetX;
        double myWeaponOffsetY = myBoardOffsetY;

        double myPowerupWidth = myWeaponWidth;
        double myPowerupHeight = myWeaponHeight;
        double myPowerupOffsetX = myWeaponOffsetX;
        double myPowerupOffsetY = myWeaponOffsetY;

        double playerWeaponWidth = (width * 0.5 * 0.35 - spacing) / 3 - spacing;
        double playerWeaponHeight = playerBoardHeight;
        double playerWeaponOffsetX = (width * 0.5 * 1.65) + spacing;
        double playerWeaponOffsetY = spacing;

        double mapIconHeight = (1318.0 / 1931) * mapHeight;
        double mapIconWidth = (1733.0 / 2551) * mapWidth;
        double mapIconOffsetX = (414.0 / 2551) * mapWidth + spacing;
        double mapIconOffsetY = (402.0 / 1931) * mapHeight + spacing;

        double squareHeight = mapIconWidth / 4;
        double squareWidth = mapIconHeight / 3;

        double tileHeight = (mapIconWidth / 4) / 3;
        double tileWidth = tileHeight;

        double figureHeight = tileHeight;
        double figureWidth = figureHeight;

        double weaponWidth = (820.0 / (3 * 2551)) * mapWidth;
        double weaponHeight = weaponWidth * 1.7;

        Pane collectorPane = new Pane();

        // Setup the map
        ImagePane imagePaneMap = new ImagePane(properties.getProperty("mapsRoot").concat(properties.getProperty("map" + (smartModel.getMapIndex() + 1))));
        imagePaneMap.setPrefHeight(mapHeight);
        imagePaneMap.setPrefWidth(mapWidth);
        imagePaneMap.setLayoutX(mapOffsetX);
        imagePaneMap.setLayoutY(mapOffsetY);
        collectorPane.getChildren().add(imagePaneMap);

        // Setup the tiles
        for (SmartTile smartTile : smartModel.getMapTiles()) {
            ImagePane imagePaneTile = new ImagePane(properties.getProperty("tilesRoot").concat(properties.getProperty(getTileFileName(smartTile))));
            imagePaneTile.setPrefHeight(tileHeight);
            imagePaneTile.setPrefWidth(tileWidth);
            imagePaneTile.setLayoutX(mapIconOffsetX + smartTile.getPosY() * (squareWidth) + squareWidth * 0.15);
            imagePaneTile.setLayoutY(mapIconOffsetY + smartTile.getPosX() * (squareHeight) + squareHeight * 0.15);
            collectorPane.getChildren().add(imagePaneTile);
        }

        // Setup blue spawn weapons
        int weaponSpawnCounter = 0;
        for (WeaponName weaponName : smartModel.getSpawnWeaponMap().get(it.polimi.ingsw.model.enumeratedclasses.Color.BLUE)) {
            double weaponOffsetX = (1344.0 / 2551) * mapWidth + spacing;
            double weaponOffsetY = (375.0 / 1931) * mapHeight + spacing - weaponHeight;

            ImagePane imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + weaponName.toString())));
            imagePaneWeapon.setPrefHeight(weaponHeight);
            imagePaneWeapon.setPrefWidth(weaponWidth);
            imagePaneWeapon.setLayoutX(weaponOffsetX + weaponWidth * weaponSpawnCounter);
            imagePaneWeapon.setLayoutY(weaponOffsetY);
            collectorPane.getChildren().add(imagePaneWeapon);
            weaponSpawnCounter++;
        }

        // Setup red spawn weapons
        weaponSpawnCounter = 0;
        for (WeaponName weaponName : smartModel.getSpawnWeaponMap().get(it.polimi.ingsw.model.enumeratedclasses.Color.RED)) {
            double weaponOffsetX = (320.0 / 2551) * mapWidth + spacing - weaponWidth;
            double weaponOffsetY = (698.0 / 1931) * mapHeight + spacing;

            ImagePane imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + weaponName.toString())));
            imagePaneWeapon.setPrefHeight(weaponHeight);
            imagePaneWeapon.setPrefWidth(weaponWidth);
            imagePaneWeapon.setLayoutX(weaponOffsetX);
            imagePaneWeapon.setLayoutY(weaponOffsetY + weaponSpawnCounter * weaponWidth);
            collectorPane.getChildren().add(imagePaneWeapon);
            weaponSpawnCounter++;
        }

        // Setup yellow spawn weapons
        weaponSpawnCounter = 0;
        for (WeaponName weaponName : smartModel.getSpawnWeaponMap().get(it.polimi.ingsw.model.enumeratedclasses.Color.YELLOW)) {
            double weaponOffsetX = (2240.0 / 2551) * mapWidth + spacing;
            double weaponOffsetY = (950.0 / 1931) * mapHeight + spacing;

            ImagePane imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + weaponName.toString())));
            imagePaneWeapon.setPrefHeight(weaponHeight);
            imagePaneWeapon.setPrefWidth(weaponWidth);
            imagePaneWeapon.setLayoutX(weaponOffsetX);
            imagePaneWeapon.setLayoutY(weaponOffsetY + weaponSpawnCounter * weaponWidth);
            collectorPane.getChildren().add(imagePaneWeapon);
            weaponSpawnCounter++;
        }

        if (!smartModel.getDomination()) {

            // Setup killshot track in normal mode
            int figureCounter = 0;
            for (Figure figure : smartModel.getKillshotTrack()) {
                ImagePane imagePaneFigure = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + figure.toString())));
                imagePaneFigure.setPrefHeight((128.0 / 1931) * mapHeight);
                imagePaneFigure.setPrefWidth((854.0 / 2551) * mapWidth / 8);
                imagePaneFigure.setLayoutX(mapOffsetX + (182.0 / 2551) * mapWidth + figureCounter * (854.0 / 2551) * mapWidth / 8 / 2);
                imagePaneFigure.setLayoutY(mapOffsetY + (103.0 / 1931) * mapHeight);
                collectorPane.getChildren().add(imagePaneFigure);
                figureCounter++;
            }

            // Setup frenxy counter in normal mode
            for (int i = 0; i < smartModel.getKillCount() ; i++) {
                ImagePane imagePaneSkull = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blobSKULL")));
                imagePaneSkull.setPrefHeight((128.0 / 1931) * mapHeight);
                imagePaneSkull.setPrefWidth((854.0 / 2551) * mapWidth / 8);
                imagePaneSkull.setLayoutX(mapOffsetX + (182.0 / 2551) * mapWidth + (854.0 / 2551) * mapWidth * 7 / 8 - i * (854.0 / 2551) * mapWidth / 8);
                imagePaneSkull.setLayoutY(mapOffsetY + (103.0 / 1931) * mapHeight);
                collectorPane.getChildren().add(imagePaneSkull);
            }

        } else {

            double dominationBoardHeight = (364.0 / 1931) * mapHeight;
            double dominationBoardWidth = (1124.0 / 2551) * mapWidth;
            double dominationBoardOffsetX = mapOffsetX + (163.0 / 2551) * mapWidth;
            double dominationBoardOffsetY = mapOffsetY + (7.0 / 1931) * mapHeight;
            double dominationBlobHeight = (78.0 / 364) * dominationBoardHeight;
            double dominationBlobWidth = (452.0 / 1124 / 7) * dominationBoardWidth;
            double dominationKilltrackOffsetX = mapOffsetX + (1010.0 / 2551) * mapWidth;
            double dominationKilltrackOffsetY = mapOffsetY + (49.0 / 1931) * mapHeight;

            // Setup domination board
            ImagePane imagePaneDominationBoard = new ImagePane(properties.getProperty("boardDominationRoot").concat(properties.getProperty("boardDomination")));
            imagePaneDominationBoard.setPrefHeight(dominationBoardHeight);
            imagePaneDominationBoard.setPrefWidth(dominationBoardWidth);
            imagePaneDominationBoard.setLayoutX(dominationBoardOffsetX);
            imagePaneDominationBoard.setLayoutY(dominationBoardOffsetY);
            collectorPane.getChildren().add(imagePaneDominationBoard);

            // Setup spawn damage tracks in domination mode
            int colorCounter = 0;
            for (it.polimi.ingsw.model.enumeratedclasses.Color color : smartModel.getSpawnDamageTrack().keySet()) {
                double i = 0;
                double j = 0;
                for (Figure figure : smartModel.getSpawnDamageTrack().get(color)) {
                    ImagePane imagePaneDamagePoint = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + figure.toString())));
                    imagePaneDamagePoint.setPrefHeight(dominationBlobHeight);
                    imagePaneDamagePoint.setPrefWidth(dominationBlobWidth);
                    imagePaneDamagePoint.setLayoutX(mapOffsetX + (179.0 / 2551) * mapWidth + (i + j) * dominationBlobWidth);
                    imagePaneDamagePoint.setLayoutY(mapOffsetY + (29.0 / 1931) * mapHeight + colorCounter * (dominationBlobHeight + (40.0 / 364) * dominationBoardHeight));
                    collectorPane.getChildren().add(imagePaneDamagePoint);
                    if (i < 8)
                        i += 1;
                    else
                        j += 0.3;
                }
                colorCounter++;
            }

            // Setup the killshot track in domination mode
            for (int i = 0 ; (i < smartModel.getKillshotTrack().size() && i < 8) ; i++) {
                ImagePane imagePaneBlob = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + smartModel.getKillshotTrack().get(i).toString())));
                imagePaneBlob.setPrefHeight(dominationBlobHeight);
                imagePaneBlob.setPrefWidth(dominationBlobWidth);
                imagePaneBlob.setLayoutX(dominationKilltrackOffsetX + i / 2.0 * dominationBlobWidth);
                imagePaneBlob.setLayoutY(dominationKilltrackOffsetY);
                collectorPane.getChildren().add(imagePaneBlob);
            } for (int i = 8 ; i < smartModel.getKillshotTrack().size() ; i++) {
                ImagePane imagePaneBlob = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + smartModel.getKillshotTrack().get(i).toString())));
                imagePaneBlob.setPrefHeight(dominationBlobHeight);
                imagePaneBlob.setPrefWidth(dominationBlobWidth);
                imagePaneBlob.setLayoutX(dominationKilltrackOffsetX + (i - 8) / 2.0 * dominationBlobWidth);
                imagePaneBlob.setLayoutY(dominationKilltrackOffsetY + dominationBlobHeight);
                collectorPane.getChildren().add(imagePaneBlob);
            }

            // Setup the frenzy counter track in domination mode
            for (int i = 0 ; (i < smartModel.getKillCount() && i < 4) ; i++) {
                ImagePane imagePaneSkull = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blobSKULL")));
                imagePaneSkull.setPrefHeight(dominationBlobHeight);
                imagePaneSkull.setPrefWidth(dominationBlobWidth);
                imagePaneSkull.setLayoutX(dominationKilltrackOffsetX + 3 * dominationBlobWidth - i * dominationBlobWidth);
                imagePaneSkull.setLayoutY(dominationKilltrackOffsetY + dominationBlobHeight);
                collectorPane.getChildren().add(imagePaneSkull);
            } for (int i = 4 ; i < smartModel.getKillCount() ; i++) {
                ImagePane imagePaneSkull = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blobSKULL")));
                imagePaneSkull.setPrefHeight(dominationBlobHeight);
                imagePaneSkull.setPrefWidth(dominationBlobWidth);
                imagePaneSkull.setLayoutX(dominationKilltrackOffsetX + 3 * dominationBlobWidth - (i - 4) * dominationBlobWidth);
                imagePaneSkull.setLayoutY(dominationKilltrackOffsetY);
                collectorPane.getChildren().add(imagePaneSkull);
            }

        }

        // Setup the players informations
        int playerCounter = 0;
        int playerTotalCounter = 0;
        for (String nickname : smartModel.getSmartPlayerMap().keySet()) {

            SmartPlayer smartPlayer = smartModel.getSmartPlayerMap().get(nickname);

            if (nickname.equals(this.nickname)) {

                // Setup principal player board
                ImagePane imagePaneMyBoard;
                if (!smartPlayer.isHasReverseBoard())
                    imagePaneMyBoard = new ImagePane(properties.getProperty("boardsRoot").concat(properties.getProperty("board" + smartPlayer.getFigure().toString())));
                else
                    imagePaneMyBoard = new ImagePane(properties.getProperty("boardsRoot").concat(properties.getProperty("board" + smartPlayer.getFigure().toString()  + "reverse")));
                imagePaneMyBoard.setPrefHeight(myBoardHeight);
                imagePaneMyBoard.setPrefWidth(myBoardWidth);
                imagePaneMyBoard.setLayoutX(myBoardOffsetX);
                imagePaneMyBoard.setLayoutY(myBoardOffsetY);
                collectorPane.getChildren().add(imagePaneMyBoard);

                if (smartModel.isFrenezyMode()) {
                    ImagePane imagePane = new ImagePane(properties.getProperty("boardsRoot").concat(properties.getProperty("sideboard" + smartPlayer.getFigure().toString())));
                    imagePane.setPrefHeight(myBoardHeight * 0.9);
                    imagePane.setPrefWidth((90.0/277) * myBoardHeight * 0.9);
                    imagePane.setLayoutX(myBoardOffsetX);
                    imagePane.setLayoutY(myBoardOffsetY);
                    collectorPane.getChildren().add(imagePane);
                }

                // Setup principal player weapons
                int weaponCounter = 0;
                for (SmartWeapon smartWeapon : smartPlayer.getWeapons()) {
                    ImagePane imagePaneWeapon;
                    if (smartWeapon.getLoaded())
                        imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + smartWeapon.getWeaponName().toString())));
                    else
                        imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weaponBack")));
                    imagePaneWeapon.setPrefHeight(myWeaponHeight);
                    imagePaneWeapon.setPrefWidth(myWeaponWidth);
                    imagePaneWeapon.setLayoutX(myWeaponOffsetX + (spacing + myWeaponWidth) * weaponCounter);
                    imagePaneWeapon.setLayoutY(myWeaponOffsetY);
                    collectorPane.getChildren().add(imagePaneWeapon);

                    weaponCounter++;
                }

                // Setup principal player powerups
                int powerupCounter = 0;
                for (SmartPowerup smartPowerup : smartPlayer.getPowerups()) {
                    ImagePane imagePanePowerup = new ImagePane(properties.getProperty("powerupsRoot").concat(properties.getProperty("powerup" + smartPowerup.getPowerupName().toString() + "_" + smartPowerup.getColor().toString())));
                    imagePanePowerup.setPrefHeight(myPowerupHeight);
                    imagePanePowerup.setPrefWidth(myPowerupWidth);
                    imagePanePowerup.setLayoutX(myPowerupOffsetX + (spacing + myWeaponWidth) * weaponCounter + (spacing + myPowerupWidth) * powerupCounter);
                    imagePanePowerup.setLayoutY(myPowerupOffsetY);
                    collectorPane.getChildren().add(imagePanePowerup);

                    powerupCounter++;
                }

                // Setup principal player damage
                ArrayList<Figure> damage = smartPlayer.getDamage();
                for (int i = 0 ; i < damage.size() ; i++) {
                    ImagePane imagePaneDamagePoint = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + damage.get(i).toString())));
                    imagePaneDamagePoint.setPrefHeight((76.0 / 277) * myBoardHeight);
                    imagePaneDamagePoint.setPrefWidth((64.0 / 1124) * myBoardWidth);
                    imagePaneDamagePoint.setLayoutX(myBoardOffsetX + (98.0 / 1124) * myBoardWidth + i * (64.0 / 1124) * myBoardWidth);
                    imagePaneDamagePoint.setLayoutY(myBoardOffsetY + (92.0 / 277) * myBoardHeight);
                    collectorPane.getChildren().add(imagePaneDamagePoint);
                }

                // Setup principal player marks
                ArrayList<Figure> marks = new ArrayList<>();
                for (Figure figure : smartPlayer.getMarks().keySet()) {
                    for (int i = 0; i < smartPlayer.getMarks().get(figure) ; i++) {
                        marks.add(figure);
                    }
                }
                for (int i = 0; i < marks.size() ; i++) {
                    ImagePane imagePaneDamagePoint = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + damage.get(i).toString())));
                    imagePaneDamagePoint.setPrefHeight((76.0 / 277) * myBoardHeight);
                    imagePaneDamagePoint.setPrefWidth((64.0 / 1124) * myBoardWidth);
                    imagePaneDamagePoint.setLayoutX(myBoardOffsetX + (543.0 / 1124) * myBoardWidth + i * (64.0 / 1124) * myBoardWidth / 2);
                    imagePaneDamagePoint.setLayoutY(myBoardOffsetY + (57.0 / 277) * myBoardHeight - (76.0 / 277) * myBoardHeight);
                    collectorPane.getChildren().add(imagePaneDamagePoint);
                }

                // Setup principal player ammo
                int colorCounter = 0;
                for (it.polimi.ingsw.model.enumeratedclasses.Color color : smartPlayer.getAmmo().keySet()) {
                    for (int i = 0 ; i < smartPlayer.getAmmo().get(color) ; i++) {
                        ImagePane imagePane = new ImagePane(properties.getProperty("ammoRoot").concat(properties.getProperty("ammo" + color.toString())));
                        imagePane.setPrefHeight((54.0 / 277) * myBoardHeight);
                        imagePane.setPrefWidth((54.0 / 1124) * myBoardWidth);
                        imagePane.setLayoutX(myBoardOffsetX + (916.0 / 1124) * myBoardWidth + i * (54.0 / 1124) * myBoardWidth);
                        imagePane.setLayoutY(myBoardOffsetY + (48.0 / 277) * myBoardHeight + colorCounter * (54.0 / 277) * myBoardHeight);
                        collectorPane.getChildren().add(imagePane);
                    }
                    colorCounter++;
                }

                // Setup principal player pointtrack
                double reverseOffset = 0;
                if (smartPlayer.isHasReverseBoard())
                    reverseOffset = (72.0 / 1124) * myBoardWidth;
                System.out.println(smartPlayer.getDeaths());
                for (int i = 0 ; i < smartPlayer.getDeaths() ; i++) {
                    ImagePane imagePaneSkull = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blobSKULL")));
                    imagePaneSkull.setPrefHeight((76.0 / 277) * myBoardHeight);
                    imagePaneSkull.setPrefWidth((360.0 / 6 / 1124) * myBoardWidth);
                    imagePaneSkull.setLayoutX(myBoardOffsetX + (232.0 / 1124) * myBoardWidth + reverseOffset + i * (360.0 / 6 / 1124) * myBoardWidth);
                    imagePaneSkull.setLayoutY(myBoardOffsetY + (176.0 / 277) * myBoardHeight);
                    collectorPane.getChildren().add(imagePaneSkull);
                }

            } else {

                // Setup other players boards
                ImagePane imagePanePlayerBoard;
                if (!smartPlayer.isHasReverseBoard())
                    imagePanePlayerBoard = new ImagePane(properties.getProperty("boardsRoot").concat(properties.getProperty("board" + smartPlayer.getFigure().toString())));
                else
                    imagePanePlayerBoard = new ImagePane(properties.getProperty("boardsRoot").concat(properties.getProperty("board" + smartPlayer.getFigure().toString()  + "reverse")));
                imagePanePlayerBoard.setPrefHeight(playerBoardHeight);
                imagePanePlayerBoard.setPrefWidth(playerBoardWidth);
                imagePanePlayerBoard.setLayoutX(playerBoardOffsetX);
                imagePanePlayerBoard.setLayoutY(playerBoardOffsetY + (playerBoardOffsetY + playerBoardHeight) * playerCounter);
                collectorPane.getChildren().add(imagePanePlayerBoard);

                if (smartModel.isFrenezyMode()) {
                    ImagePane imagePane = new ImagePane(properties.getProperty("boardsRoot").concat(properties.getProperty("sideboard" + smartPlayer.getFigure().toString())));
                    imagePane.setPrefHeight(playerBoardHeight * 0.9);
                    imagePane.setPrefWidth((90.0/277) * playerBoardHeight * 0.9);
                    imagePane.setLayoutX(playerBoardOffsetX);
                    imagePane.setLayoutY(playerBoardOffsetY + (playerBoardOffsetY + playerBoardHeight) * playerCounter);
                    collectorPane.getChildren().add(imagePane);
                }

                // Setup other players weapons
                int weaponCounter = 0;
                for (SmartWeapon smartWeapon : smartPlayer.getWeapons()) {
                    ImagePane imagePaneWeapon;
                    if (smartWeapon.getLoaded())
                        imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + smartWeapon.getWeaponName().toString())));
                    else
                        imagePaneWeapon = new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weaponBack")));
                    imagePaneWeapon.setPrefHeight(playerWeaponHeight);
                    imagePaneWeapon.setPrefWidth(playerWeaponWidth);
                    imagePaneWeapon.setLayoutX(playerWeaponOffsetX + (playerWeaponWidth + spacing) * weaponCounter);
                    imagePaneWeapon.setLayoutY(playerWeaponOffsetY + (playerWeaponHeight + spacing) * playerCounter);
                    collectorPane.getChildren().add(imagePaneWeapon);
                    weaponCounter++;
                }

                // Setup other players damage
                ArrayList<Figure> damage = smartPlayer.getDamage();
                for (int i = 0 ; i < damage.size() ; i++) {
                    ImagePane imagePaneDamagePoint = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + damage.get(i).toString())));
                    imagePaneDamagePoint.setPrefHeight((76.0 / 277) * playerBoardHeight);
                    imagePaneDamagePoint.setPrefWidth((64.0 / 1124) * playerBoardWidth);
                    imagePaneDamagePoint.setLayoutX(playerBoardOffsetX + (98.0 / 1124) * playerBoardWidth + i * (64.0 / 1124) * playerBoardWidth);
                    imagePaneDamagePoint.setLayoutY(playerBoardOffsetY + (playerBoardOffsetY + playerBoardHeight) * playerCounter + (92.0 / 277) * playerBoardHeight);
                    collectorPane.getChildren().add(imagePaneDamagePoint);
                }

                // Setup other players marks
                ArrayList<Figure> marks = new ArrayList<>();
                for (Figure figure : smartPlayer.getMarks().keySet()) {
                    for (int i = 0; i < smartPlayer.getMarks().get(figure) ; i++) {
                        marks.add(figure);
                    }
                }
                for (int i = 0; i < marks.size() ; i++) {
                    ImagePane imagePaneDamagePoint = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blob" + damage.get(i).toString())));
                    imagePaneDamagePoint.setPrefHeight((76.0 / 277) * playerBoardHeight);
                    imagePaneDamagePoint.setPrefWidth((64.0 / 1124) * playerBoardWidth);
                    imagePaneDamagePoint.setLayoutX(playerBoardOffsetX + (543.0 / 1124) * playerBoardWidth + i * (64.0 / 1124) * playerBoardWidth / 2);
                    imagePaneDamagePoint.setLayoutY(playerBoardOffsetY + (playerBoardOffsetY + playerBoardHeight) * playerCounter + (57.0 / 277) * playerBoardHeight - (76.0 / 277) * playerBoardHeight);
                    collectorPane.getChildren().add(imagePaneDamagePoint);
                }

                // Setup other players ammo
                int colorCounter = 0;
                for (it.polimi.ingsw.model.enumeratedclasses.Color color : smartPlayer.getAmmo().keySet()) {
                    for (int i = 0 ; i < smartPlayer.getAmmo().get(color) ; i++) {
                        ImagePane imagePane = new ImagePane(properties.getProperty("ammoRoot").concat(properties.getProperty("ammo" + color.toString())));
                        imagePane.setPrefHeight((54.0 / 277) * playerBoardHeight);
                        imagePane.setPrefWidth((54.0 / 1124) * playerBoardWidth);
                        imagePane.setLayoutX(playerBoardOffsetX + (916.0 / 1124) * playerBoardWidth + i * (54.0 / 1124) * playerBoardWidth);
                        imagePane.setLayoutY(playerBoardOffsetY + (playerBoardOffsetY + playerBoardHeight) * playerCounter + (48.0 / 277) * playerBoardHeight + colorCounter * (54.0 / 277) * playerBoardHeight);
                        collectorPane.getChildren().add(imagePane);
                    }
                    colorCounter++;
                }

                // Setup other players pointtrack
                double reverseOffset = 0;
                if (smartPlayer.isHasReverseBoard())
                    reverseOffset = (72.0 / 1124) * playerBoardWidth;
                System.out.println(smartPlayer.getDeaths());
                for (int i = 0 ; i < smartPlayer.getDeaths() ; i++) {
                    ImagePane imagePaneSkull = new ImagePane(properties.getProperty("blobRoot").concat(properties.getProperty("blobSKULL")));
                    imagePaneSkull.setPrefHeight((76.0 / 277) * playerBoardHeight);
                    imagePaneSkull.setPrefWidth((360.0 / 6 / 1124) * playerBoardWidth);
                    imagePaneSkull.setLayoutX(playerBoardOffsetX + (232.0 / 1124) * playerBoardWidth + reverseOffset + i * (360.0 / 6 / 1124) * playerBoardWidth);
                    imagePaneSkull.setLayoutY(playerBoardOffsetY + (playerBoardOffsetY + playerBoardHeight) * playerCounter + (176.0 / 277) * playerBoardHeight);
                    collectorPane.getChildren().add(imagePaneSkull);
                }

                playerCounter++;
            }

            // Setup players positions on the map
            ImagePane imagePaneFigure = new ImagePane(properties.getProperty("figuresRoot").concat(properties.getProperty("figure" + smartPlayer.getFigure().toString())));
            if ((smartPlayer.getPosX() >= 0) && (smartPlayer.getPosY() >= 0)) {

                imagePaneFigure.setPrefHeight(figureHeight);
                imagePaneFigure.setPrefWidth(figureWidth);
                imagePaneFigure.setLayoutX(mapIconOffsetX + smartPlayer.getPosY() * (squareWidth) + squareWidth * 0.1 + spacing * 1.4 * playerTotalCounter);
                imagePaneFigure.setLayoutY(mapIconOffsetY + smartPlayer.getPosX() * (squareHeight) + squareHeight * 0.55);
                collectorPane.getChildren().add(imagePaneFigure);
                playerTotalCounter++;
            }

        }

        Platform.runLater(() -> gamePane.getChildren().add(collectorPane));
    }

    /**
     * This method is used to get the correct file name of a given tile
     * @param smartTile is the tile to be parsed
     * @return the correct file name
     */
    private String getTileFileName(SmartTile smartTile) {
        String fileName = "tile";
        for (it.polimi.ingsw.model.enumeratedclasses.Color color : smartTile.getAmmo())
            if (Objects.equals(color.toString(), "RED"))
                fileName += "R";
        for (it.polimi.ingsw.model.enumeratedclasses.Color color : smartTile.getAmmo())
            if (Objects.equals(color.toString(), "BLUE"))
                fileName += "B";
        for (it.polimi.ingsw.model.enumeratedclasses.Color color : smartTile.getAmmo())
            if (Objects.equals(color.toString(), "YELLOW"))
                fileName += "Y";
        for (int i = 0 ; i < smartTile.getPowerup() ; i++)
            fileName += "P";
        return fileName;
    }

    /**
     * This method is used to reset the root pane
     */
    private void setCleanScenario() {
        Platform.runLater(() -> rootPane.getChildren().clear());
    }

    /**
     * This method is used to reset the pane that contain the request from the server
     */
    private void cleanRequestPane() {
        pendingRequest.set(false);
        Platform.runLater(() -> requestPane.getChildren().clear());
    }

    /**
     * This method is used to reset the pane that contains the game map
     */
    private void cleanGamePane() {
        Platform.runLater(() -> gamePane.getChildren().clear());
    }

    /**
     * This method is used to set a scenario where the used ca select the info to login and start the game
     */
    private void setLoginScenario() {
        currentScenario.set(1);

        Text textWelcome = new Text(WELCOME_TEXT);
        textWelcome.setFont(Font.font(TEXT_FONT, FontWeight.NORMAL,TEXT_SIZE));

        TextField textFieldNickname = new TextField();

        AtomicInteger networkType = new AtomicInteger();
        ToggleGroup toggleGroupNetworkChoice = new ToggleGroup();
        toggleGroupNetworkChoice.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> obsValue, Toggle oldToggle, Toggle newToggle) -> networkType.set((int) toggleGroupNetworkChoice.getSelectedToggle().getUserData()));

        RadioButton radioButtonRMI = new RadioButton(RMI_BUTTON_TEXT);
        radioButtonRMI.setUserData(0);
        radioButtonRMI.setToggleGroup(toggleGroupNetworkChoice);
        radioButtonRMI.setSelected(true);
        RadioButton radioButtonSocket = new RadioButton(SOCKET_BUTTON_TEXT);
        radioButtonSocket.setUserData(1);
        radioButtonSocket.setToggleGroup(toggleGroupNetworkChoice);

        HBox hBoxNetworkChoice = new HBox(radioButtonRMI,radioButtonSocket);
        hBoxNetworkChoice.setSpacing(ABSOLUTE_SPACING);
        hBoxNetworkChoice.setAlignment(Pos.CENTER);

        Button buttonLogin = new Button(LOGIN_BUTTON_TEXT);
        buttonLogin.setOnAction(behavior -> tryLogin(networkType.get(),textFieldNickname.getText()));

        HBox hBoxTextEvent = new HBox(textEvent);
        hBoxTextEvent.setAlignment(Pos.CENTER);

        GridPane gridPaneLogin = new GridPane();
        gridPaneLogin.setAlignment(Pos.CENTER);
        gridPaneLogin.setVgap(ABSOLUTE_SPACING);
        gridPaneLogin.setHgap(ABSOLUTE_SPACING);
        gridPaneLogin.add(textWelcome,0,0,3,1);
        gridPaneLogin.add(textFieldNickname,0,1,2,1);
        gridPaneLogin.add(buttonLogin,2,1);
        gridPaneLogin.add(hBoxNetworkChoice,0,2,3,1);
        gridPaneLogin.add(hBoxTextEvent,0,3,3,1);

        setCleanScenario();
        Platform.runLater(() -> textEvent.setText(""));
        Platform.runLater(() -> rootPane.getChildren().add(gridPaneLogin));
    }

    /**
     * This method is used to show a text that displays that the game is aboout to start
     */
    private void setStartWaitScenario() {
        currentScenario.set(2);

        Text textWait = new Text(STARTWAIT_TEXT);
        textWait.setFont(Font.font(TEXT_FONT,FontWeight.NORMAL,TEXT_SIZE));

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(textWait);
        borderPane.setTop(getTopBar());
        borderPane.setBottom(getBottomBar());

        setCleanScenario();
        Platform.runLater(() -> textEvent.setText(""));
        Platform.runLater(() -> rootPane.getChildren().add(borderPane));
    }

    /**
     * This method is used to set the scenario that contains the game map
     */
    private void setGameMapScenario() {
        currentScenario.set(3);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gamePane);
        borderPane.setBottom(getBottomBar());
        if (pendingRequest.get())
            borderPane.setTop(getTopBarLinkedRequest());
        else
            borderPane.setTop(getTopBar());

        setCleanScenario();
        Platform.runLater(() -> textEvent.setText(""));
        Platform.runLater(() -> rootPane.getChildren().add(borderPane));
    }

    /**
     * This method is used to show a text that displays that the logout is being performed
     */
    private void setLogoutScenario() {
        currentScenario.set(4);

        Text textWait = new Text(LOGOUTWAIT_TEXT);
        textWait.setFont(Font.font(TEXT_FONT,FontWeight.NORMAL,TEXT_SIZE));

        setCleanScenario();
        Platform.runLater(() -> textEvent.setText(""));
        Platform.runLater(() -> rootPane.getChildren().add(textWait));
    }

    /**
     * This method is used to set the scenario that contains the last request from the server
     */
    private void setRequestScenario() {
        currentScenario.set(5);
        pendingRequest.set(true);

        setCleanScenario();
        Platform.runLater(() -> rootPane.getChildren().add(requestPane));
    }

    /**
     * This method is used to create a new Client instance and to login with the specified network type and nickname
     * @param networkType This parameter determines which type of connection must be used
     *          0 - RMI
     *          1 - Socket
     * @param nickname is the nickname used to login
     */
    private void tryLogin(int networkType, String nickname) {
        try {
            client = new Client(networkType,this);
            this.nickname = nickname;
            int retVal = client.login(nickname);
            switch (retVal) {
                case 0: {
                    setStartWaitScenario();
                    break;
                }
                case 1: {
                    Platform.runLater(() -> textEvent.setText(LOGIN_OUTCOME_1));
                    break;
                }
                case 2: {
                    smartModel = client.getModelUpdate();
                    if (smartModel != null) {
                        try {
                            setupGamePane();
                            setGameMapScenario();
                        } catch (Exception e) {
                            client.logout();
                        }
                    } else setStartWaitScenario();
                    break;
                }
                case 3: {
                    Platform.runLater(() -> textEvent.setText(LOGIN_OUTCOME_3));
                    break;
                }
                case 4: {
                    Platform.runLater(() -> textEvent.setText(LOGIN_OUTCOME_4));
                    break;
                }
                default:
                    Platform.runLater(() -> textEvent.setText(LOGIN_OUTCOME_DEFAULT));
            }
        } catch (Exception e) {
            Platform.runLater(() -> textEvent.setText(LOGIN_OUTCOME_FAILED));
        }
    }

    /**
     * This method is used to display a message opening a new windows
     * @param s is the message to be shown
     */
    private void popup(String s) {
        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(ABSOLUTE_SPACING,ABSOLUTE_SPACING,ABSOLUTE_SPACING,ABSOLUTE_SPACING));

        Text textPopup = new Text(s);
        textPopup.setFont(Font.font(TEXT_FONT,FontWeight.NORMAL,TEXT_SIZE));
        stackPane.getChildren().add(textPopup);


        Scene scene = new Scene(stackPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        if (args[0] != null)
            serverIp = args[0];
        else
            serverIp = null;
        if (args[1] != null)
            clientIp = args[1];
        else
            clientIp = null;
        if (args[2] != null) {
            staticHeight = Integer.parseInt(args[2]);
        }
        else
            staticHeight = 0;
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        properties = new Properties();
        try {
            properties.load(Objects.requireNonNull(Client.class.getClassLoader().getResourceAsStream(GRAPHIC_SETTINGS)));
        } catch (Exception e) {
            System.err.println("Error reading " + GRAPHIC_SETTINGS);
            throw new Exception();
        }

        Properties networkProperties = new Properties();
        try {
            networkProperties.load(Objects.requireNonNull(Client.class.getClassLoader().getResourceAsStream(NETWORK_SETTINGS)));
        } catch (Exception e) {
            System.err.println("Error reading " + NETWORK_SETTINGS);
            throw new Exception();
        }

        if (serverIp == null)
            serverIp = networkProperties.getProperty("serverIp");
        if (clientIp == null)
            clientIp = networkProperties.getProperty("clientIp");
        if (staticHeight > 0)
            this.height = staticHeight;
        else
            this.height = DEFAULT_HEIGHT;
        this.width = (this.height - TOP_BAR_DEFAULT_HEIGHT - BOTTOM_BAR_DEFAULT_HEIGHT) * WIDTH_HEIGHT_RATIO;
        this.spacing = this.height / SPACING_SCALING;
        this.textEvent = new Text();
        this.textEvent.setFill(EVENT_COLOR);
        this.currentScenario = new AtomicInteger();
        this.pendingRequest = new AtomicBoolean();
        this.gson = new Gson();
        this.rootPane = new StackPane();
        this.requestPane = new StackPane();
        this.gamePane = new Pane();
        this.smartModel = null;
        this.primaryScene = new Scene(this.rootPane,this.width,this.height);
        this.primaryStage = stage;
        this.primaryStage.setScene(this.primaryScene);
        this.primaryStage.setTitle(TITLE);
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
        setLoginScenario();
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    @Override
    public void logout() {
        if (currentScenario.get() != 1)
            Platform.runLater(this::setLoginScenario);
        Platform.runLater(this::cleanRequestPane);
        client = null;
    }

    @Override
    public void sendMessage(String s) {
        Platform.runLater(() -> popup(s));
    }

    @Override
    public void notifyEvent(String s) {
        Platform.runLater(() -> textEvent.setText(s));
    }

    @Override
    public int choosePlayer(Figure[] f) {
        CustomStream customStream = new CustomStream();

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(ABSOLUTE_SPACING);

        for (int i = 0 ; i < f.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("figuresRoot").concat(properties.getProperty("figure" + f[i].toString())))
            );
            stackPane.setPrefHeight((this.width) * MEDIUM_BUTTON_WIDTH_RATIO);
            stackPane.setPrefWidth((this.width) * MEDIUM_BUTTON_WIDTH_RATIO);

            Button button = new Button();
            button.setGraphic(stackPane);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setGameMapScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(hBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseWeapon(WeaponName[] w) {
        CustomStream customStream = new CustomStream();

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(ABSOLUTE_SPACING);

        for (int i = 0 ; i < w.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + w[i].toString())))
            );
            stackPane.setPrefHeight(((this.width) * MEDIUM_BUTTON_WIDTH_RATIO) * WEAPON_HEIGHT_WIDTH_RATIO);
            stackPane.setPrefWidth(((this.width) * MEDIUM_BUTTON_WIDTH_RATIO));

            Button button = new Button();
            button.setGraphic(stackPane);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setGameMapScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(hBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseString(String[] s) {
        CustomStream customStream = new CustomStream();

        VBox vBoxChoice = new VBox();
        vBoxChoice.setSpacing(ABSOLUTE_SPACING);
        vBoxChoice.setAlignment(Pos.CENTER);

        ListView<String> listView = new ListView<>();
        listView.setPrefHeight(this.height * 0.6);
        listView.setMaxWidth(this.width / 2);
        listView.setMinWidth(this.width / 2);

        ObservableList<String> viewItems = FXCollections.observableArrayList(s);
        listView.setItems(viewItems);

        Button buttonSelect = new Button(SELECT_BUTTON_TEXT);
        buttonSelect.setOnAction(behavior -> {
            String selection = listView.getFocusModel().getFocusedItem();

            for (int i = 0 ; i < s.length ; i++) {
                if (selection.equals(s[i])) {
                    customStream.putLine(Integer.toString(i));
                    cleanRequestPane();
                    setGameMapScenario();
                }
            }
        });

        vBoxChoice.getChildren().addAll(listView,buttonSelect);

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(vBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseDirection(Character[] c) {
        CustomStream customStream = new CustomStream();

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(ABSOLUTE_SPACING);

        for (int i = 0 ; i < c.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("directionsRoot").concat(properties.getProperty("direction_" + c[i].toString().toLowerCase())))
            );
            stackPane.setPrefHeight(this.width * LITTLE_BUTTON_WIDTH_RATIO);
            stackPane.setPrefWidth(this.width * LITTLE_BUTTON_WIDTH_RATIO);

            Button button = new Button();
            button.setGraphic(stackPane);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setGameMapScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(hBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseColor(it.polimi.ingsw.model.enumeratedclasses.Color[] c) {
        CustomStream customStream = new CustomStream();

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(ABSOLUTE_SPACING);

        for (int i = 0 ; i < c.length ; i++) {

            Button button = new Button();
            button.setStyle("-fx-background-color: " + c[i].toString() + ";");
            button.setPrefHeight(this.width * MEDIUM_BUTTON_WIDTH_RATIO);
            button.setPrefWidth(this.width * MEDIUM_BUTTON_WIDTH_RATIO);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setGameMapScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(hBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int choosePowerup(Powerup[] p) {
        CustomStream customStream = new CustomStream();

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(ABSOLUTE_SPACING);

        for (int i = 0 ; i < p.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("powerupsRoot").concat(properties.getProperty("powerup" + p[i].getName().toString() + "_" + p[i].getColor().toString())))
            );
            stackPane.setPrefHeight(((this.width) * MEDIUM_BUTTON_WIDTH_RATIO) * POWERUP_HEIGHT_WIDTH_RATIO);
            stackPane.setPrefWidth(((this.width) * MEDIUM_BUTTON_WIDTH_RATIO));

            Button button = new Button();
            button.setGraphic(stackPane);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setGameMapScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(hBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseMap(int[] m) {
        CustomStream customStream = new CustomStream();

        Text textQuestion = new Text(MAP_SELECTION_TEXT);
        textQuestion.setFont(Font.font(TEXT_FONT,FontWeight.NORMAL,TEXT_SIZE));

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(ABSOLUTE_SPACING);

        for (int i = 0 ; i < m.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("mapsiconsRoot").concat(properties.getProperty("mapicon" + (m[i]+1))))
            );
            stackPane.setPrefHeight(((this.width) * BIG_BUTTON_WIDTH_RATIO) * MAPICON_HEIGHT_WIDTH_RATIO);
            stackPane.setPrefWidth(((this.width) * BIG_BUTTON_WIDTH_RATIO));

            Button button = new Button();
            button.setGraphic(stackPane);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setStartWaitScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        VBox vBoxChoice = new VBox();
        vBoxChoice.setSpacing(ABSOLUTE_SPACING);
        vBoxChoice.setAlignment(Pos.CENTER);
        vBoxChoice.getChildren().addAll(textQuestion,hBoxChoice);

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBar());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(vBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseMode(Character[] c) {
        CustomStream customStream = new CustomStream();

        Text textQuestion = new Text(GAMEMODE_SELECTION_TEXT);
        textQuestion.setFont(Font.font(TEXT_FONT,FontWeight.NORMAL,TEXT_SIZE));

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(ABSOLUTE_SPACING);

        for (int i = 0 ; i < c.length ; i++) {

            Button button = new Button();

            switch (c[i]) {
                case 'N': {
                    button.setText(NORMALMODE_BUTTON_TEXT);
                    break;
                }
                case 'D': {
                    button.setText(DOMINATIONMODE_BUTTON_TEXT);
                    break;
                }
                default:
                    button.setText(c[i].toString());
            }

            button.setPrefWidth(120);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setStartWaitScenario();
            });

            hBoxChoice.getChildren().add(button);
        }

        VBox vBoxChoice = new VBox();
        vBoxChoice.setSpacing(ABSOLUTE_SPACING);
        vBoxChoice.setAlignment(Pos.CENTER);
        vBoxChoice.getChildren().addAll(textQuestion,hBoxChoice);

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBar());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(vBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int chooseSquare(int[][] s) {
        CustomStream customStream = new CustomStream();

        Pane choicePane = new Pane();

        ImagePane imagePaneMap = new ImagePane(properties.getProperty("mapsiconsRoot").concat(properties.getProperty("mapicon" + (smartModel.getMapIndex()+1))));
        imagePaneMap.setPrefHeight((height-TOP_BAR_DEFAULT_HEIGHT-BOTTOM_BAR_DEFAULT_HEIGHT) * 0.7);
        imagePaneMap.setPrefWidth(imagePaneMap.getPrefHeight()/MAPICON_HEIGHT_WIDTH_RATIO);
        imagePaneMap.setLayoutX((width-imagePaneMap.getPrefWidth())/2);
        imagePaneMap.setLayoutY((height-TOP_BAR_DEFAULT_HEIGHT-BOTTOM_BAR_DEFAULT_HEIGHT-imagePaneMap.getPrefHeight())/2);

        choicePane.getChildren().add(imagePaneMap);

        for (int i = 0 ; i < s[0].length ; i++) {

            Button button = new Button();
            button.setStyle("-fx-background-color: rgba(0,128,0,0.35); -fx-border-color: green; -fx-border-width: 2;");
            button.setPrefHeight(imagePaneMap.getPrefHeight()*(1318.0 / 1488) / 3);
            button.setPrefWidth(imagePaneMap.getPrefWidth()*(1733.0 / 1932) / 4);
            double offsetX = ((width - imagePaneMap.getPrefWidth()) / 2 ) + (imagePaneMap.getPrefWidth() * (85.0 / 1488)) ;
            button.setLayoutX(offsetX + button.getPrefWidth() * s[1][i]);
            double offsetY = (((height - 36 -16) - imagePaneMap.getPrefHeight()) / 2 ) + (imagePaneMap.getPrefHeight() * (99.5 / 1932)) ;
            button.setLayoutY(offsetY + button.getPrefHeight() * s[0][i]);
            button.setUserData(i);
            button.setOnAction(behavior -> {
                customStream.putLine(Integer.toString((int)button.getUserData()));
                cleanRequestPane();
                setGameMapScenario();
            });

            choicePane.getChildren().add(button);
        }

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(choicePane);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int booleanQuestion(String s) {
        CustomStream customStream = new CustomStream();

        Text textQuestion = new Text(s);
        textQuestion.setFont(Font.font(TEXT_FONT,FontWeight.NORMAL,TEXT_SIZE));

        Button buttonTrue  = new Button(BOOLEAN_BUTTON_TRUE);
        buttonTrue.setPrefWidth(80);
        buttonTrue.setOnAction(behavior -> {
            customStream.putLine(Integer.toString(1));
            cleanRequestPane();
            setGameMapScenario();
        });

        Button buttonFalse = new Button(BOOLEAN_BUTTON_FALSE);
        buttonFalse.setPrefWidth(80);
        buttonFalse.setOnAction(behavior -> {
            customStream.putLine(Integer.toString(0));
            cleanRequestPane();
            setGameMapScenario();
        });

        HBox hBoxChoice = new HBox();
        hBoxChoice.setAlignment(Pos.CENTER);
        hBoxChoice.setSpacing(ABSOLUTE_SPACING);
        hBoxChoice.getChildren().addAll(buttonTrue,buttonFalse);

        VBox vBoxChoice = new VBox();
        vBoxChoice.setSpacing(ABSOLUTE_SPACING);
        vBoxChoice.setAlignment(Pos.CENTER);
        vBoxChoice.getChildren().addAll(textQuestion,hBoxChoice);

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(vBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return Integer.parseInt(customStream.getLine());
    }

    @Override
    public int[] chooseMultiplePowerup(Powerup[] p) {
        CustomStream customStream = new CustomStream();

        HBox hBoxButtonGroup = new HBox();
        hBoxButtonGroup.setSpacing(ABSOLUTE_SPACING);
        hBoxButtonGroup.setAlignment(Pos.CENTER);

        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        for (int i = 0 ; i < p.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("powerupsRoot").concat(properties.getProperty("powerup" + p[i].getName().toString() + "_" + p[i].getColor().toString())))
            );
            stackPane.setPrefHeight(((this.width) * MEDIUM_BUTTON_WIDTH_RATIO) * POWERUP_HEIGHT_WIDTH_RATIO);
            stackPane.setPrefWidth(((this.width) * MEDIUM_BUTTON_WIDTH_RATIO));
            stackPane.setPadding(new Insets(ABSOLUTE_SPACING,ABSOLUTE_SPACING,ABSOLUTE_SPACING,ABSOLUTE_SPACING));

            CheckBox checkBox = new CheckBox();
            checkBox.setUserData(i);
            checkBox.setGraphic(stackPane);

            checkBoxes.add(checkBox);
            hBoxButtonGroup.getChildren().add(checkBox);
        }

        Button buttonSelect = new Button(SELECT_BUTTON_TEXT);
        buttonSelect.setOnAction(behavior -> {
            ArrayList<Integer> arrayList = new ArrayList<>();
            for (CheckBox checkBox : checkBoxes)
                if (checkBox.isSelected())
                    arrayList.add((Integer) checkBox.getUserData());
            Integer[] array = arrayList.toArray(new Integer[arrayList.size()]);
            customStream.putLine(gson.toJson(array));
            cleanRequestPane();
            setGameMapScenario();
        });

        VBox vBoxChoice = new VBox();
        vBoxChoice.setSpacing(ABSOLUTE_SPACING);
        vBoxChoice.setAlignment(Pos.CENTER);
        vBoxChoice.getChildren().addAll(hBoxButtonGroup,buttonSelect);

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(vBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return gson.fromJson(customStream.getLine(),int[].class);
    }

    @Override
    public int[] chooseMultipleWeapon(WeaponName[] w) {
        CustomStream customStream = new CustomStream();

        HBox hBoxButtonGroup = new HBox();
        hBoxButtonGroup.setSpacing(ABSOLUTE_SPACING);
        hBoxButtonGroup.setAlignment(Pos.CENTER);

        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        for (int i = 0 ; i < w.length ; i++) {

            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(
                    new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + w[i].toString())))
            );
            stackPane.setPrefHeight(((this.width) * MEDIUM_BUTTON_WIDTH_RATIO) * WEAPON_HEIGHT_WIDTH_RATIO);
            stackPane.setPrefWidth(((this.width) * MEDIUM_BUTTON_WIDTH_RATIO));
            stackPane.setPadding(new Insets(ABSOLUTE_SPACING,ABSOLUTE_SPACING,ABSOLUTE_SPACING,ABSOLUTE_SPACING));

            CheckBox checkBox = new CheckBox();
            checkBox.setUserData(i);
            checkBox.setGraphic(stackPane);

            checkBoxes.add(checkBox);
            hBoxButtonGroup.getChildren().add(checkBox);
        }

        Button buttonSelect = new Button(SELECT_BUTTON_TEXT);
        buttonSelect.setOnAction(behavior -> {
            ArrayList<Integer> arrayList = new ArrayList<>();
            for (CheckBox checkBox : checkBoxes)
                if (checkBox.isSelected())
                    arrayList.add((Integer) checkBox.getUserData());
            Integer[] array = arrayList.toArray(new Integer[arrayList.size()]);
            customStream.putLine(gson.toJson(array));
            cleanRequestPane();
            setGameMapScenario();
        });

        VBox vBoxChoice = new VBox();
        vBoxChoice.setSpacing(ABSOLUTE_SPACING);
        vBoxChoice.setAlignment(Pos.CENTER);
        vBoxChoice.getChildren().addAll(hBoxButtonGroup,buttonSelect);

        BorderPane borderPane = new BorderPane();
        Platform.runLater(() -> {
            borderPane.setTop(getTopBarLinkedMap());
            borderPane.setBottom(getBottomBar());
            borderPane.setCenter(vBoxChoice);
        });
        Platform.runLater(() -> requestPane.getChildren().add(borderPane));
        Platform.runLater(this::setRequestScenario);
        return gson.fromJson(customStream.getLine(),int[].class);
    }

    @Override
    public void notifyModelUpdate() {
        try {
            smartModel = client.getModelUpdate();
            if (smartModel != null) {
                setupGamePane();
                if (currentScenario.get() == 2)
                    Platform.runLater(this::setGameMapScenario);
            }
        } catch (Exception ignored) {
        }
    }

}

class ImagePane extends Pane {

    private static final String DEFAULT_IMAGEPANE_STYLE = "-fx-background-size: contain; -fx-background-repeat: no-repeat;";


    ImagePane(String imageRef) {
        this(imageRef,DEFAULT_IMAGEPANE_STYLE);
    }

    ImagePane(String imageRef, String style) {
        this(new SimpleStringProperty(imageRef),new SimpleStringProperty(style));
    }

    ImagePane(StringProperty imageRefProperty, StringProperty styleProperty) {
        styleProperty().bind(
                new SimpleStringProperty("-fx-background-image: url(\"")
                        .concat(imageRefProperty)
                        .concat(new SimpleStringProperty("\");"))
                        .concat(styleProperty)
        );
    }
}