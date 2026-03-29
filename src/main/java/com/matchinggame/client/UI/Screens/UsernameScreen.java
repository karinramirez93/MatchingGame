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

            //disable input control to avoid sending duplicate usernames
            usernameField.setDisable(true);
            continueButton.setDisable(true);

            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Submitting username...");

            //start a background listener only once for the whole client session
            if (clientSession.getListeningThread() == null) {
                ServerMessageListener listener = new ServerMessageListener(
                        clientSession.getClientConnection(),
                        message -> Platform.runLater(() -> handleServerMessage(
                                message,
                                statusLabel,
                                usernameField,
                                continueButton
                        ))
                );

                Thread listenerThread = new Thread(listener);
                listenerThread.setDaemon(true);
                listenerThread.start();

                clientSession.setListenerThread(listenerThread);
            }
            //send the username
            clientSession.getClientConnection().sendMessage("USERNAME " +username);

        });
        VBox root = new VBox(10, titleLabel, instructionsLabel, usernameField,  continueButton, statusLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));

        return new Scene(root, 420, 300);

    }
    private void handleServerMessage(String message, Label statusLabel, TextField usernameField, Button continueButton) {
        boolean handled  = false;
        if (message.equals("STATE:WAITING")) {
            handled = true;
            clientSession.setLastStatusMessage("Waiting for another player to connect to the server");
            sceneManager.showScene(new LobbyScreen(sceneManager, clientSession).createLobbyScreen());
        }

        else if (message.startsWith("STATE:ROOM_READY")) {
            handled = true;

            String opponentName = message.substring("STATE:ROOM_READY:".length()).trim();
            clientSession.setOpponentUsername(opponentName);
            clientSession.setLastStatusMessage(null);
            sceneManager.showScene(new MenuScreen(sceneManager, clientSession).createMenuScreen());
        }
        else if (message.contains("MENU_SELECTION ")) {
            handled = true;

            // Example: "Menu selection ALICE START"
            String[] parts = message.split(" ");
            if (parts.length >= 3) {
                String playerName = parts[1];
                String selection = parts[2];

                if (!playerName.equalsIgnoreCase(clientSession.getUsername())) {
                    // Here you can update a status label in the menu screen
                    clientSession.setLastStatusMessage(
                            //message that the opponent received
                            playerName + " selected " + selection + ". Choose the same option to continue."
                    );
                    sceneManager.showScene(new MenuScreen(sceneManager, clientSession).createMenuScreen());
                }
            }
        }

        else if(message.contains("Choose Difficulty")) {
            handled = true;
            clientSession.setLastStatusMessage(null);
            sceneManager.showScene(new DifficultyScreen(sceneManager, clientSession).createDifficultyScreen());
        }
        else if (message.startsWith("DIFFICULTY_SELECTION:")) {
            handled = true;

            String payload = message.substring("DIFFICULTY_SELECTION:".length()).trim();
            String[] parts = payload.split(" ");

            if (parts.length >= 2) {
                String playerName = parts[0];
                String selection = parts[1];

                if (!playerName.equalsIgnoreCase(clientSession.getUsername())) {
                    clientSession.setLastStatusMessage(
                            playerName + " selected " + selection + ". Choose the same difficulty to continue."
                    );

                    sceneManager.showScene(
                            new DifficultyScreen(sceneManager, clientSession).createDifficultyScreen()
                    );
                }
            }
        }

        //in case one of the two player is disconnected
        //remaining player online is sent back to the lobby
        else if (message.contains("RETURNING_TO_LOBBY")) {
            handled = true;
            clientSession.setOpponentUsername(null);
            clientSession.setLastStatusMessage("Opponenet disconnected. waiting for another player");
            sceneManager.showScene(new LobbyScreen(sceneManager, clientSession).createLobbyScreen());
        }
        //waiting for another player to connect to the server
        else if(message.equals("WAITING_FOR_PLAYER")){
            handled = true;
            clientSession.setLastStatusMessage("Waiting for the player to connect...");
            sceneManager.showScene(new LobbyScreen(sceneManager, clientSession).createLobbyScreen());
        }
        else if (message.contains("ERROR")){
            handled = true;
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText(message);

            //allow the user to try again if the backend rejects the username introduced
            usernameField.setDisable(false);
            continueButton.setDisable(false);
        }
        if(!handled) {
            System.out.println("Unhandled server message: " + message);
        }

    }

}
