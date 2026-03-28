package com.matchinggame.server;

public class GameBoard {
    //board of cards
    private final Card[][] cards;
    private final GameDifficulty gameDifficulty;

    //constructor
    //create a new board
    public GameBoard(GameDifficulty gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
        this.cards = new Card[2][2];
        initializeBoard();
    }
    /**
     * Initializes the board based on the selected difficulty.
     *
     * For now, all difficulty levels use a small board for easier testing.
     * Later we can expand MEDIUM and HARD to larger boards with more pairs.
     */
    private void initializeBoard() {
        if (gameDifficulty == GameDifficulty.EASY) {
            cards[0][0] = new Card("A");
            cards[0][1] = new Card("A");
            cards[1][0] = new Card("B");
            cards[1][1] = new Card("B");
            return;
        }

        if (gameDifficulty == GameDifficulty.MEDIUM) {
            cards[0][0] = new Card("C");
            cards[0][1] = new Card("C");
            cards[1][0] = new Card("D");
            cards[1][1] = new Card("D");
            return;
        }

        if (gameDifficulty == GameDifficulty.HARD) {
            cards[0][0] = new Card("E");
            cards[0][1] = new Card("E");
            cards[1][0] = new Card("F");
            cards[1][1] = new Card("F");
        }
    }
    //checks whether the given row and column exist on the board
    //return true if the position is valid
    public boolean isValidPosition(int row, int column){
        var whatToReturn = row >=0 && row < cards.length && column >= 0 && column < cards[row].length;
        return whatToReturn;
    }
    //return the card at the given position
    public Card getCard(int row, int column){
        return cards[row][column];
    }
    //reveals the card at the given position
    public void revealCard(int row, int column){
        cards[row][column].reveal();
    }

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
    //check whether all cards in the board are matched
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
