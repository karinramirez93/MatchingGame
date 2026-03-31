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
    private int firstPlayerScore;
    private int secondPlayerScore;
    private RoomState roomState; // track the current state of the room or game (isAlive or match is over)
    //stores the current selected difficulty for the next match
    private String firstPlayerMenuChoice;
    private String secondPlayerMenuChoice;

    //stores each player's current difficulty choice
    private GameDifficulty firstPlayerDifficultyChoice;
    private GameDifficulty secondPlayerDifficultyChoice;
    private GameDifficulty currentDifficulty = GameDifficulty.EASY; // set up initial game difficulty
    //indicate when teh room is still active
    // once closed, the room should ignore further actions
    private boolean roomClosed;
    private final MatchingGameServer matchingGameServer;

    // constructor
    public GameRoom(PlayerSession firstPlayer, PlayerSession secondPlayer, MatchingGameServer matchingGameServer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.matchingGameServer = matchingGameServer;
    }

    //return the current state of the room
    public RoomState getRoomState(){
        return roomState;
    }
    public synchronized void handlePlayerDisconnect(PlayerSession disconnectedPlayer){
        if(roomClosed){
            return;
        }
        roomClosed = true;

        PlayerSession remainingPlayer;

        if(disconnectedPlayer == firstPlayer){
            remainingPlayer = secondPlayer;
        }
        else{
            remainingPlayer = firstPlayer;
        }
        if(remainingPlayer != null){
            remainingPlayer.sendMessage("OPPONENT_DISCONNECTED --> " + disconnectedPlayer.getUsername());
            remainingPlayer.sendMessage("ROOM_CLOSED");
            //remainingPlayer.sendMessage(null);
//            try{
//                Thread.sleep(2000);
//            }
//            catch (InterruptedException e){
//                Thread.currentThread().interrupt();
//            }
            System.out.println("Room closed because player disconnected: " + disconnectedPlayer.getUsername());

        }
        matchingGameServer.returnPlayerToLobby(remainingPlayer);

    }

    //shows the main manu to both players
    public synchronized void showMainMenu() {
        if(roomClosed){
            return;
        }
        roomState = RoomState.MAIN_MENU;

        //reset menu and difficulty choices from any previous round
        firstPlayerMenuChoice = null;
        secondPlayerMenuChoice = null;
        firstPlayerDifficultyChoice = null;
        secondPlayerDifficultyChoice = null;

        //attach this room to both players
        firstPlayer.setGameRoom(this);
        secondPlayer.setGameRoom(this);

        //tell each client who their opponent is
        firstPlayer.sendMessage("STATE:ROOM_READY: " + secondPlayer.getUsername());
        secondPlayer.sendMessage("STATE:ROOM_READY: " + firstPlayer.getUsername());

        broadcastMessage(GameMenu.getMainMenuText());

        System.out.println("Main menu shown for " + firstPlayer.getUsername() + " vs " + secondPlayer.getUsername());
    }
    // starts the match after both players agreed on difficulty
    public void startMatch(){
        if(roomClosed){
            return;
        }
        //assign this room to both players
        firstPlayer.setGameRoom(this);
        secondPlayer.setGameRoom(this);

        roomState = RoomState.PLAYING; // start game

        //reset menu and difficulty state for the new match
        firstPlayerMenuChoice = null;
        secondPlayerMenuChoice = null;
        firstPlayerDifficultyChoice = null;
        secondPlayerDifficultyChoice = null;

        //reset score and selected cards
        firstPlayerScore = 0;
        secondPlayerScore = 0;
        firstSelectedCard = null;
        secondSelectedCard = null;

        //who goes first in the match once it starts
        currentTurnPlayer = firstPlayer;
        //create a new gameBoard with default difficulty (easy)
        gameBoard = new GameBoard(currentDifficulty);

        //notify both player that the match is ready to start
        firstPlayer.sendMessage("MATCH_READY Opponent: " + secondPlayer.getUsername());
        secondPlayer.sendMessage("MATCH_READY Opponent: " + firstPlayer.getUsername());

        //assign roles
        firstPlayer.sendMessage("YOUR_ROLE PLAYER_ONE");
        secondPlayer.sendMessage("YOUR_ROLE PLAYER_TWO");

        //inform both players that a game room has started
        firstPlayer.sendMessage("GAME_ROOM_STARTED");
        secondPlayer.sendMessage("GAME_ROOM_STARTED");

        //tell the frontend how many rows and columns th board has
        broadcastMessage("BOARD_SIZE " + gameBoard.getRowCount() + " " + gameBoard.getColumnCount());

        //set initial game state
        broadcastMessage("DIFFICULTY_CONFIRMED: " + currentDifficulty.name());
        broadcastMessage("BOARD " + gameBoard.getBoardDisplay());
        broadcastMessage("TURN " + currentTurnPlayer.getUsername());

        System.out.println("Game Room Started for " + firstPlayer.getUsername() + " Vs " + secondPlayer.getUsername());
    }
    private synchronized void showDifficultyMenu(){
        if(roomClosed){
            return;
        }
        roomState = RoomState.DIFFICULTY_SELECTION;
        firstPlayerDifficultyChoice = null;
        secondPlayerDifficultyChoice = null;

        broadcastMessage(GameMenu.getDifficultyMenuText());
        System.out.println("Difficulty menu shown for " + firstPlayer.getUsername() + " Vs " + secondPlayer.getUsername());
    }
    //numeric main menu options
    // 1 - start
    // 2 - exit
    public synchronized void handleMenuOption(PlayerSession playerSession, String optionText){
        if(roomClosed){
            playerSession.sendMessage("ERROR, ROOM_IS_CLOSED");
            return;
        }

        if(roomState != RoomState.MAIN_MENU){
            playerSession.sendMessage("ERROR, Invalid menu option");
            return;
        }
        String normalizedOption = optionText.trim();

        switch (normalizedOption){
            case "1":
                storeMenuChoice(playerSession, "START");
                //notify both players about the selection
                broadcastMessage("MENU_SELECTION " + playerSession.getUsername() + " START");
                //move to difficulty selection only if both players pressed "start"
                if("START".equals(firstPlayerMenuChoice) && "START".equals(secondPlayerMenuChoice)){
                    showDifficultyMenu();
                }
                break;

            case "2":
                storeMenuChoice(playerSession, "EXIT");
                broadcastMessage("MENU_SELECTION " + playerSession.getUsername() + " EXIT");
                broadcastMessage("SESSION_CLOSED " +  playerSession.getUsername());
                System.out.println("Session closed by: " + playerSession.getUsername());
                playerSession.disconnectFromServer();
                break;

            default:
                playerSession.sendMessage("Error Invalid menu option. Use 1, or 2");

        }
    }
    //stores the menu option selected from players
    private void storeMenuChoice(PlayerSession playerSession, String option){
        if(playerSession == firstPlayer){
            firstPlayerMenuChoice = option;
        }else if(playerSession == secondPlayer){
            secondPlayerMenuChoice = option;
        }
    }

    //numeric difficulty menu options
    // 1 - Easy
    // 2 - medium
    // 3 - hard
    public synchronized void handleDifficultyOption(PlayerSession playerSession, String difficultyText){
        if(roomClosed){
            playerSession.sendMessage("ERROR, ROOM_IS_CLOSED");
            return;
        }

        if(roomState != RoomState.DIFFICULTY_SELECTION){
            playerSession.sendMessage("ERROR Invalid difficulty option");
            return;
        }
        GameDifficulty selectedDifficulty;

        //read user difficulty selected by on the difficulty menu options
        switch(difficultyText.trim()){
            case "1":
                selectedDifficulty = GameDifficulty.EASY;
                break;
            case "2":
                selectedDifficulty = GameDifficulty.MEDIUM;
                break;
            case "3":
                selectedDifficulty = GameDifficulty.HARD;
                break;
            default:
                playerSession.sendMessage("ERROR Invalid difficulty option. Use 1, 2, or 3");
                return;

        }

        if(playerSession == firstPlayer){
            firstPlayerDifficultyChoice = selectedDifficulty;
        }
        else if(playerSession == secondPlayer){
            secondPlayerDifficultyChoice = selectedDifficulty;
        }
        //notify both players which difficulty was selected
        broadcastMessage("DIFFICULTY_SELECTION: " + playerSession.getUsername() + " " + selectedDifficulty);

        //start the match only if both players selected the same difficulty
        if(firstPlayerDifficultyChoice != null && secondPlayerDifficultyChoice != null){
            if(firstPlayerDifficultyChoice == secondPlayerDifficultyChoice){
                currentDifficulty = firstPlayerDifficultyChoice;
                startMatch();

            }
            else{
                broadcastMessage("DIFFICULTY_MISMATCH");
                showDifficultyMenu();
            }
        }
    }

    //handles one FLIP command while the room is in PLAYING state
    public synchronized void handleFlipCommand(PlayerSession playerSession, int row, int column){
        if(roomClosed){
            playerSession.sendMessage("ERROR, ROOM_IS_CLOSED");
            return;
        }
        if(roomState != RoomState.PLAYING){
            playerSession.sendMessage("ERROR the match is not currently active");
            return;
        }
        if(playerSession != currentTurnPlayer){
            playerSession.sendMessage("NOT_YOUR_TURN");
            return;
        }

        if(!gameBoard.isValidPosition(row, column)){
            playerSession.sendMessage("ERROR Invalid board position");
            return;
        }

        Card selectedCard = gameBoard.getCard(row, column);

        if (selectedCard.isRevealed()){
            playerSession.sendMessage("ERROR it is already revealed");
            return;
        }

        //reveal card
        gameBoard.revealCard(row, column);

        //broadcastMessage("Card flipped " + playerSession.getUsername() + " " + row + " " + column + " " + selectedCard.getSymbol());

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
        if(secondSelectedCard == null) {
            secondSelectedCard = selectedCard;
            secondRow = row;
            secondColumn = column;

            //check cards if match each other
            if (firstSelectedCard.getSymbol().equals(secondSelectedCard.getSymbol())) {
                broadcastMessage("MATCH_FOUND " + playerSession.getUsername());

                //marks both cards as permanently matched
                firstSelectedCard.markAsMatched();
                secondSelectedCard.markAsMatched();

                //update score for players
                if (playerSession == firstPlayer) {
                    firstPlayerScore++;
                } else {
                    secondPlayerScore++;
                }
                //show score bar
                broadcastMessage("SCORE: " + firstPlayer.getUsername() + " " + firstPlayerScore + " Vs " + secondPlayer.getUsername() + " " + secondPlayerScore);

                //reset selection current player keep the turn because player found a match on the cards
                firstSelectedCard = null;
                secondSelectedCard = null;

                //end game if all are matched
                if (gameBoard.areAllCardsMatched()) {
                    endGame();
                    return;

                }
                broadcastMessage("TURN " + currentTurnPlayer.getUsername());
                return;
            }
                broadcastMessage("NO_MATCH");
                try{
                    Thread.sleep(1200);
                }
                catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
                firstSelectedCard.hide();
                secondSelectedCard.hide();

                broadcastMessage("BOARD " + gameBoard.getBoardDisplay());

                firstSelectedCard = null;
                secondSelectedCard = null;

                switchTurn();
                broadcastMessage("TURN " + currentTurnPlayer.getUsername());

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

    public void endGame(){


        broadcastMessage("GAME_OVER");
        broadcastMessage("FINAL_SCORE: " + firstPlayer.getUsername() + " " +  firstPlayerScore + " Vs " + secondPlayer.getUsername() + " " + secondPlayerScore);

        if(firstPlayerScore > secondPlayerScore){
            broadcastMessage("WINNER_IS: " + firstPlayer.getUsername());
        }
        else if(secondPlayerScore > firstPlayerScore){
            broadcastMessage("WINNER_IS: " + secondPlayer.getUsername());
        }
        else{
            broadcastMessage("DRAW");
        }
        System.out.println("GAME_FINISHED: " + firstPlayer.getUsername() + " " +  firstPlayerScore + " Vs " + secondPlayer.getUsername() + " " + secondPlayerScore);

        //display menu with options
        showMainMenu();
    }


    private void broadcastMessage(String message){
        firstPlayer.sendMessage(message);
        secondPlayer.sendMessage(message);
    }




}//end class
