package com.example.javaproject;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
public class ControlsWindow extends BaseGame {

    //implements abstract method load() from BaseGame
    //sets up and displays stage for the 'controls' feature
    public void load(Stage stage, int numAsteroids) {
        this.stage = stage;
        setupGame(numAsteroids);
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                animationHandle();
            }

        }.start();

        stage.setTitle("Controls");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }

    //sets up user interface elements (explanation of controls) for the CoontrolsWindow
    @Override
    protected List<Text> setupUITextElements(Pane pane){
        Text titleText = new Text(((double) WIDTH /2 -60), 80, "Controls");
        Text controlText = new Text(((double) WIDTH /2 -370), 440, "W / UP: Accelerate     A / LEFT: Turn Left     D / RIGHT: Turn Right");
        Text controlText1 = new Text(((double) WIDTH /2 - 200), 480, "Space: Shoot    Shift: Hyperspace Jump");
        Text exitText = new Text(((double) WIDTH /2 - 120), 520, "Press ESC for Main Screen");

        //an array of all text elements, iterate through the array to add text elements to pane
        List<Text> textElements = Arrays.asList(titleText, controlText, controlText1, exitText);

        //iterates through all textElements, sets color, font, opacity and adds to pane
        textElements.forEach(textElement -> {
            textElement.setFill(Color.WHITE);
            textElement.setStyle("-fx-font: 20 consolas;");
            textElement.setOpacity(.4);
            pane.getChildren().add(textElement);
        });

        titleText.setStyle("-fx-font: 30 consolas;");

        return textElements;
    }

    // overrides/extends the populateKeyHashmap() function from the superclass
    // the additional functionality is closing the ControlsWindow on pressing 'ESC'
    @Override
    protected void populateKeyHashMap(Scene scene){
        super.populateKeyHashMap(scene);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });

    }
}

