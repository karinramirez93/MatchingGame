package com.matchinggame.server;

public class GameMenu {
    public static String getMainMenuText(){
       var menuPage = """ 
                   ------------ Main Menu -----------------
             
                               1. Start
                               2. Exit
                """;
        return menuPage;
    }
    public static String getDifficultyMenuText(){
        var difficultyPage = """
                ---------- Choose Difficulty --------------
                
                                1. EASY
                                2. MEDIUM
                                3. HARD
                """;
        return difficultyPage;
    }
}
