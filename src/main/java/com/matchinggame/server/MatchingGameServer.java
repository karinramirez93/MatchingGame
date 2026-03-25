package com.matchinggame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//represents the multiplayer server for the Matching Game.
//stores the port and confirms startup

public class MatchingGameServer {

	private final int serverPort;
	private ServerSocket serverSocket;

	//constructor
	public MatchingGameServer(int serverPort){
		this.serverPort = serverPort;
	}

	//start the server and listens for incoming client connection	

	public void startServer(){
		try{
			serverSocket = new ServerSocket(serverPort);
			System.out.println("Server is listening on port: " + serverPort);

            //mutithreading
			while(true){
				Socket clientSocket = serverSocket.accept();
				System.out.println("New client connected from " + clientSocket.getInetAddress());
				PlayerSession playerSession = new PlayerSession(clientSocket);
				Thread playerThread = new Thread(playerSession);
				playerThread.start();
			}
		}
		catch (IOException ioException){
			System.out.println("Server Error: " + ioException.getMessage());
		}
	}
}
