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

    // constructor
    public GameRoom(PlayerSession firstPlayer, PlayerSession secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    //return the current state of the room
    public RoomState getRoomState(){
        return roomState;
    }

    //shows the main manu to both players
    public synchronized void showMainMenu() {
        roomState = RoomState.MAIN_MENU;

        firstPlayerMenuChoice = null;
        secondPlayerMenuChoice = null;

        firstPlayerDifficultyChoice = null;
        secondPlayerDifficultyChoice = null;

        firstPlayer.setGameRoom(this);
        secondPlayer.setGameRoom(this);

        firstPlayer.sendMessage("Room ready, your opponent is: " + secondPlayer.getUsername());
        secondPlayer.sendMessage("Room ready, your opponent is: " + firstPlayer.getUsername());

        broadcastMessage(GameMenu.getMainMenuText());

        System.out.println("Main menu shown for " + firstPlayer.getUsername() + " vs " + secondPlayer.getUsername());
    }
    // starts the match after both players agreed on difficulty
    public void startMatch(){
        //assign this room to both players
        firstPlayer.setGameRoom(this);
        secondPlayer.setGameRoom(this);

        roomState = RoomState.PLAYING; // start game

        firstPlayerMenuChoice = null;
        secondPlayerMenuChoice = null;

        firstPlayerDifficultyChoice = null;
        secondPlayerDifficultyChoice = null;

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
        firstPlayer.sendMessage("Game_room_started");
        secondPlayer.sendMessage("Game_room_started");

        broadcastMessage("Difficulty confirmed: " + currentDifficulty.name());
        broadcastMessage("Board " + gameBoard.getBoardDisplay());
        broadcastMessage("Turn " + currentTurnPlayer.getUsername());

        System.out.println("Game Room Started for " + firstPlayer.getUsername() + " Vs " + secondPlayer.getUsername());
    }
    private synchronized void showDifficultyMenu(){
        roomState = RoomState.DIFFICULTY_SELECTION;
        firstPlayerMenuChoice = null;
        secondPlayerMenuChoice = null;

        broadcastMessage(GameMenu.getDifficultyMenuText());
        System.out.println("Difficulty menu shown for " + firstPlayer.getUsername() + " Vs " + secondPlayer.getUsername());
    }
    //numeric main menu options
    // 1 - start
    // 2 - rematch
    // 3 - exit
    public synchronized void handleMenuOption(PlayerSession playerSession, String optionText){
        if(roomState != RoomState.MAIN_MENU){
            playerSession.sendMessage("ERROR Invalid menu option");
            return;
        }
        String normalizedOption = optionText.trim();

        switch (normalizedOption){
            case "1":
                storeMenuChoice(playerSession, "START");
                broadcastMessage("Menu selection " + playerSession.getUsername() + " START");

                if("START".equals(firstPlayerMenuChoice) && "START".equals(secondPlayerMenuChoice)){
                    showDifficultyMenu();
                }
                break;
            case "2":
                storeMenuChoice(playerSession, "REMATCH");
                broadcastMessage("MENU_SELECTION " + playerSession.getUsername() + " REMATCH");

                if ("REMATCH".equals(firstPlayerMenuChoice) && "REMATCH".equals(secondPlayerMenuChoice)) {
                    showDifficultyMenu();
                }
                break;

            case "3":
                storeMenuChoice(playerSession, "EXIT");
                broadcastMessage("MENU_SELECTION " + playerSession.getUsername() + " EXIT");
                broadcastMessage("SESSION_CLOSED " +  playerSession.getUsername());
                System.out.println("Session closed by: " + playerSession.getUsername());
                playerSession.disconedFromServer();
                break;

            default:
                playerSession.sendMessage("ERROR Invalid menu option. Use 1, 2, or 3");

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
        broadcastMessage("DIFFICULTY_SELECTION " + playerSession.getUsername() + " " + selectedDifficulty);

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
        if(roomState != RoomState.PLAYING){
            playerSession.sendMessage("Error the match is not currently active");
        }
        if(playerSession != currentTurnPlayer){
            playerSession.sendMessage("Not Your turn yet");
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
                broadcastMessage("Turn: " + currentTurnPlayer.getUsername());
                return;
            }
                broadcastMessage("No match");
                try{
                    Thread.sleep(1200);
                }
                catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                }
                firstSelectedCard.hide();
                secondSelectedCard.hide();

                broadcastMessage("Board " + gameBoard.getBoardDisplay());

                firstSelectedCard = null;
                secondSelectedCard = null;

                switchTurn();
                broadcastMessage("Turn: " + currentTurnPlayer.getUsername());

        }
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
        System.out.println("Game finished: " + firstPlayer.getUsername() + " " +  firstPlayerScore + " Vs " + secondPlayer.getUsername() + " " + secondPlayerScore);

        //display menu with options
        showMainMenu();
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




}//end class
