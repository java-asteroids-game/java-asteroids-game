package com.example.javaproject;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;
public class Controls {

    //game window size
    public static final int WIDTH = 960;
    public static final int HEIGHT = 600;
    static int framesSinceLastShot = 0;
    public static void load(Stage stage) {
        javafx.scene.layout.Pane pane1 = new Pane();
        pane1.setPrefSize(WIDTH, HEIGHT);
        pane1.setStyle("-fx-background-color: black");

        Text titleText = new Text((WIDTH/2 -60), 80, "Controls");
        titleText.setFill(Color.WHITE);
        titleText.setStyle("-fx-font: 30 arial;");
        titleText.setOpacity(.4);
        pane1.getChildren().add(titleText);

        Text controlText = new Text((WIDTH/2 -180), 440, "W: Accelerate  A:Turn Left  D: Turn Right");
        controlText.setFill(Color.WHITE);
        controlText.setStyle("-fx-font: 20 arial;");
        controlText.setOpacity(.4);
        pane1.getChildren().add(controlText);

        Text controlText1 = new Text((WIDTH/2 - 60), 480, "Space:  Shoot");
        controlText1.setFill(Color.WHITE);
        controlText1.setStyle("-fx-font: 20 arial;");
        controlText1.setOpacity(.4);
        pane1.getChildren().add(controlText1);

        Text exitText = new Text((WIDTH/2 - 120), 520, "Press ESC for Main Screen");
        exitText.setFill(Color.WHITE);
        exitText.setStyle("-fx-font: 20 arial;");
        exitText.setOpacity(.4);
        pane1.getChildren().add(exitText);

        Scene scene = new Scene(pane1); // Create a new Scene with the Pane as its root
        stage.setScene(scene); // Set the Scene to the Stage
        stage.setTitle("Controls"); // Set the title of the window
        stage.show(); // Display the Stage

        PlayerShip ship = new PlayerShip(WIDTH / 2, HEIGHT / 2);
        pane1.getChildren().add(ship.getCharacter());
        //text to explain controls
        //text to say 'press escape or press x to exit'
        List<Projectile> shoots = new ArrayList<>();

        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();
        //Scene scene1 = new Scene(pane1);
        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });
        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
        });
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });

        new AnimationTimer() {

            @Override
            public void handle(long now) {

                if (pressedKeys.getOrDefault(KeyCode.A, false) && !pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.D, false) && !pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnRight();
                }

                if (pressedKeys.getOrDefault(KeyCode.W, false)) {
                    ship.accelerate();
                }

                boolean moveAndShootPressed = (pressedKeys.getOrDefault(KeyCode.D, false) || pressedKeys.getOrDefault(KeyCode.A, false) || pressedKeys.getOrDefault(KeyCode.W, false))
                        && pressedKeys.getOrDefault(KeyCode.SPACE, false);

                //can use an or operator here, as logically if moveAndShootPressed is true, then Space must be pressed
                if (pressedKeys.getOrDefault(KeyCode.SPACE, false) || moveAndShootPressed) {
                    // Check if enough frames have passed since the last shot
                    if (framesSinceLastShot >= 10) {
                        // When shooting the bullet in the same direction as the ship
                        Projectile shot = new Projectile((int) ship.getCharacter().getTranslateX(),
                                (int) ship.getCharacter().getTranslateY());

                        shot.getCharacter().setRotate(ship.getCharacter().getRotate());
                        shoots.add(shot);
                        shot.move();
                        pane1.getChildren().add(shot.getCharacter());

                        // Reset the framesSinceLastShot counter
                        framesSinceLastShot = 0;
                    }
                    //pressedKeys.clear();
                }

                // Increment the framesSinceLastShot counter on each frame
                framesSinceLastShot++;

                ship.move();

                Iterator<Projectile> iterator = shoots.iterator();
                while(iterator.hasNext()) {
                    Projectile shoot = iterator.next();
                    if(shoot.outOfBounds()) {
                        iterator.remove();
                    } else {
                        shoot.move();
                    }
                }

            }

        }.start();
        stage.setTitle("Controls");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);

    }
}

