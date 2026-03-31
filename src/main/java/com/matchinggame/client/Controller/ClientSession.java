package com.matchinggame.client.Controller;

import com.matchinggame.client.network.ClientConnection;

/**
 * stores shared client-side state across multiple screens
 *
 * stores the active network connection
 * store the current player's username
 * store the server message listener thread
 * */
public class ClientSession {
    private ClientConnection clientConnection;
    private String username;
    private Thread listeningThread;
    private String opponentUsername;
    private String lastStatusMessage;
    private String playerRole;
    private String confirmedDifficulty;
    private int boardRows;
    private int boardColumns;
    private String boardDisplay;
    private String currentTurn;
    private String currentScore;
    private String finalScore;
    private String winnerMessage;
    private boolean gameOver;
    private boolean hasRecentMatchResult;
    private String recentFinalScore;
    private String recentWinnerMessage;

    //returns the active client connection
    public ClientConnection getClientConnection() {
        return clientConnection;
    }
    //stores the active client connection
    public void setClientConnection(ClientConnection ClientConnection) {
        this.clientConnection = ClientConnection;
    }

    public void setOpponentUsername(String opponentUsername) {
        this.opponentUsername = opponentUsername;
    }

    public String getOpponentUsername() {
        return opponentUsername;
    }

    // returns the current username
    public String getUsername() {
        return username;

    }
    //stores the player's username
    public void setUsername(String username) {
        this.username = username;
    }
    // returns the background listener thread
    public Thread getListeningThread() {
        return listeningThread;
    }
    //stores the background listener thread
    public void setListenerThread(Thread listenerThread){
        this.listeningThread = listenerThread;
    }

    public String getLastStatusMessage() {
        return lastStatusMessage;
    }
    public void setLastStatusMessage(String lastStatusMessage) {
        this.lastStatusMessage = lastStatusMessage;
    }
    public String getPlayerRole() {
        return playerRole;
    }
    public void setPlayerRole(String playerRole) {
        this.playerRole = playerRole;
    }
    public String getConfirmedDifficulty() {
        return confirmedDifficulty;
    }
    public void setConfirmedDifficulty(String confirmedDifficulty) {
        this.confirmedDifficulty = confirmedDifficulty;
    }
    public String getBoardDisplay() {
        return boardDisplay;
    }
    public void setBoardDisplay(String boardDisplay) {
        this.boardDisplay = boardDisplay;
    }
    public String getCurrentTurn() {
        return currentTurn;
    }
    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }
    public int getBoardRows() {
        return boardRows;
    }
    public void setBoardRows(int boardRows) {
        this.boardRows = boardRows;
    }
    public int getBoardColumns() {
        return boardColumns;
    }
    public void setBoardColumns(int boardColumns) {
        this.boardColumns = boardColumns;
    }
    public String getCurrentScore() {
        return currentScore;
    }
    public void setCurrentScore(String currentScore) {
        this.currentScore = currentScore;
    }
    public String getFinalScore() {
        return finalScore;
    }
    public void setFinalScore(String finalScore) {
        this.finalScore = finalScore;
    }
    public String getWinnerMessage() {
        return winnerMessage;
    }
    public void setWinnerMessage(String winnerMessage) {
        this.winnerMessage = winnerMessage;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
    public boolean isHasRecentMatchResult() {
        return hasRecentMatchResult;
    }
    public void setHasRecentMatchResult(boolean hasRecentMatchResult) {
        this.hasRecentMatchResult = hasRecentMatchResult;
    }
    public String getRecentFinalScore() {
        return recentFinalScore;
    }
    public void setRecentFinalScore(String recentFinalScore) {
        this.recentFinalScore = recentFinalScore;
    }
    public String getRecentWinnerMessage() {
        return recentWinnerMessage;
    }
    public void setRecentWinnerMessage(String recentWinnerMessage) {
        this.recentWinnerMessage = recentWinnerMessage;
    }

}

