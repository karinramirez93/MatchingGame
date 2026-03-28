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
    //stores the active game once a match starts
    private GameRoom gameRoom;
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
    // assign the active game room to current players
    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }
    //return the active game room for this player
    public GameRoom getGameRoom() {
        return gameRoom;
    }


    //request a valid username from client before allowing normal communication with the server
    // format USERNAME playername
    // @throws IOException if the client disconnects during registration
    public void requestUsername() throws IOException {
        while(!registered){
            sendMessage("REQUEST_USERNAME (format: USERNAME yourname)");

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

            sendMessage("USERNAME_ACCEPTED: " + username);
            System.out.println("Register player: " + username);

            //add player to lobby after registered to wait for an opponent
            matchingGameServer.addPlayerToLobby(this);
        }
    }

    @Override
    public void run(){
        try{
           sendMessage("Welcome to Matching Game Server");
            requestUsername();
            String incomingMessage;

            while((incomingMessage = reader.readLine()) != null){
                incomingMessage = incomingMessage.trim();

                //real game move while playing
                if(incomingMessage.startsWith("FLIP ")){
                    if(gameRoom == null){
                        sendMessage("ERROR you are not inside a game room yet");
                        continue;
                    }
                    String[] messageParts = incomingMessage.split("\\s+");

                    if(messageParts.length != 3){
                        sendMessage("Error invalid flip format. use: flip row column");
                        continue;
                    }
                    try{
                        int row = Integer.parseInt(messageParts[1]);
                        int column = Integer.parseInt(messageParts[2]);

                        System.out.println(username + " attempted flip at (" + row + ", " + column + ")");
                        gameRoom.handleFlipCommand(this, row, column);
                    }
                    catch(NumberFormatException numberFormatException){
                        sendMessage("ERROR row and column must be numbers");
                    }
                    continue;
                }
                //numeric options depending on current room state
                if(gameRoom != null){
                    RoomState currentRoomState = gameRoom.getRoomState();

                    //numeric option for main menu
                    if(currentRoomState == RoomState.MAIN_MENU){
                        if(incomingMessage.equals("1") ||  incomingMessage.equals("2") || incomingMessage.equals("3")){
                            System.out.println(username + "Selected main menu options: " + incomingMessage);
                            gameRoom.handleMenuOption(this, incomingMessage);
                            continue;
                        }
                        sendMessage("Error invalid menu option. use 1, 2 , 3");
                        continue;

                    }
                    //numeric option for difficulty menu
                    if(currentRoomState == RoomState.DIFFICULTY_SELECTION){
                        if(incomingMessage.equals("1") ||  incomingMessage.equals("2") || incomingMessage.equals("3")){
                            System.out.println(username + "Selected difficulty options: " + incomingMessage);
                            gameRoom.handleDifficultyOption(this, incomingMessage);
                            continue;
                        }
                        sendMessage("Error invalid difficulty option. use 1, 2 , 3");
                        continue;

                    }
                }
                // tempp fallback while still developing
                System.out.println((username + "says: " + incomingMessage));
                sendMessage("SERVER_ECHO: " + incomingMessage);
            }
        }
        catch (IOException ioException){
            System.out.println("Client connection error: " + ioException.getMessage());
        }
        finally{
            closeConnection();
        }

    }
    //closes the client connection
    private void closeConnection(){
        try{
            if(clientSocket != null && !clientSocket.isClosed()){
                System.out.println("closing session for player: " +  username);
                clientSocket.close();
            }
        }
        catch(IOException ioException){
            System.out.println("Error closing socket: " + ioException.getMessage());
        }
    }
    public void disconedFromServer(){
        try{
            if(clientSocket != null && !clientSocket.isClosed()){
                closeConnection();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //send a message from the server to a client
    public void sendMessage(String message){
        if(writer != null){
            writer.println(message);
        }
    }


}// end class
