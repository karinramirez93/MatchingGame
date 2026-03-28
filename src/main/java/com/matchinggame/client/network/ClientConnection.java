package com.matchinggame.client.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        //receives text messages coming from the server
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //sends text messages to the server
        writer = new PrintWriter(socket.getOutputStream(), true);


    }


    //sends a single message to the server
    public void sendMessage(String message){
        if(writer != null){
            writer.println(message);
        }
    }
    //reads messages sent by the server
    public String readMessage() throws IOException {
        if (reader != null) {
            return reader.readLine();
        }
        return null;
    }

    //check if the connection if currently active
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
    //close all network resources safely
    public void close() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ignored) {
        }
        if(writer != null){
            writer.close();
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }catch (IOException ignored) {

        }
    }
}
