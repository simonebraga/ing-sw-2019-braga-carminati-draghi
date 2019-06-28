package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.cardclasses.Powerup;
import it.polimi.ingsw.model.enumeratedclasses.Color;
import it.polimi.ingsw.model.enumeratedclasses.Figure;
import it.polimi.ingsw.model.enumeratedclasses.WeaponName;
import it.polimi.ingsw.model.mapclasses.Square;
import it.polimi.ingsw.model.smartmodel.SmartModel;
import it.polimi.ingsw.model.smartmodel.SmartWeapon;
import it.polimi.ingsw.view.Client;
import it.polimi.ingsw.view.ViewInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class GuiMain extends Application implements ViewInterface {

    private Properties properties;
    private Stage primaryStage;
    private Scene primaryScene;
    private StackPane rootPane;
    private Client client;
    private String nickname;
    private AtomicInteger currentScenario;
    private Text textEvent;
    private SmartModel smartModel = null;
    private BorderPane smartModelPane = new BorderPane();

    public GuiMain() {
        currentScenario = new AtomicInteger();
        textEvent = new Text();
        textEvent.setFill(javafx.scene.paint.Color.FIREBRICK);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private AnchorPane getFullscreenButton() {

        Button buttonFullscreen = new Button("⤢");
        buttonFullscreen.setOnAction(behavior -> {
            if (primaryStage.isFullScreen())
                primaryStage.setFullScreen(false);
            else
                primaryStage.setFullScreen(true);
        });
        AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setBottomAnchor(buttonFullscreen,10.0);
        AnchorPane.setRightAnchor(buttonFullscreen,10.0);
        anchorPane.getChildren().add(buttonFullscreen);
        return anchorPane;

    }

    private AnchorPane getLogoutButton() {

        Button buttonLogout = new Button("Logout");
        buttonLogout.setOnAction(behavior -> {
            client.logout();
            Platform.runLater(this::setCleanScenario);
            Platform.runLater(this::setLoginScenario);
        });
        AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(buttonLogout,10.0);
        AnchorPane.setRightAnchor(buttonLogout,10.0);
        anchorPane.getChildren().add(buttonLogout);
        return anchorPane;
    }

    private StackPane getBottomBar() {

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(textEvent);
        stackPane.getChildren().add(getFullscreenButton());
        return stackPane;
    }

    private void updateSmartModelPane() {

        AnchorPane anchorPaneLogout = getLogoutButton();
        smartModelPane.setTop(anchorPaneLogout);
        StackPane stackPaneBottomBar = getBottomBar();
        smartModelPane.setBottom(stackPaneBottomBar);

        StackPane stackPaneGameScenario = new StackPane();
        stackPaneGameScenario.maxWidthProperty().bind(primaryScene.widthProperty());
        stackPaneGameScenario.minWidthProperty().bind(primaryScene.widthProperty());
        stackPaneGameScenario.maxHeightProperty().bind(primaryScene.heightProperty().subtract(anchorPaneLogout.heightProperty()).subtract(stackPaneBottomBar.heightProperty()));
        stackPaneGameScenario.minHeightProperty().bind(primaryScene.heightProperty().subtract(anchorPaneLogout.heightProperty()).subtract(stackPaneBottomBar.heightProperty()));

        StackPane stackPaneMap = new StackPane();
        stackPaneMap.getChildren().add(
                new ImagePane(properties.getProperty("mapsRoot").concat(properties.getProperty("map"+smartModel.getMapIndex())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;")
        );
        stackPaneMap.setPadding(new Insets(10,10,10,10));
        stackPaneMap.maxHeightProperty().bind(stackPaneGameScenario.heightProperty().multiply(0.7));
        stackPaneMap.minHeightProperty().bind(stackPaneGameScenario.heightProperty().multiply(0.7));
        stackPaneMap.maxWidthProperty().bind(stackPaneGameScenario.widthProperty().multiply(0.5));
        stackPaneMap.minWidthProperty().bind(stackPaneGameScenario.widthProperty().multiply(0.5));

        GridPane gridPaneOtherPlayers = new GridPane();
        int i = 0;
        for (String nickname : smartModel.getSmartPlayerMap().keySet()) {
            if (nickname.equals(this.nickname)) {
                //TODO
            } else {
                StackPane stackPanePlayerBoard = new StackPane();
                stackPanePlayerBoard.getChildren().add(
                        new ImagePane(properties.getProperty("boardsRoot").concat(properties.getProperty("board" + smartModel.getSmartPlayerMap().get(nickname).getFigure().toString())),"-fx-background-size: contain; -fx-background-repeat: no-repeat;")
                );
                stackPanePlayerBoard.setPadding(new Insets(10,10,10,10));
                stackPanePlayerBoard.maxHeightProperty().bind(stackPaneMap.heightProperty().divide(4));
                stackPanePlayerBoard.minHeightProperty().bind(stackPaneMap.heightProperty().divide(4));
                stackPanePlayerBoard.maxWidthProperty().bind(stackPaneGameScenario.widthProperty().subtract(stackPaneMap.widthProperty()).multiply(0.6));
                stackPanePlayerBoard.minWidthProperty().bind(stackPaneGameScenario.widthProperty().subtract(stackPaneMap.widthProperty()).multiply(0.6));
                stackPanePlayerBoard.setStyle("-fx-border-color: red; -fx-border-width: 1;");
                gridPaneOtherPlayers.add(stackPanePlayerBoard,0,i);

                HBox hBoxWeapons = new HBox();
                hBoxWeapons.setStyle("-fx-border-color: red; -fx-border-width: 1;");
                for (SmartWeapon smartWeapon : smartModel.getSmartPlayerMap().get(nickname).getWeapons()) {
                    StackPane stackPaneWeapon = new StackPane();
                    if (smartWeapon.getLoaded())
                        stackPaneWeapon.getChildren().add(
                                new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weapon" + smartWeapon.getWeaponName())), "-fx-background-size: contain; -fx-background-repeat: no-repeat;")
                        );
                    else
                        stackPaneWeapon.getChildren().add(
                                new ImagePane(properties.getProperty("weaponsRoot").concat(properties.getProperty("weaponBack")),"-fx-background-size: contain; -fx-background-repeat: no-repeat;")
                        );
                    stackPaneWeapon.maxHeightProperty().bind(stackPaneMap.heightProperty().divide(4));
                    stackPaneWeapon.minHeightProperty().bind(stackPaneMap.heightProperty().divide(4));
                    stackPaneWeapon.maxWidthProperty().bind(stackPaneGameScenario.widthProperty().subtract(stackPaneMap.widthProperty()).subtract(stackPanePlayerBoard.widthProperty()).divide(3));
                    stackPaneWeapon.minWidthProperty().bind(stackPaneGameScenario.widthProperty().subtract(stackPaneMap.widthProperty()).subtract(stackPanePlayerBoard.widthProperty()).divide(3));
                    hBoxWeapons.getChildren().add(stackPaneWeapon);
                }
                gridPaneOtherPlayers.add(hBoxWeapons,1,i);

                i++;
            }
        }

        GridPane gridPaneGameScenario = new GridPane();
        gridPaneGameScenario.add(stackPaneMap,0,0);
        gridPaneGameScenario.add(gridPaneOtherPlayers,1,0);
        stackPaneGameScenario.getChildren().add(gridPaneGameScenario);

        smartModelPane.setCenter(stackPaneGameScenario);
    }

    private void setCleanScenario() {
        currentScenario.set(0);

        if (!rootPane.getChildren().isEmpty()) {
            rootPane.getChildren().subList(0, rootPane.getChildren().size()).clear();
        }
    }

    private void setLoginScenario() {
        currentScenario.set(1);

        Platform.runLater(() -> textEvent.setText(""));
        AtomicInteger networkType = new AtomicInteger();

        Text textWelcome = new Text("Welcome to Adrenaline!");
        textWelcome.setFont(Font.font("Tahoma", FontWeight.NORMAL,20));
        HBox hBoxTextWelcome = new HBox(textWelcome);
        hBoxTextWelcome.setAlignment(Pos.CENTER);

        Text textLoginOutCome = new Text();
        textLoginOutCome.setFill(javafx.scene.paint.Color.FIREBRICK);
        HBox hBoxTextLoginOutcome = new HBox(textLoginOutCome);
        hBoxTextLoginOutcome.setAlignment(Pos.CENTER);

        TextField textFieldNickname = new TextField();
        Button buttonLogin = new Button("Login");
        buttonLogin.setOnAction(behavior -> {
            nickname = textFieldNickname.getText();
            try {
                client = new Client(networkType.get(),this);
                int ret = client.login(nickname);
                switch (ret) {
                    case 0: {
                        Platform.runLater(this::setCleanScenario);
                        Platform.runLater(this::setStartWaitScenario);
                        break;
                    }
                    case 1: {
                        Platform.runLater(() -> textLoginOutCome.setText("Nickname already chosen"));
                        break;
                    }
                    case 2: {
                        Platform.runLater(this::setCleanScenario);
                        smartModel = client.getModelUpdate();
                        notifyModelUpdate();
                        if (smartModel != null)
                            Platform.runLater(this::setGameMapScenario);
                        else {
                            Platform.runLater(this::setStartWaitScenario);
                        }
                        break;
                    }
                    case 3: {
                        Platform.runLater(() -> textLoginOutCome.setText("Nickname already logged in"));
                        break;
                    }
                    case 4: {
                        Platform.runLater(() -> textLoginOutCome.setText("Nickname not registered"));
                        break;
                    }
                    default:
                        Platform.runLater(() -> textLoginOutCome.setText("Something very bad went wrong"));
                }
            } catch (Exception e) {
                Platform.runLater(() -> textLoginOutCome.setText("Server not available"));
            }
        });

        RadioButton radioButtonRMI = new RadioButton("RMI");
        radioButtonRMI.setUserData(0);
        RadioButton radioButtonSocket = new RadioButton("Socket");
        radioButtonSocket.setUserData(1);

        ToggleGroup toggleGroupNetworkChoice = new ToggleGroup();
        radioButtonRMI.setToggleGroup(toggleGroupNetworkChoice);
        radioButtonSocket.setToggleGroup(toggleGroupNetworkChoice);
        radioButtonRMI.setSelected(true);
        toggleGroupNetworkChoice.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> obsValue, Toggle oldToggle, Toggle newToggle) -> networkType.set((int) toggleGroupNetworkChoice.getSelectedToggle().getUserData()));

        HBox hBoxNetworkChoice = new HBox(radioButtonRMI,radioButtonSocket);
        hBoxNetworkChoice.setSpacing(10);
        hBoxNetworkChoice.setAlignment(Pos.CENTER);

        GridPane gridPaneLogin = new GridPane();
        gridPaneLogin.setAlignment(Pos.CENTER);
        gridPaneLogin.setHgap(10);
        gridPaneLogin.setVgap(10);
        gridPaneLogin.setPadding(new Insets(10,10,10,10));
        gridPaneLogin.add(textWelcome,0,0,3,1);
        gridPaneLogin.add(textFieldNickname,0,1,2,1);
        gridPaneLogin.add(buttonLogin,2,1);
        gridPaneLogin.add(hBoxNetworkChoice,0,2,3,1);
        gridPaneLogin.add(hBoxTextLoginOutcome,0,3,3,1);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gridPaneLogin);
        borderPane.setBottom(getFullscreenButton());

        rootPane.getChildren().add(borderPane);
    }

    private void setStartWaitScenario() {
        currentScenario.set(2);

        Text textWait = new Text("Waiting for the game to start");
        textWait.setFont(Font.font("Tahoma",FontWeight.NORMAL,20));

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(textWait);
        borderPane.setBottom(getBottomBar());
        borderPane.setTop(getLogoutButton());

        rootPane.getChildren().add(borderPane);
    }

    private void setGameMapScenario() {
        currentScenario.set(3);

        rootPane.getChildren().add(smartModelPane);
    }

    @Override
    public void start(Stage stage) throws Exception {

        properties = new Properties();
        try {
            properties.load(Objects.requireNonNull(Client.class.getClassLoader().getResourceAsStream("graphics/references.properties")));
        } catch (Exception e) {
            System.err.println("Error reading graphics/references.properties");
            throw new Exception();
        }

        primaryStage = stage;
        rootPane = new StackPane();
        primaryScene = new Scene(rootPane,800,600);

        primaryStage.setTitle("Adrenaline");
        primaryStage.setScene(primaryScene);
        primaryStage.show();
        Platform.runLater(this::setLoginScenario);
    }

    @Override
    public void logout() {
        if (currentScenario.get() != 1) {
            Platform.runLater(this::setCleanScenario);
            Platform.runLater(this::setLoginScenario);
        }
    }

    @Override
    public void sendMessage(String s) {

    }

    @Override
    public void notifyEvent(String s) {
        textEvent.setText(s);
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
            smartModel = client.getModelUpdate();
            if (smartModel != null) {
                updateSmartModelPane();
                if (currentScenario.get() == 2) {
                    Platform.runLater(this::setCleanScenario);
                    Platform.runLater(this::setGameMapScenario);
                }
            }
        } catch (Exception ignored) {
        }
    }
}

class ImagePane extends Pane {

    ImagePane(String imageRef) {
        this(imageRef,"-fx-background-size: cover; -fx-background-repeat: no-repeat;");
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