package com.matchinggame.client.UI.Screens;

import com.matchinggame.client.Controller.ClientSession;
import com.matchinggame.client.UI.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
public class LobbyScreen {
    private final SceneManager sceneManager;
    private ClientSession clientSession;

    //constructor
    public LobbyScreen(SceneManager sceneManager,  ClientSession clientSession) {
        this.sceneManager = sceneManager;
        this.clientSession = clientSession;
    }

    //builds the lobby waiting scene
    public Scene createLobbyScreen() {
        Label titleLabel = new Label("Lobby");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold");

        Label usernameLabel = new Label("Player: " + clientSession.getUsername());

        String lobbyMessage = clientSession.getLastStatusMessage() != null
                ? clientSession.getLastStatusMessage()
                : "Opponent is disconnected \n \nWaiting for another player...";

        Label waitingLabel = new Label(lobbyMessage);
        waitingLabel.setStyle("-fx-text-fill: green;");

        VBox root = new VBox(12, titleLabel, usernameLabel, waitingLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));


        return new Scene(root, 420, 300);
    }

}
