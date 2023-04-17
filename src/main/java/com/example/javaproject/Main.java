package com.example.javaproject;

import javafx.application.Application;
import javafx.stage.Stage;

// Open the game
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        StartPane.getInstance().scene(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}