package com.matchinggame.client.Controller;

import com.matchinggame.client.UI.SceneManager;
import com.matchinggame.client.UI.Screens.DifficultyScreen;
import com.matchinggame.client.UI.Screens.GameScreen;
import com.matchinggame.client.UI.Screens.LobbyScreen;
import com.matchinggame.client.UI.Screens.MenuScreen;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ClientMessageHandler {

    private final SceneManager sceneManager;
    private final ClientSession clientSession;

    public ClientMessageHandler(SceneManager sceneManager, ClientSession clientSession) {
        this.sceneManager = sceneManager;
        this.clientSession = clientSession;
    }

    public void handleServerMessage(String message, Label statusLabel, TextField usernameField, Button continueButton) {
        boolean handled  = false;
        if (message.equals("STATE:WAITING")) {
            handled = true;
            clientSession.setLastStatusMessage("Waiting for another player to connect to the server");
            sceneManager.showScene(new LobbyScreen(sceneManager, clientSession).createLobbyScreen());
        }

        else if (message.startsWith("STATE:ROOM_READY:")) {
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
        //track the size of the board
        else if (message.startsWith("BOARD_SIZE")) {
            handled = true;

            String sizeText = message.substring("BOARD_SIDE:".length()).trim();
            String[] parts = sizeText.split(" ");

            if(parts.length >= 2) {
                int rows = Integer.parseInt(parts[0]);
                int columns = Integer.parseInt(parts[1]);

                clientSession.setBoardRows(rows);
                clientSession.setBoardColumns(columns);
            }

        }
        //server informs who the opponent is
        else if (message.startsWith("MATCH_READY Opponent:")) {
            handled = true;
            String opponentName = message.substring("MATCH_READY Opponent:".length()).trim();
            clientSession.setOpponentUsername(opponentName);
        }
        //server assigns player role (player one / two)
        else if (message.startsWith("YOUR_ROLE ")) {
            handled = true;
            String role = message.substring("YOUR_ROLE ".length()).trim();
            clientSession.setPlayerRole(role);
        }
        //server confirms selected difficulty
        else if (message.startsWith("DIFFICULTY_CONFIRMED:")) {
            handled = true;
            String difficulty = message.substring("DIFFICULTY_CONFIRMED:".length()).trim();
            //show the current difficulty that the game is being played
            clientSession.setConfirmedDifficulty(difficulty);
        }
        //indicates that the game has started
        else if (message.startsWith("GAME_ROOM_STARTED")) {
            handled = true;
            clientSession.setLastStatusMessage("The match has been started");

            //switch to gameScreen
            sceneManager.showScene(new GameScreen(sceneManager, clientSession).createGameScreen());

        } else if (message.startsWith("BOARD ")) {
            handled = true;
            String boardDisplay = message.substring("BOARD ".length()).trim();

            //store board in session
            clientSession.setBoardDisplay(boardDisplay);
            //refresh gamescreen
            sceneManager.showScene(new GameScreen(sceneManager, clientSession).createGameScreen());

        } else if (message.startsWith("TURN")) {
            handled = true;
            String currentTurn = message.substring("TURN ".length()).trim();

            clientSession.setCurrentTurn(currentTurn);

            //refresh gamescreen to show updated turn
            sceneManager.showScene(new GameScreen(sceneManager, clientSession).createGameScreen());

        }
        // Live score update during gameplay
        else if (message.startsWith("SCORE:")) {
            handled = true;

            String scoreText = message.substring("SCORE:".length()).trim();
            clientSession.setCurrentScore(scoreText);

            sceneManager.showScene(
                    new GameScreen(sceneManager, clientSession).createGameScreen()
            );
        }

        // Game over trigger
        else if (message.equals("GAME_OVER")) {
            handled = true;

            clientSession.setGameOver(true);
            clientSession.setLastStatusMessage("The game has ended.");
        }

        // Final score at the end of the match
        else if (message.startsWith("FINAL_SCORE:")) {
            handled = true;

            String finalScore = message.substring("FINAL_SCORE:".length()).trim();
            clientSession.setFinalScore(finalScore);
            clientSession.setRecentFinalScore(finalScore);
            clientSession.setHasRecentMatchResult(true);
        }

        // Winner message
        else if (message.startsWith("WINNER_IS:")) {
            handled = true;

            String winner = message.substring("WINNER_IS:".length()).trim();
            clientSession.setWinnerMessage("Winner: " + winner);
            clientSession.setRecentWinnerMessage("Winner: " + winner);
            clientSession.setLastStatusMessage("The winner is " + winner + ".");
            clientSession.setHasRecentMatchResult(true);
        }

        // Draw message
        else if (message.equals("DRAW")) {
            handled = true;

            clientSession.setWinnerMessage("Result: Draw");
            clientSession.setRecentWinnerMessage("Result: Draw");
            clientSession.setLastStatusMessage("Result: Draw.");
            clientSession.setHasRecentMatchResult(true);
        }
        //if one of the player get desconnected
        else if (message.startsWith("OPPONENT_DISCONNECTED")) {
            handled = true;
            clientSession.setLastStatusMessage("The match has been stopped");
            clientSession.setLastStatusMessage("opponent disconnected");
        }
        //room closed (game ended unexpectedly)
        else if (message.startsWith("ROOM_CLOSED")) {
            handled = true;
            clientSession.setLastStatusMessage("Room closed");
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
