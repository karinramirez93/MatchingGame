package com.matchinggame.server;

public class GameRoom {
    private final PlayerSession firstPlayer;
    private final PlayerSession secondPlayer;
    private GameBoard gameBoard;
    private PlayerSession currentTurnPlayer;
    private Card firstSelectedCard;
    private Card secondSelectedCard;
    private int firstRow;
    private int firstColumn;
    private int secondRow;
    private int secondColumn;
    private int firstPlayerScore = 0;
    private int secondPlayerScore =0;

    //constructor
    public GameRoom(PlayerSession firstPlayer, PlayerSession secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }
    public void startMatch(){
        //assign this room to both players
        firstPlayer.setGameRoom(this);
        secondPlayer.setGameRoom(this);

        //who goes first in the match once it starts
        currentTurnPlayer = firstPlayer;
        //create a new gameBoard
        gameBoard = new GameBoard();

        //notify both player that the match is ready to start
        firstPlayer.sendMessage("MATCH_READY Opponent: " + secondPlayer.getUsername());
        secondPlayer.sendMessage("MATCH_READY Opponent: " + firstPlayer.getUsername());

        //assign roles
        firstPlayer.sendMessage("YOUR_ROLE PLAYER_ONE");
        secondPlayer.sendMessage("YOUR_ROLE PLAYER_TWO");

        //inform both players that a game room has started
        firstPlayer.sendMessage("GAME_ROOM_STARTED");
        secondPlayer.sendMessage("GAME_ROOM_STARTED");

        broadcastMessage("BOARD " + gameBoard.getBoardDisplay());
        broadcastMessage("TURN " + currentTurnPlayer.getUsername());

        System.out.println("Game Room Started for " + firstPlayer.getUsername() + " Vs " + secondPlayer.getUsername());
    }

    public synchronized void handleFlipCommand(PlayerSession playerSession, int row, int column){
        if(playerSession != currentTurnPlayer){
            playerSession.sendMessage("Error it is not your turn");
            return;
        }
        if(!gameBoard.isValidPosition(row, column)){
            playerSession.sendMessage("Error Invalid board position");
            return;
        }

        Card selectedCard = gameBoard.getCard(row, column);

        if (selectedCard.isRevealed()){
            playerSession.sendMessage("Error it is already revealed");
            return;
        }

        //reveal card
        gameBoard.revealCard(row, column);

        broadcastMessage("CARD_FLIPPED " + playerSession.getUsername() + " " + row + " " + column + " " + selectedCard.getSymbol());

        broadcastMessage("BOARD " + gameBoard.getBoardDisplay());

        //fist Card
        if(firstSelectedCard == null){
            firstSelectedCard = selectedCard;
            firstRow = row;
            firstColumn = column;
            return; // saves it and returns first card info
        }
        //prevent selecting the same card position twice
        if(row == firstRow && column == firstColumn){
            playerSession.sendMessage("ERROR you cannot select the same card twice");
            return;
        }
        //second Card
        if(secondSelectedCard == null){
            secondSelectedCard = selectedCard;
            secondRow = row;
            secondColumn = column;

            //check cards if match each other
            if(firstSelectedCard.getSymbol().equals(secondSelectedCard.getSymbol())){
                broadcastMessage("MATCH_FOUND " + playerSession.getUsername());

                //marks both cards as permanently matched
                firstSelectedCard.markAsMatched();
                secondSelectedCard.markAsMatched();

                //update score for players
                if(playerSession == firstPlayer){
                    firstPlayerScore++;
                }
                else{
                    secondPlayerScore++;
                }
                //show score bar
                broadcastMessage("SCORE: " + firstPlayer.getUsername() + " " + firstPlayerScore + " Vs " + secondPlayer.getUsername() + " " + secondPlayerScore);

                //end game if all are matched
                if(gameBoard.areAllCardsRevealed()){
                    endGame();
                    return;
                }

                //reset selection current player keep the turn because player found a match on the cards
                firstSelectedCard = null;
                secondSelectedCard = null;
            }
            //if match is not found, flip back cards again
            else {
                broadcastMessage("NO_MATCH");

                //hide cards again
                firstSelectedCard.hide();
                secondSelectedCard.hide();
                //show the board with the card hidden again if match not found
                broadcastMessage("BOARD " + gameBoard.getBoardDisplay());

                //clear the selection state for the next turn
                firstSelectedCard = null;
                secondSelectedCard = null;

            switchTurn();
            broadcastMessage("Turn " + currentTurnPlayer.getUsername());

            }
        }
    }

    private void switchTurn(){
        if(currentTurnPlayer == firstPlayer){
            currentTurnPlayer = secondPlayer;
        }
        else{
            currentTurnPlayer = firstPlayer;
        }
    }
    private void broadcastMessage(String message){
        firstPlayer.sendMessage(message);
        secondPlayer.sendMessage(message);
    }
    public void endGame(){
        broadcastMessage("GAME OVER");
        broadcastMessage("FINAL SCORE: " + firstPlayer.getUsername() + " " +  firstPlayerScore + " Vs " + secondPlayer.getUsername() + " " + secondPlayerScore);

        if(firstPlayerScore > secondPlayerScore){
            broadcastMessage("WINNER is: " + firstPlayer.getUsername());
        }
        else if(secondPlayerScore > firstPlayerScore){
            broadcastMessage("WINNER is: " + secondPlayer.getUsername());
        }
        else{
            broadcastMessage("DRAW");
        }
    }




}//end class
