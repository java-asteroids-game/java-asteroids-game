package com.example.javaproject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;

public class Index {

    public static void load(Stage stage) {
        try {
            // loads the FXML file
            Parent root = FXMLLoader.load(Index.class.getResource("index.fxml"));
            // sets the root of the Scene of the given Stage as the loaded FXML 'root'
            stage.getScene().setRoot(root);
        }catch(IOException e) {
            // if exception e occurs, prints stack trace
            e.printStackTrace();
        }
    }
}
