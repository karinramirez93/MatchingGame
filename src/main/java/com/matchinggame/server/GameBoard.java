package com.matchinggame.server;

public class GameBoard {
    //board of cards
    private final Card[][] cards;
    private final GameDifficulty gameDifficulty;
    private final int rowCount;
    private final int columnCount;

    //constructor
    //create a new board
    public GameBoard(GameDifficulty gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
        //difficulty Easy
        if(gameDifficulty == GameDifficulty.EASY){
            this.rowCount = 2;
            this.columnCount = 2;
        }
        //difficulty Medium
        else if (gameDifficulty == GameDifficulty.MEDIUM) {
            this.rowCount = 2;
            this.columnCount = 4;
        }
        //difficulty hard
        else {
            this.rowCount = 4;
            this.columnCount = 4;
        }
        //create the matrix after row/column values are known
        this.cards = new Card[rowCount][columnCount];
        initializeBoard();
    }

    public int getRowCount() {
        return rowCount;
    }
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * Initializes the board based on the selected difficulty.
     *
     * EASY     -> 2x2  -> 2 pairs
     * MEDIUM   -> 2x4  -> 4 pairs
     * HARD     -> 4x4  -> 8 pairs
     */
    private void initializeBoard(){
        if(gameDifficulty == GameDifficulty.EASY){
            initializeEasyBoard();
            return;
        }
        if(gameDifficulty == GameDifficulty.MEDIUM){
            initializeMediumBoard();
            return;
        }
        if(gameDifficulty == GameDifficulty.HARD){
            initializeHardBoard();
            return;
        }
    }
    private void initializeEasyBoard() {
            cards[0][0] = new Card("A");
            cards[0][1] = new Card("A");
            cards[1][0] = new Card("B");
            cards[1][1] = new Card("B");

    }
    private void initializeMediumBoard() {
        cards[0][0] = new Card("A");
        cards[0][1] = new Card("A");
        cards[0][2] = new Card("B");
        cards[0][3] = new Card("B");

        cards[1][0] = new Card("C");
        cards[1][1] = new Card("C");
        cards[1][2] = new Card("D");
        cards[1][3] = new Card("D");

    }
    private void initializeHardBoard() {
        cards[0][0] = new Card("A");
        cards[0][1] = new Card("A");
        cards[0][2] = new Card("B");
        cards[0][3] = new Card("B");

        cards[1][0] = new Card("C");
        cards[1][1] = new Card("C");
        cards[1][2] = new Card("D");
        cards[1][3] = new Card("D");

        cards[2][0] = new Card("E");
        cards[2][1] = new Card("E");
        cards[2][2] = new Card("F");
        cards[2][3] = new Card("F");

        cards[3][0] = new Card("G");
        cards[3][1] = new Card("G");
        cards[3][2] = new Card("H");
        cards[3][3] = new Card("H");

    }

    //checks whether the given row and column exist on the board
    public boolean isValidPosition(int row, int column){
        return row >= 0 && row < cards.length && column >= 0 && column < cards[row].length;
    }
    //return the card at the given position
    public Card getCard(int row, int column){
        return cards[row][column];
    }
    //reveals the card at the given position
    public void revealCard(int row, int column){
        cards[row][column].reveal();
    }

    //builds a text representation of the board
    //hidden cars are shown as "*"
    //revealed cards show their symbol
    public String getBoardDisplay(){
        StringBuilder boardText = new StringBuilder();

        for(int row = 0; row < cards.length; row++){
            for(int column = 0; column < cards[row].length; column++){
                if(cards[row][column].isRevealed()){
                    boardText.append(cards[row][column].getSymbol());
                }
                else{
                    boardText.append("*");
                }
                if(column < cards[row].length-1){
                    boardText.append(" ");
                }

            }
            if(row < cards.length-1){
                boardText.append(" | ");
            }
        }
        return boardText.toString();
    }
    //check whether all cards in the board have been matched
    //returns true if all cards are matched
    public boolean areAllCardsMatched(){
        for(int row = 0; row < cards.length; row++){
            for(int column = 0; column < cards[row].length; column++){
                if(!cards[row][column].isMatched()){
                    return false;
                }
            }
        }
        return true;
    }



}//end of class
