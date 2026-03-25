package com.matchinggame.server;


 //entry point for the matching game server application.
 // this class is responsible only for starting the server.
 

public class ServerMain {

	private static final int DEFAULT_PORT = 8989;

	public static void main(String [] args){

		int serverPort = DEFAULT_PORT;

		if(args.length >= 1){
			try{
				serverPort = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException NumberFormatException){
				System.out.println("Invalid port provided. Using default port 8989.");
			}
		}
		else{
			System.out.println("No port provided. using default port 8989.");
		}

		MatchingGameServer matchingGameServer = new MatchingGameServer(serverPort);
		matchingGameServer.startServer();

	}//end of main

}//end of class
