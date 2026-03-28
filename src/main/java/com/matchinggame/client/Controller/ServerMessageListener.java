package com.matchinggame.client.Controller;

import com.matchinggame.client.network.ClientConnection;
import java.io.IOException;
import java.util.function.Consumer;

public class ServerMessageListener implements Runnable{
    private final ClientConnection clientConnection;
    private final Consumer<String> messageConsumer;
    private volatile boolean running;

    public ServerMessageListener (ClientConnection clientConnection, Consumer<String> messageConsumer) {
        this.clientConnection = clientConnection;
        this.messageConsumer = messageConsumer;
        this.running = true;
    }

    //continously reads messages from the server
    //until the connection closes or the listener is stopped
    @Override
    public void run(){
        try{
            while (running && clientConnection.isConnected()){
                String message = clientConnection.readMessage();

                //if the server closes the connection, stop listening
                if(message == null){
                    stop();
                    break;
                }
                //pass the message to the provided handler
                messageConsumer.accept(message);
            }
        }
        catch (IOException e){
            System.out.println("Listener stopped due to connection error: " + e.getMessage());

        }
    }
    //stop the listener loop
    public void stop(){
        running = false;
    }

}
