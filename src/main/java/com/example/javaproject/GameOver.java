package com.example.javaproject;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameOver {

    ScoreManager scoreManager;
    int points;


    public void displayHighScores(TextField playerNameField){
        String playerName = playerNameField.getText().trim();

        if (!playerName.isEmpty()) {
            // Call the ScoreManager.appendScore() method
            scoreManager.appendScoreToFile(playerName, this.points);
            // Hide the input box
            playerNameField.setVisible(false);
        }
    }


    public Scene showGameOverScreen(Stage stage, int points) {
        this.points = points;

        // Create a black-colored pane to cover the entire screen
        StackPane gameOverPane = new StackPane();
        gameOverPane.setPrefSize(GameWindow.WIDTH, GameWindow.HEIGHT);
        gameOverPane.setPrefSize(GameWindow.WIDTH, GameWindow.HEIGHT);
        gameOverPane.setStyle("-fx-background-color: black");
        gameOverPane.setStyle("-fx-background-color: black;");

        // Create a VBox to hold the label and TextField
        VBox gameOverContainer = new VBox();
        gameOverContainer.setSpacing(20); // Add some space between label and TextField
        gameOverContainer.setAlignment(Pos.CENTER);

        // Create a "Game Over" label
        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setStyle("-fx-font: 65 consolas;");
        gameOverLabel.setTextFill(Color.WHITE);
        gameOverContainer.setAlignment(Pos.CENTER);

        // Create a TextField for user input
        TextField playerNameField = new TextField();
        playerNameField.setPromptText("Enter your name");
        playerNameField.setStyle("-fx-font: 30 consolas; " +
                "-fx-text-fill: white; " +
                "-fx-background-color: DARKGRAY; " +
                "-fx-border-color: transparent; " +
                "-fx-border-width: 0;");
        playerNameField.setMaxWidth(300);

        // Add listener to detect when the user has pressed enter
        playerNameField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Scene highScoresScene = new ScoreWindow().showHighScoresScreen(stage);
                stage.setScene(highScoresScene);
            }
        });

        // Add the "Game Over" label to the pane
        gameOverContainer.getChildren().addAll(gameOverLabel, playerNameField);

        // Add the VBox to the pane
        gameOverPane.getChildren().add(gameOverContainer);


        // Animate the pane by changing its opacity from 0 to 1 and back to 0 repeatedly
        Timeline animation = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> gameOverLabel.setOpacity(0.5)),
                new KeyFrame(Duration.seconds(0.25), event -> gameOverLabel.setOpacity(1.0))
        );

        animation.setCycleCount(3);
        animation.play();

        return new Scene(gameOverPane);
        }



}
