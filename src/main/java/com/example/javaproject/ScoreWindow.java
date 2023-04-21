package com.example.javaproject;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

    public void load(Stage stage) {
        Scene scene = createHighScoreScene(WIDTH, HEIGHT);
        stage.setScene(scene); // Set the Scene to the Stage
        stage.setTitle("High Score!!!"); // Set the title of the window
        stage.show(); // Display the Stage
    }

    public Scene showHighScoresScreen(Stage stage){
        return createHighScoreScene(GameWindow.WIDTH, GameWindow.HEIGHT);
    }

    private static Scene createHighScoreScene(int width, int height){
        StackPane scorePane = new StackPane();
        scorePane.setPrefSize(width, height);
        scorePane.setStyle("-fx-background-color: black");

        VBox scoreBox = createVBoxWithTitle();

        //displaying score
        ScoreManager ScoreManager = new ScoreManager();
        ArrayList<String> highscores = ScoreManager.outputThreeHighestScores(); //this will output the 3 highest scores, we just need an empty pane

        // Add list of highscores to the vbox
        addHighScoreListToVBox(highscores, scoreBox);

        // Create restart label, add it to the vbox
        addRestartLabelToVBox(scoreBox);

        // Add the vbox to the scorePane
        scorePane.getChildren().add(scoreBox);

        // Return the new scorePane
        return new Scene(scorePane);
    }

    private static VBox createVBoxWithTitle(){
        // Create the high score title
        Text highScoreTitle = new Text("High Score");
        highScoreTitle.setFill(Color.WHITE);
        highScoreTitle.setStyle("-fx-font: 30 consolas;");

        // Create a VBox to hold the score elements
        VBox VBoxToAppend = new VBox();
        VBoxToAppend.setAlignment(Pos.CENTER);
        VBoxToAppend.setSpacing(20); // Add some space between the elements

        // Add the title to the VBox
        VBoxToAppend.getChildren().add(highScoreTitle);

        return VBoxToAppend;

    }

    private static void addHighScoreListToVBox(ArrayList<String> highScores, VBox VBoxToAppend) {
        for (String line : highScores) {
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

            VBoxToAppend.getChildren().add(highScore);
        }
    }

    private static void addRestartLabelToVBox(VBox VBoxToAppend){
            Label restart = new Label("Click to Restart Game");
            restart.setStyle("-fx-font: 20 Consolas;");
            restart.setTextFill(Color.WHITE);
            restart.setTranslateY(150);

            VBoxToAppend.getChildren().add(restart);

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

        }

}
