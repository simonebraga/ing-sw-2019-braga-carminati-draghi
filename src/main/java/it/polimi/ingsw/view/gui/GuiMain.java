package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.ViewInterface;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.rmi.RemoteException;

public class GuiMain extends Application implements ViewInterface {

    private BorderPane root = new BorderPane();
    private Scene scene = new Scene(root,800,600);
    private Stage stage;
    private Client client;

    private int networkType;

    public static void main(String[] args) {
        launch(args);
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
            try {
                client = new Client(networkType,this);
                client.login(nicknameField.getText());
            } catch (RemoteException e) {
                loginOutcomeText.setText("Server not connected");
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

        Button fullscreenButton = new Button("⤢");
        fullscreenButton.setOnAction(behav -> {
            if (!stage.isFullScreen())
                stage.setFullScreen(true);
            else
                stage.setFullScreen(false);
        });

        HBox hbFullscreen = new HBox();
        hbFullscreen.setPadding(new Insets(10,10,10,10));
        hbFullscreen.setAlignment(Pos.CENTER_RIGHT);
        hbFullscreen.getChildren().add(fullscreenButton);

        root.setBottom(hbFullscreen);
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
        return null;
    }

    @Override
    public WeaponName chooseWeapon(WeaponName[] w) {
        return null;
    }

    @Override
    public String chooseString(String[] s) {
        return null;
    }

    @Override
    public Powerup choosePowerup(Powerup[] p) {
        return null;
    }

    @Override
    public Boolean booleanQuestion(String s) {
        return null;
    }

    @Override
    public Powerup[] chooseMultiplePowerups(Powerup[] p) {
        return new Powerup[0];
    }

    @Override
    public WeaponName[] chooseMultipleWeapons(WeaponName[] w) {
        return new WeaponName[0];
    }

    @Override
    public String chooseMap(String[] s) {
        return null;
    }

    @Override
    public String chooseMode(String[] s) {
        return null;
    }

    @Override
    public String chooseSave(String[] s) {
        return null;
    }
}
