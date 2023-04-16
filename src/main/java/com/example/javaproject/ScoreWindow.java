package com.example.javaproject;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ScoreWindow {
    //game window size
    public static final int WIDTH = 500;
    public static final int HEIGHT = 400;

    public static void load(Stage stage) {
        javafx.scene.layout.Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);
        pane.setStyle("-fx-background-color: black");

        Text text0 = new Text(50, 50, "High Score");
        text0.setFill(Color.WHITE);
        text0.setStyle("-fx-font: 30 arial;");
        pane.getChildren().add(text0);


        //displaying score
        scoreManager ScoreManager = new scoreManager();
        ArrayList<String> highscore = ScoreManager.outputThreeHighestScores(); //this will output the 3 highest scores, we just need an empty pane

        // Create Text objects for each line in the highscore
        int y = 100; // y-coordinate for the Text objects
        for (String line : highscore) {
            Text text = new Text(50, y, line);
            text.setFill(Color.WHITE);
            text.setStyle("-fx-font: 20 arial;");
            pane.getChildren().add(text);
            y += 30; // Increase y-coordinate for next Text object
        }

        Scene scene = new Scene(pane); // Create a new Scene with the Pane as its root
        stage.setScene(scene); // Set the Scene to the Stage
        stage.setTitle("High Score!!!"); // Set the title of the window
        stage.show(); // Display the Stage



    }
}
