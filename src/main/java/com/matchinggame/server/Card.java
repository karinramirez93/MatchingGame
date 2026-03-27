package com.matchinggame.server;

/**
 * represents one card on the matching board
 * A card has:
 *          - a symbol
 *          - a revealed state
 */

public class Card {
    private final String symbol; // value shown when the card is revealed
    private boolean revealed; // check if the card is velealed

    public Card(String symbol) {
        this.symbol = symbol;
        this.revealed = false;
    }

    public String getSymbol() {
        return symbol;
    }
    //check if the card has been reveled
    public boolean isRevealed() {
        return revealed;
    }

    //reveals card
    public void reveal(){
        this.revealed = true;
    }
    public void hide(){
        this.revealed = false;
    }
}//end class
