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
    private String boardDisplay;
    private String currentTurn;

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
}

