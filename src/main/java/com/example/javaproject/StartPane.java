package com.example.javaproject;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

// StartPane class represents the main application window and manages different scenes within the application
public class StartPane {

    // Window width
    public static final double WIDTH = 960;
    // Window height
    public static final double HEIGHT = 600;

    // Instance of the StartPane class
    private static StartPane instance = new StartPane();
    // Stage for the application
    private Stage stage;

    // Private constructor to enforce the pattern
    private StartPane() {}

    // Getter method to return the singleton instance of StartPane
    public static StartPane getInstance() {
        return instance;
    }

    // Method to create and set up the main application scene
    public void scene(Stage stage) {
        AnchorPane root = new AnchorPane(); // Create the root AnchorPane for the scene
        Scene scene = new Scene(root, WIDTH, HEIGHT); // Create a new Scene with the specified width and height
        stage.setTitle("Asteroids Game"); // Set the title of the stage
        stage.setResizable(false); // Disable resizing of the stage
        stage.setScene(scene); // Set the scene for the stage
        this.stage = stage; // Store the stage in the class instance variable
        toIndex(); // Load the index scene
        stage.show(); // Display the stage
    }

    // Method to load the index scene (e.g., main menu)
    public void toIndex() {
        Index.load(stage);
    }

    // Method to start the game and load the game window
    public void gameStart() {
        GameWindow game = new GameWindow();
        game.load(stage, 3);
    }

    // Method to display the score list
    public void scoreList(Stage stage) {
        ScoreWindow scoreWindow = new ScoreWindow();
        scoreWindow.load(stage);
    }

    // Method to display the controls for the game
    public void showControls(Stage stage) {
        ControlsWindow controls = new ControlsWindow();
        controls.load(stage, 12);
    }
}
