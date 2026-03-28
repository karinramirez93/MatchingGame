package com.matchinggame.client;
import com.matchinggame.client.Controller.ClientSession;
import com.matchinggame.client.UI.SceneManager;
import com.matchinggame.client.UI.Screens.ConnectScreen;
import javafx.application.Application;
import javafx.stage.Stage;

//entry point for the JavaFx client application
public class ClientApp extends Application {
    //first screen of the app
    @Override
    public void start(Stage primaryStage){
        //scene manager, switching between screens
        SceneManager sceneManager = new SceneManager(primaryStage);

        // shared state used by multiple screens
        ClientSession clientSession = new ClientSession();

        primaryStage.setTitle("Matching Game Client");
        sceneManager.showScene(new ConnectScreen(sceneManager, clientSession).createConnectScene());
        //display the window
        primaryStage.show();

    }
    public static void main(String[] args) {
        launch(args);
    }

}
