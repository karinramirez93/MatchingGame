package com.matchinggame.client.UI.Screens;

import com.matchinggame.client.Controller.ClientSession;
import com.matchinggame.client.UI.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class MenuScreen {
    private final SceneManager sceneManager;
    private final ClientSession clientSession;

    public MenuScreen(SceneManager sceneManager, ClientSession clientSession) {
        this.sceneManager = sceneManager;
        this.clientSession = clientSession;
    }
    public Scene createMenuScreen() {

        //Main Menu
        Label titleLabel = new Label("Main Menu");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold");

        //Player vs Player
        Label playerLabel = new Label("Player: " + clientSession.getUsername() );
        Label vsLabel = new Label(" Vs ");
        Label opponentLabel = new Label("Opponent: " + clientSession.getOpponentUsername());

        // player vs player style
        playerLabel.setStyle("-fx-font-size: 16px;");
        opponentLabel.setStyle("-fx-font-size: 16px;");
        vsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // player vs player layout
        HBox playersBox = new HBox(10, playerLabel, vsLabel, opponentLabel);
        playersBox.setAlignment(Pos.CENTER);

        //informational label to guide the user
        Label infoLabel = new Label("Choose an option to continue.");
        infoLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px;");

        //Status label used to show what the player selects
        Label statusLabel = new Label();

        if(clientSession.getLastStatusMessage() != null) {
            statusLabel.setText(clientSession.getLastStatusMessage());
        }

        //Menu option Buttons
        Button startButton = new Button("1. Start");
        Button exitButton = new Button("2. exit");

        startButton.setPrefWidth(140);
        exitButton.setPrefWidth(140);

        // When the user clicks Start, send option 1 to the backend
        startButton.setOnAction(event -> {
            clientSession.getClientConnection().sendMessage("1");

            statusLabel.setText("Start option sent. Waiting for player response...");

            // Disable both buttons so the same option is not sent multiple times
            startButton.setDisable(true);
            exitButton.setDisable(true);
        });

        // When the user clicks Exit, send option 2 to the backend
        exitButton.setOnAction(event -> {
            clientSession.getClientConnection().sendMessage("2");

            statusLabel.setText("Exit option sent. Waiting for server response...");

            // Disable both buttons so the same option is not sent multiple times
            startButton.setDisable(true);
            exitButton.setDisable(true);
        });

        // place button on vertical layout
        VBox buttonBox = new VBox(15, startButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER);


        VBox root = new VBox(25 ,  titleLabel, playersBox, infoLabel, buttonBox, statusLabel);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(25));

        return new Scene(root, 450, 320);
    }




}
