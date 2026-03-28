package com.matchinggame.client.UI;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private final Stage primaryStage;

    //manages scene transitions for the JavaFx Client
    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    //displays the provided scene on the primary stage
    public void showScene(Scene scene) {
        primaryStage.setScene(scene);
    }
    //returns teh primary stage
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
