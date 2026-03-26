package com.matchinggame.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * handles communication with one connected client.
 * each client runs in its own thread
 */

public class PlayerSession implements Runnable{

    private final Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username; // identify the player inside the game
    private boolean registered; // verify if the client has completed registration

    private final MatchingGameServer matchingGameServer; //notify when the player finishes registration

    public PlayerSession(Socket clientSocket, MatchingGameServer matchingGameServer) {
        this.clientSocket = clientSocket;
        this.matchingGameServer = matchingGameServer;

        try{
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);

        }
        catch(IOException ioException){
            System.out.println("Error setting up client streams: " + ioException.getMessage());
        }
    }//end PlayerSession

    public String getUsername() {
        return username;
    }
    //request a valid username from client before allowing normal communication with the server
    // format USERNAME playername
    // @throws IOException if the client disconnects during registration
    public void requestUsername() throws IOException {
        while(!registered){
            writer.println("REQUEST_USERNAME (format: USERNAME yourname)");

            String usernameMessage = reader.readLine();

            if(usernameMessage == null){
                throw new IOException("Client disconnected before username registration");
            }

            usernameMessage = usernameMessage.trim();

            //username format
            if(!usernameMessage.startsWith("USERNAME ")){
                writer.println("ERROR Invalid username format. use: USERNAME yourName");
                continue;
            }

            //extract the player name after registration
            String proposedUsername = usernameMessage.substring("USERNAME ".length()).trim();

            if (proposedUsername.isEmpty()){
                writer.println("ERROR Invalid username format (cannot be empty). use: USERNAME yourName");
                continue;
            }

            //registration succeeds once a non-empty username is received
            this.username = proposedUsername;
            this.registered = true;

            writer.println("USERNAME_ACCEPTED: " + username);
            System.out.println("Register player: " + username);

            //add player to lobby after registered to wait for an opponent
            matchingGameServer.addPlayerToLobby(this);
        }
    }

    public void run(){
        try{
            writer.println("Welcome to Matching Game Server");
            requestUsername();
            String incomingMessage;

            while((incomingMessage = reader.readLine()) != null){
                incomingMessage = incomingMessage.trim();
                System.out.println(username + " says: " + incomingMessage);
                writer.println("Server_ECHO: " + incomingMessage);
            }
        }
        catch (IOException ioException){
            System.out.println("Client connection error: " + ioException.getMessage());
        }
        finally{
            closeConnection();
        }

    }
    private void closeConnection(){
        try{
            if(clientSocket != null && !clientSocket.isClosed()){
                clientSocket.close();
            }
        }
        catch(IOException ioException){
            System.out.println("Error closing socket: " + ioException.getMessage());
        }
    }
    //send a message from the server to a client
    public void sendMessage(String message){
        if(writer != null){
            writer.println(message);
        }
    }


}// end class
