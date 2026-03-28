package com.matchinggame.client.Controller;

import com.matchinggame.client.network.ClientConnection;

public class ClientSession {
    private ClientConnection clientConnection;
    private String username;

    public ClientConnection getClientConnection() {
        return clientConnection;
    }
    public void setClientConnection(ClientConnection ClientConnection) {
        this.clientConnection = ClientConnection;
    }
    public String getUsername() {
        return username;

    }
    public void setUsername(String username) {
        this.username = username;
    }
}
