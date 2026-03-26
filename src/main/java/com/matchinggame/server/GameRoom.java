package com.matchinggame.server;

public class GameRoom {
    private final PlayerSession firstPlayer;
    private final PlayerSession secondPlayer;

    //constructor
    public GameRoom(PlayerSession firstPlayer, PlayerSession secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }
    public void startMatch(){
        //assign this room to both players
        firstPlayer.setGameRoom(this);
        secondPlayer.setGameRoom(this);

        //notify both player that the match is ready to start
        firstPlayer.sendMessage("MATCH_READY Opponent: " + secondPlayer.getUsername());
        secondPlayer.sendMessage("MATCH_READY Opponent: " + firstPlayer.getUsername());

        //assign roles
        firstPlayer.sendMessage("YOUR_ROLE PLAYER_ONE");
        secondPlayer.sendMessage("YOUR_ROLE PLAYER_TWO");

        //inform both players that a game room has started
        firstPlayer.sendMessage("GAME_ROOM_STARTED");
        secondPlayer.sendMessage("GAME_ROOM_STARTED");

        System.out.println("Game Room Started for " + firstPlayer.getUsername() + " Vs " + secondPlayer.getUsername());
    }
}//end class
