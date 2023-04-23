package com.example.javaproject;

import javafx.application.Application;
import javafx.stage.Stage;

// Open the game
// This is the main class of the application, it extends the JavaFX Application class
public class Main extends Application {

    // Start() method is the entry point for all JavaFX applications
    @Override
    public void start(Stage primaryStage) throws Exception {
        // The getInstance() method is called on the StartPane class
        // It is assumed to be a custom class that returns an instance of StartPane
        // The scene() method is then called on the StartPane instance, passing the primaryStage (the main window) as an argument
        // This sets up the scene (the content inside the main window) for the primaryStage
        StartPane.getInstance().scene(primaryStage);
    }

    // The main() method is the entry point for Java applications
    // It launches the JavaFX application by calling the launch() method and passing the command-line arguments
    public static void main(String[] args) {
        // The launch() method is responsible for starting the JavaFX application
        // (setting up the JavaFX runtime, initializing the application, and calling the start() method)
        launch(args);
    }
}