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

    public PlayerSession(Socket clientSocket) {
        this.clientSocket = clientSocket;

        try{
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);

        }
        catch(IOException ioException){
            System.out.println("Error setting up client streams: " + ioException.getMessage());
        }
    }//end PlayerSession
    public void run(){
        try{
            writer.println("Welcome to Matching Game Server");
            String incomingMessage;

            while((incomingMessage = reader.readLine()) != null){
                System.out.println("client says: " + incomingMessage);
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


}// end class
