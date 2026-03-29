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

public class DifficultyScreen {
    private final SceneManager sceneManager;
    private final ClientSession clientSession;

    public DifficultyScreen(SceneManager sceneManager, ClientSession clientSession) {
        this.sceneManager = sceneManager;
        this.clientSession = clientSession;
    }
    public Scene createDifficultyScreen() {

        //choose a difficulty
        Label titleLabel = new Label("Difficulty Selection");
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
        Label infoLabel = new Label("select to continue.");
        infoLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16px;");

        //Status label used to show what the player selects
        Label statusLabel = new Label();

        if(clientSession.getLastStatusMessage() != null) {
            statusLabel.setText(clientSession.getLastStatusMessage());
        }

        //Menu option Buttons
        Button easyButton = new Button("1. Easy");
        Button mediumButton = new Button("2. Medium");
        Button hardButton = new Button("3. Hard");

        easyButton.setPrefWidth(140);
        mediumButton.setPrefWidth(140);
        hardButton.setPrefWidth(140);

        // When the user clicks Start, send option 1 to the backend
        easyButton.setOnAction(event -> {
            clientSession.getClientConnection().sendMessage("1");

            statusLabel.setText("Easy option sent. Waiting for player response...");

            // Disable both buttons so the same option is not sent multiple times
            easyButton.setDisable(true);
            mediumButton.setDisable(true);
            hardButton.setDisable(true);
        });

        // When the user clicks MEDIUM, send option 2 to the backend
        mediumButton.setOnAction(event -> {
            clientSession.getClientConnection().sendMessage("2");

            statusLabel.setText("Medium option sent. Waiting for player response...");

            // Disable both buttons so the same option is not sent multiple times
            easyButton.setDisable(true);
            mediumButton.setDisable(true);
            hardButton.setDisable(true);
        });
        // When the user clicks HARD, send option 3 to the backend
        hardButton.setOnAction(event -> {
            clientSession.getClientConnection().sendMessage("3");

            statusLabel.setText("Hard option sent. Waiting for player response...");

            // Disable both buttons so the same option is not sent multiple times
            easyButton.setDisable(true);
            mediumButton.setDisable(true);
            hardButton.setDisable(true);
        });

        // place button on vertical layout
        VBox buttonBox = new VBox(15, easyButton, mediumButton,  hardButton);
        buttonBox.setAlignment(Pos.CENTER);

        //used to display any functionability on the screen
        VBox root = new VBox(25 ,  titleLabel, playersBox, infoLabel, buttonBox, statusLabel);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(25));

        //window layout (width, height)
        return new Scene(root, 450, 400);
    }
}
