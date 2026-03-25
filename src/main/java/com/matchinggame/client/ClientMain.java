package com.matchinggame.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;

/**
 * Basic client that connects to the server
 * and listens for incoming messages
 * */

public class ClientMain {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8989;

    public static void main(String [] args){
        try{
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            System.out.println("Connected to server at " + SERVER_HOST + ":" + SERVER_PORT);

            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));

            Thread listenerThread = new Thread(() -> {
                try{
                    String message;
                    while((message = serverReader.readLine()) != null){
                        System.out.println("Server: " + message);
                    }
                }
                catch (IOException ioException){
                    System.out.println("Disconnected from server.");
                }
            });
            listenerThread.start();

            String userInput;
            while ((userInput = userInputReader.readLine()) != null){
                writer.println(userInput);
            }



        }
        catch (IOException ioException) {
            System.out.println("Unable to connect to server: " + ioException.getMessage());
        }
    }

}//end of class
