package com.matchinggame.client.UI.Screens;

import com.matchinggame.client.Controller.ClientSession;
import com.matchinggame.client.UI.SceneManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
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
        Label playerLabel = new Label("Player: " + safeValue(clientSession.getUsername()));
        Label vsLabel = new Label("VS");
        Label opponentLabel = new Label("Opponent: " + safeValue(clientSession.getOpponentUsername()));

        playerLabel.setStyle("-fx-font-size: 15px;");
        vsLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        opponentLabel.setStyle("-fx-font-size: 15px;");

        HBox playerInfoBox = new HBox(12, playerLabel, vsLabel, opponentLabel);
        playerInfoBox.setAlignment(Pos.CENTER);

        //display live score
        Label scoreLabel = new Label("Score: " + safeValue(clientSession.getCurrentScore()));
        scoreLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        //display the role assigned by the server
        Label roleLabel = new Label("Role: " + safeValue(clientSession.getPlayerRole()));

        //display selected difficulty of the match
        Label difficultyLabel = new Label("Difficulty: " + safeValue(clientSession.getConfirmedDifficulty()));

        //display who turn it is
        Label turnLabel = new Label("Current Turn: " + safeValue(clientSession.getCurrentTurn()));

        // Inform the player if it is currently their turn
        Label turnStatusLabel = new Label();
        if (isMyTurn()) {
            turnStatusLabel.setText("It is your turn.");
            turnStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            turnStatusLabel.setText("Waiting for opponent turn...");
            turnStatusLabel.setStyle("-fx-text-fill: darkorange; -fx-font-weight: bold;");
        }

        //visual Board
        Label boardTitleLabel = new Label("Board");
        boardTitleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane boardGrid = buildBoardGrid();


        //status label for dynamic messages (errors, updates, etc)
        Label statusLabel = new Label();
        if (clientSession.getLastStatusMessage() != null) {
            statusLabel.setText(clientSession.getLastStatusMessage());
        }

        //screen layout
        VBox root = new VBox(15, titleLabel,
                playerInfoBox,
                scoreLabel,
                roleLabel,
                difficultyLabel,
                turnLabel,
                turnStatusLabel,
                boardTitleLabel,
                boardGrid,
                statusLabel);

        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(25));

        // Soft background color
        root.setStyle("-fx-background-color: #eef3f7;");

        // Build a scene size that can grow depending on board size
        double sceneWidth = calculateSceneWidth();
        double sceneHeight = calculateSceneHeight();

        return new Scene(root, sceneWidth, sceneHeight);

    }
    //Builds a GridPane representation of the board using the
    //board text currently stored in the session.
    private GridPane buildBoardGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        String boardDisplay = clientSession.getBoardDisplay();

        // If board data has not arrived yet, return an empty grid
        if (boardDisplay == null || boardDisplay.isBlank()) {
            return gridPane;
        }

        String[] rowTexts = boardDisplay.split("\\|");

        for (int row = 0; row < rowTexts.length; row++) {
            String trimmedRow = rowTexts[row].trim();

            if (trimmedRow.isEmpty()) {
                continue;
            }

            String[] cells = trimmedRow.split("\\s+");

            for (int column = 0; column < cells.length; column++) {
                String cellValue = cells[column];

                Button cardButton = createCardButton(cellValue, row, column);

                gridPane.add(cardButton, column, row);
            }
        }

        return gridPane;
    }

    //Creates one visual card button.
    private Button createCardButton(String cellValue, int row, int column) {
        Button cardButton = new Button();

        // Hidden cards appear as "?"
        if ("*".equals(cellValue)) {
            cardButton.setText("?");
            cardButton.setStyle(
                    "-fx-background-color: #7fa7c9;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;"
            );
        } else {
            // Revealed cards show the actual symbol
            cardButton.setText(cellValue);
            cardButton.setStyle(
                    "-fx-background-color: #ffffff;" +
                            "-fx-text-fill: #222222;" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-color: #b0bec5;"
            );
        }

        // Make card size adapt to board dimensions
        double cardSize = calculateCardSize();
        cardButton.setMinSize(cardSize, cardSize);
        cardButton.setPrefSize(cardSize, cardSize);
        cardButton.setMaxSize(cardSize, cardSize);

        // A player should only click hidden cards on their own turn
        boolean shouldDisable =
                !isMyTurn() || !"*".equals(cellValue);

        cardButton.setDisable(shouldDisable);

        // Send FLIP command to the backend when the player clicks a hidden card
        cardButton.setOnAction(event ->
                clientSession.getClientConnection().sendMessage("FLIP " + row + " " + column)
        );

        return cardButton;
    }

    //Checks whether the current turn belongs to this player.
    private boolean isMyTurn() {
        if (clientSession.getUsername() == null || clientSession.getCurrentTurn() == null) {
            return false;
        }

        return clientSession.getUsername().equalsIgnoreCase(clientSession.getCurrentTurn());
    }

    //Calculates card size dynamically according to board dimensions.
    private double calculateCardSize() {
        int rows = Math.max(clientSession.getBoardRows(), 1);
        int columns = Math.max(clientSession.getBoardColumns(), 1);

        int maxDimension = Math.max(rows, columns);

        //card size on easy difficulty
        if (maxDimension <= 2) {
            return 90;
        }
        //card size on medium difficulty
        else if (maxDimension <= 4) {
            return 70;
        }
        //cards size on Hard difficulty
        else {
            return 55;
        }
    }

    //Calculates scene width based on the number of columns.
    private double calculateSceneWidth() {
        int columns = Math.max(clientSession.getBoardColumns(), 2);
        double cardSize = calculateCardSize();

        return Math.max(560, 180 + (columns * (cardSize + 12)));
    }

    //Calculates scene height based on the number of rows.
    private double calculateSceneHeight() {
        int rows = Math.max(clientSession.getBoardRows(), 2);
        double cardSize = calculateCardSize();

        return Math.max(500, 380 + (rows * (cardSize + 12)));
    }


    //helper to avoid showing null values in UI
    private String safeValue(String value){
        return value != null ? value : "not available yet";
    }


}//end class
