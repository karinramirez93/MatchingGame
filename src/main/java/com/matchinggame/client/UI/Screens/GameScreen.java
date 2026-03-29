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
public class GameScreen {

    private ClientSession clientSession;
    private SceneManager sceneManager;

    //constructor
    public GameScreen(SceneManager sceneManager, ClientSession clientSession) {
        this.clientSession = clientSession;
        this.sceneManager = sceneManager;
    }

    //create the GameScreen
    public Scene createGameScreen(){
        //title
        Label titleLabel = new Label("Matching Game");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: black;");

        //display player vs opponent (example: Alice vs Dragon)
        Label playerInfoLabel = new Label(clientSession.getUsername() + " Vs " + clientSession.getOpponentUsername() );

        //display the role assigned by the server
        Label roleLabel = new Label("Role: " + safeValue(clientSession.getPlayerRole()));

        //display selected difficulty of the match
        Label difficultyLabel = new Label("Difficulty: " + safeValue(clientSession.getConfirmedDifficulty()));

        //display who turn it is
        Label turnLabel = new Label("Current Turn: " + safeValue(clientSession.getCurrentTurn()));

        //title for the board section
        Label boardTitleLabel = new Label("Board: ");
        boardTitleLabel.setStyle("-fx-font-weight: bold;");

        //board representation (text-based for now)
        Label boardLabel = new Label(safeValue(clientSession.getBoardDisplay()));

        //align the board visually
        boardLabel.setStyle("fx-font-family: monospace; -fx-font-size: 14;");

        //status label for dynamic messages (errors, updates, etc)
        Label statusLabel = new Label();

        if(clientSession.getLastStatusMessage() != null){
            statusLabel.setText(clientSession.getLastStatusMessage());
        }

        //screen layout
        VBox root = new VBox(15, titleLabel, playerInfoLabel,  roleLabel, difficultyLabel, turnLabel, boardTitleLabel, boardLabel, statusLabel);

        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));
        return new Scene(root, 560, 420);

    }
    //helper to avoid showing null values in UI
    private String safeValue(String value){
        return value != null ? value : "not available yet";
    }


}//end class
