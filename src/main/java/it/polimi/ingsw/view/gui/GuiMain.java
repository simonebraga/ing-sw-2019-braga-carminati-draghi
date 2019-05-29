package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.NetworkException;
import it.polimi.ingsw.view.ViewInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class contains the JavaFX application of the client
 * @author simonebraga
 */
public class GuiMain extends Application implements ViewInterface {

    private BorderPane root = new BorderPane();
    private Scene scene = new Scene(root,800,600);
    private Stage stage;
    private Client client;

    private int networkType;
    private String nickname;
    private ArrayList<String> nicknameList;

    public static void main(String[] args) {
        launch(args);
    }

    private void setGamemapScenario() {

        ImageView map = null;
        map = new ImageView(new Image(GuiMain.class.getClassLoader().getResourceAsStream("graphics/maps/test.png"),700,500,false,false));
        //map.fitWidthProperty().bind(stage.widthProperty());
        //map.fitHeightProperty().bind(stage.heightProperty());
        root.setCenter(map);
        Platform.runLater(this::setLogoutButtonScenario);

    }

    private void setStartwaitScenario() {

        Text waitText = new Text("Waiting for the game to start");
        waitText.setFont(Font.font("Tahoma",FontWeight.NORMAL,20));
        HBox hbWaitText = new HBox();
        hbWaitText.setAlignment(Pos.CENTER);
        hbWaitText.setPadding(new Insets(10,10,10,10));
        hbWaitText.getChildren().add(waitText);

        root.setCenter(hbWaitText);

    }

    private void setLogoutButtonScenario() {

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(behav -> {
            client.logout();
            client = null;
            root.setCenter(null);
            root.setTop(null);
            setLoginScenario();
            setNetworkChoiceScenarioBack();
        });

        HBox hbLogoutButton = new HBox();
        hbLogoutButton.setPadding(new Insets(10,10,10,10));
        hbLogoutButton.setAlignment(Pos.CENTER_RIGHT);
        hbLogoutButton.getChildren().add(logoutButton);

        root.setTop(hbLogoutButton);

    }

    private void setLoginScenario() {

        Text nicknameChoiceText = new Text("Choose your nickname");
        nicknameChoiceText.setFont(Font.font("Tahoma",FontWeight.NORMAL,20));

        HBox hbNicknameChoiceText = new HBox();
        hbNicknameChoiceText.setAlignment(Pos.CENTER);
        hbNicknameChoiceText.getChildren().add(nicknameChoiceText);

        TextField nicknameField = new TextField();

        Text loginOutcomeText = new Text();
        loginOutcomeText.setFill(Color.FIREBRICK);

        HBox hbLoginOutcomeText = new HBox();
        hbLoginOutcomeText.setAlignment(Pos.CENTER);
        hbLoginOutcomeText.getChildren().add(loginOutcomeText);

        Button loginButton = new Button("Login");
        loginButton.setOnAction(behav -> {
            nickname = nicknameField.getText();
            try {
                client = new Client(networkType,this);
                int ret = client.login(nickname);
                switch (ret) {
                    case 0: {
                        root.setCenter(null);
                        root.setTop(null);
                        Platform.runLater(this::setStartwaitScenario);
                        Platform.runLater(this::setLogoutButtonScenario);
                        break;
                    }
                    case 1: {
                        loginOutcomeText.setText("Nickname already chosen");
                        break;
                    }
                    case 2: {
                        root.setTop(null);
                        root.setCenter(null);
                        // TODO Game started scenario
                        break;
                    }
                    case 3: {
                        loginOutcomeText.setText("Nickname not registered");
                        break;
                    }
                    default:
                        Platform.runLater(() -> loginOutcomeText.setText("Something very bad went wrong"));
                }
            } catch (Exception e) {
                Platform.runLater(() -> loginOutcomeText.setText("Server not available"));
            }
        });

        GridPane loginPane = new GridPane();
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setHgap(10);
        loginPane.setVgap(10);
        loginPane.setPadding(new Insets(10,10,10,10));
        loginPane.add(hbNicknameChoiceText,0,0,2,1);
        loginPane.add(nicknameField,0,1);
        loginPane.add(loginButton,1,1);
        loginPane.add(hbLoginOutcomeText,0,2,2,1);

        root.setCenter(loginPane);

    }

    private void setNetworkChoiceScenarioBack() {

        Button backButton = new Button("←");
        backButton.setOnAction(behav -> {
            root.setTop(null);
            root.setCenter(null);
            setNetworkChoiceScenario();
        });

        HBox hbBack = new HBox();
        hbBack.setPadding(new Insets(10,10,10,10));
        hbBack.setAlignment(Pos.CENTER_LEFT);
        hbBack.getChildren().add(backButton);

        root.setTop(hbBack);

    }

    private void setNetworkChoiceScenario() {

        Text welcomeText = new Text("Welcome to Adrenaline!");
        welcomeText.setFont(Font.font("Tahoma", FontWeight.NORMAL,20));

        HBox hbWelcomeText = new HBox();
        hbWelcomeText.setAlignment(Pos.CENTER);
        hbWelcomeText.getChildren().add(welcomeText);

        Label networkChoiceLabel = new Label("Choose network technology");

        Button socketButton = new Button("Socket");
        socketButton.setOnAction(behav -> {
            networkType = 1;
            root.setCenter(null);
            setNetworkChoiceScenarioBack();
            setLoginScenario();
        });

        Button rmiButton = new Button("RMI");
        rmiButton.setOnAction(behav -> {
            networkType = 0;
            root.setCenter(null);
            setNetworkChoiceScenarioBack();
            setLoginScenario();
        });

        GridPane networkChoicePane = new GridPane();
        networkChoicePane.setAlignment(Pos.CENTER);
        networkChoicePane.setHgap(10);
        networkChoicePane.setVgap(10);
        networkChoicePane.setPadding(new Insets(10,10,10,10));
        networkChoicePane.add(hbWelcomeText,0,0,3,1);
        networkChoicePane.add(networkChoiceLabel,0,1);
        networkChoicePane.add(socketButton,1,1);
        networkChoicePane.add(rmiButton,2,1);

        root.setCenter(networkChoicePane);

    }

    private void setFullscreenButtonScenario() {

        //Button fullscreenButton = new Button("⤢");
        //fullscreenButton.setOnAction(behav -> {
        //    if (!stage.isFullScreen())
        //        stage.setFullScreen(true);
        //    else
        //        stage.setFullScreen(false);
        //});

        //HBox hbFullscreen = new HBox();
        //hbFullscreen.setPadding(new Insets(10,10,10,10));
        //hbFullscreen.setAlignment(Pos.CENTER_RIGHT);
        //hbFullscreen.getChildren().add(fullscreenButton);

        //root.setBottom(hbFullscreen);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setTitle("Adrenaline");

        setFullscreenButtonScenario();
        setNetworkChoiceScenario();
        stage.setScene(scene);
        stage.show();
        return;
    }

    @Override
    public Figure choosePlayer(Figure[] f) {
        // TODO
        return null;
    }

    @Override
    public WeaponName chooseWeapon(WeaponName[] w) {
        // TODO
        return null;
    }

    @Override
    public String chooseString(String[] s) {
        // TODO
        return null;
    }

    @Override
    public Powerup choosePowerup(Powerup[] p) {
        // TODO
        return null;
    }

    @Override
    public Boolean booleanQuestion(String s) {
        // TODO
        return null;
    }

    @Override
    public Powerup[] chooseMultiplePowerups(Powerup[] p) {
        // TODO
        return new Powerup[0];
    }

    @Override
    public WeaponName[] chooseMultipleWeapons(WeaponName[] w) {
        // TODO
        return new WeaponName[0];
    }

    @Override
    public String chooseMap(String[] s) {
        // TODO
        return null;
    }

    @Override
    public String chooseMode(String[] s) {
        // TODO
        return null;
    }

    @Override
    public String chooseSave(String[] s) {
        // TODO
        return null;
    }

    @Override
    public void startGame() {
        Platform.runLater(this::setGamemapScenario);
    }

    @Override
    public void notifyEvent(String s) {
        Platform.runLater(() -> {
            Text event = new Text(s);
            HBox hbEvent = new HBox();
            hbEvent.setAlignment(Pos.CENTER);
            hbEvent.getChildren().add(event);
            root.setBottom(hbEvent);
        });
    }
}
