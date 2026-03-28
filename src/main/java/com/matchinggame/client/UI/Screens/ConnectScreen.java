package com.matchinggame.client.UI.Screens;

import com.matchinggame.client.UI.SceneManager;
import com.matchinggame.client.Controller.ClientSession;
import com.matchinggame.client.network.ClientConnection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.io.IOException;


public class ConnectScreen {

    private final SceneManager sceneManager;
    private final ClientSession clientSession;

    //constructor
    public ConnectScreen(SceneManager sceneManager, ClientSession clientSession) {

        this.sceneManager = sceneManager;
        this.clientSession = new ClientSession();
    }

    //create and returns the connection screen scene
    public Scene creatConnectScene() {
        //title
        Label titleLabel = new Label("Matching Game Client");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        //Host input
        Label hostLabel = new Label("Server Host: ");
        TextField hostField = new TextField("localhost");
        hostField.setPromptText("Enter server host");

        //port input
        Label portLabel = new Label("Server Port: ");
        TextField portField = new TextField("8989");
        portField.setPromptText("Enter server port");

        //Status message label (feedback to user)
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");

        //button to initiate connection
        Button connectButton = new Button("Connect");

        connectButton.setOnAction(event -> {
            String host = hostField.getText().trim();
            String portText = portField.getText().trim();

            //validate host before connecting to the server
            if (host.isEmpty()) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Host cannot be empty");
                return;
            }
            try{
                int port = Integer.parseInt(portText);

                //validate port range
                if(port <= 0 || port > 65535) {
                    statusLabel.setStyle("-fx-text-fill: red;");
                    statusLabel.setText("Port must be between 1 and 65535");
                    return;
                }
                //create a new connection object for current session
                ClientConnection clientConnection = new ClientConnection();
                clientConnection.connect(host, port);

                //save the active connection so future screen can use it
                clientSession.setClientConnection(clientConnection);

                //if no exception, connection succeeded message
                statusLabel.setStyle("-fx-text-fill: green;");
                statusLabel.setText("Connected to server successfully.");
                System.out.println("Connected to " + host + " : " + port);

                //move to the userName scrren after a successful connection
                sceneManager.showScene(new UsernameScreen(sceneManager, clientSession).createUserNameScene());
            }
            catch (NumberFormatException e) {
                //port is not a number
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Must be a valid port number");
            }
            catch (IOException e) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Could not connect to the server.");
                System.out.println("Connection error: " + e.getMessage());
            }
        });
        //layout container
        VBox root = new VBox(10,
                titleLabel,
                hostLabel,
                hostField,
                portLabel,
                portField,
                connectButton,
                statusLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));

        return new Scene(root, 420, 320);
    }
}
