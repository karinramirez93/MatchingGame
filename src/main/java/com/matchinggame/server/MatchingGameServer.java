package com.matchinggame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//represents the multiplayer server for the Matching Game.
//stores the port and confirms startup

public class MatchingGameServer {
	// port used by the server socket
	private final int serverPort;
	//main socket that listen for incoming client connection
	private ServerSocket serverSocket;

	//lobby (stores registered players & waiting for a match)
	private final List<PlayerSession> waitingPlayers;

	//constructor
	public MatchingGameServer(int serverPort){
		this.serverPort = serverPort;
		this.waitingPlayers = new ArrayList<>();
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
				PlayerSession playerSession = new PlayerSession(clientSocket, this);
				Thread playerThread = new Thread(playerSession);
				playerThread.start();
			}
		}
		catch (IOException ioException){
			System.out.println("Server Error: " + ioException.getMessage());
		}
	}

	/**
	 * adds a registered player to the waiting looby.
	 * once two players are available, the server pairs them.
	 */
	public synchronized void addPlayerToLobby(PlayerSession playerSession){
		waitingPlayers.add(playerSession);


		System.out.println("Player added to lobby: " + playerSession.getUsername());

		if(waitingPlayers.size() == 1){
			playerSession.sendMessage("STATE:WAITING");
			//System.out.println("Waiting for player to start");
			return;
		}
		if(waitingPlayers.size() >= 2){
			PlayerSession firstPlayer = waitingPlayers.remove(0);
			PlayerSession secondPlayer = waitingPlayers.remove(0);

			System.out.println("Match found: " + firstPlayer.getUsername() + " VS " + secondPlayer.getUsername());

			GameRoom gameRoom = new GameRoom(firstPlayer, secondPlayer, this);
			gameRoom.showMainMenu();
		}
	}
	// remove player from waiting lobby if present
	public synchronized void removePlayerFromLobby(PlayerSession playerSession){
		if(waitingPlayers.remove(playerSession)){
			System.out.println("Player removed from lobby: " + playerSession.getUsername());
		}
	}
	public synchronized void returnPlayerToLobby(PlayerSession playerSession){
		if(playerSession == null){
			return;
		}
		if(waitingPlayers.contains(playerSession)){
			return;
		}
		playerSession.setGameRoom(null);
		waitingPlayers.add(playerSession);

		System.out.println("Player returned to lobby: " + playerSession.getUsername());
		playerSession.sendMessage("RETURNING_TO_LOBBY");
		playerSession.sendMessage("WAITING_FOR_PLAYER");
	}

}
