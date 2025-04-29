package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Utility.ClientSocket;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ClientSocket.getInstance().getSocket();
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
        primaryStage.setTitle("Condorcet");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
