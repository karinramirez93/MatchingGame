package com.matchinggame.server;

public class GameBoard {
    //board of cards
    private final Card[][] cards;

    //constructor
    //create a new board
    public GameBoard(){
        this.cards = new Card[2][2];
        initializeBoard();
    }
    private void initializeBoard(){
        cards[0][0] = new Card ("A");
        cards[0][1] = new Card ("A");
        cards[1][0] = new Card ("B");
        cards[1][1] = new Card ("B");

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



}//end of class
