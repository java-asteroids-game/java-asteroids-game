package com.example.javaproject;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;


public class ScoreWindow {
    //game window size
    public static final int WIDTH = 500;
    public static final int HEIGHT = 400;

    public static void load(Stage stage) {
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);
        pane.setStyle("-fx-background-color: black");

        Text text0 = new Text(170, 80, "High Score");
        text0.setFill(Color.WHITE);
        text0.setStyle("-fx-font: 30 consolas;");
        pane.getChildren().add(text0);

        //displaying score
        ScoreManager ScoreManager = new ScoreManager();
        ArrayList<String> highscore = ScoreManager.outputThreeHighestScores(); //this will output the 3 highest scores, we just need an empty pane

        int y = 130; // y-coordinate for the Text objects
        for (String line : highscore) {
            y += 5;
            Text text1 = new Text(170, y, line);
            text1.setFill(Color.WHITE);
            text1.setStyle("-fx-font: 20 consolas;");
            pane.getChildren().add(text1);

            y += 30; // Increase y-coordinate for next Text object

            // Add bounce animation to the Text object
            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), text1);
            translateTransition.setFromY(0);
            translateTransition.setToY(20);
            translateTransition.setCycleCount(TranslateTransition.INDEFINITE); // Set the animation to repeat indefinitely
            translateTransition.setAutoReverse(true); // Set the animation to reverse direction after each cycle
            translateTransition.play();
        }
            Scene scene = new Scene(pane); // Create a new Scene with the Pane as its root
            stage.setScene(scene); // Set the Scene to the Stage
            stage.setTitle("High Score!!!"); // Set the title of the window
            stage.show(); // Display the Stage
    }

    public Scene showHighScoresScreen(Stage stage){
        StackPane scorePane = new StackPane();
        scorePane.setPrefSize(GameWindow.WIDTH, GameWindow.HEIGHT);
        scorePane.setStyle("-fx-background-color: black");

        Text highScoreTitle = new Text("High Score");
        highScoreTitle.setFill(Color.WHITE);
        highScoreTitle.setStyle("-fx-font: 30 consolas;");

        // Create a VBox to hold the score elements
        VBox scoreBox = new VBox();
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setSpacing(20); // Add some space between the elements

        // Add the title to the VBox
        scoreBox.getChildren().add(highScoreTitle);

        //displaying score
        ScoreManager ScoreManager = new ScoreManager();
        ArrayList<String> highscores = ScoreManager.outputThreeHighestScores(); //this will output the 3 highest scores, we just need an empty pane

        int y = 130; // y-coordinate for the Text objects
        for (String line : highscores) {
            Text highScore = new Text(line);
            highScore.setFill(Color.WHITE);
            highScore.setStyle("-fx-font: 20 consolas;");

            // Add bounce animation to the Text object
            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), highScore);
            translateTransition.setFromY(0);
            translateTransition.setToY(20);
            translateTransition.setCycleCount(TranslateTransition.INDEFINITE); // Set the animation to repeat indefinitely
            translateTransition.setAutoReverse(true); // Set the animation to reverse direction after each cycle
            translateTransition.play();

            scoreBox.getChildren().add(highScore);
        }

        Label restart = new Label("Click to Restart Game");
        restart.setStyle("-fx-font: 20 Consolas;");
        restart.setTextFill(Color.WHITE);
        restart.setTranslateY(150);

        scoreBox.getChildren().add(restart);

        // Add listener to detect when the user has pressed enter
        restart.setOnMouseClicked(event -> {
            StartPane.getInstance().toIndex();
        });
        // Add listener to detect when mouse is over label
        restart.setOnMouseEntered(event -> {
            restart.setOpacity(0.7);
        });
        restart.setOnMouseExited((event -> {
            restart.setOpacity((1.0));
        }));

        scorePane.getChildren().add(scoreBox);

        return new Scene(scorePane);

    }

}
