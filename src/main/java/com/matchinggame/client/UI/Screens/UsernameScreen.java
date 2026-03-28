package com.matchinggame.client.UI.Screens;

import com.matchinggame.client.Controller.ClientSession;
import com.matchinggame.client.Controller.ServerMessageListener;
import com.matchinggame.client.UI.SceneManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

//Screen that allows the player to enter a userName
//after establishing a connection to the server
public class UsernameScreen {
    private final SceneManager sceneManager;
    private final ClientSession clientSession;

    public UsernameScreen(SceneManager sceneManager, ClientSession clientSession) {
        this.sceneManager = sceneManager;
        this.clientSession = clientSession;
    }
    //builds and returns the JavaFx scene for the username screen
    //returns user input
    public Scene createUserNameScene() {
        Label titleLabel = new Label("Enter Username");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold");

        Label instructionsLabel = new Label("Choose a username to join to the lobby");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");

        Label statusLabel = new Label();

        Button continueButton = new Button("Continue");

        continueButton.setOnAction(event -> {
            String username = usernameField.getText().trim();

            //avoiding empty usernames
            if(username.isEmpty()){
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Username cannot be empty");
                return;

            }
            //stores the username
            clientSession.setUsername(username);
            //send the username expected to the server (USERNAME yourname)
            //user only has to type he/her name like "ALICE or Alice or alice"
            //server will receive "USERNAME ALICE" as expected
            clientSession.getClientConnection().sendMessage("USERNAME " +username);
            statusLabel.setText("Submitting username...");

            //disable input control to avoid sending duplicate usernames
            usernameField.setDisable(true);
            continueButton.setDisable(true);

            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Joining lobby");

            System.out.println("Username sent: USERNAME " + username);

            //start a background listener only once
            if(clientSession.getListeningThread() == null){
                ServerMessageListener listener = new ServerMessageListener(clientSession.getClientConnection(),message -> Platform.runLater(() -> {
                    System.out.println("Server message: " + message);

                    //check if username is accepted
                    if(message.contains("Username accepted")){
                        //go to lobby only if username is accepted
                        sceneManager.showScene(new LobbyScreen(sceneManager,clientSession).createLobbyScreen());
                    }
                    else if(message.contains("ERROR")){
                        //show error to the screen
                        statusLabel.setStyle("-fx-text-fill: red;");
                        statusLabel.setText(message);

                        //let the username box available again to enter a new username and click "continue" button
                        usernameField.setDisable(false);
                        continueButton.setDisable(false);
                    }
                }) );
                Thread listenerThread = new Thread(listener);
                listenerThread.setDaemon(true);
                listenerThread.start();

                clientSession.setListenerThread(listenerThread);
            }

        });
        VBox root = new VBox(10, titleLabel, instructionsLabel, usernameField,  continueButton, statusLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));

        return new Scene(root, 420, 300);

    }

}
