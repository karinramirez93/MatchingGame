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
    private boolean matched;

    public Card(String symbol) {
        this.symbol = symbol;
        this.revealed = false;
        this.matched = false;
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
    public void markAsMatched(){
        this.matched = true;
        this.revealed = true; // always visible after match
    }
    public boolean isMatched(){
        return matched;
    }
    public void hide(){
        if(!matched){
            this.revealed = false;
        }

    }
}//end class
