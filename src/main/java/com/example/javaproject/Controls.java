package com.example.javaproject;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.geometry.Point2D;
import java.util.*;
public class Controls {

    //game window size
    public static final int WIDTH = 960;
    public static final int HEIGHT = 600;
    static int framesSinceLastShot = 0;
    static int framesSinceLastHyperJump = 0;

    public static void load(Stage stage) {
        Pane pane1 = new Pane();
        pane1.setPrefSize(WIDTH, HEIGHT);
        pane1.setStyle("-fx-background-color: black");

        Text titleText = new Text((WIDTH/2 -60), 80, "Controls");
        titleText.setFill(Color.WHITE);
        titleText.setStyle("-fx-font: 30 arial;");
        titleText.setOpacity(.4);
        pane1.getChildren().add(titleText);

        Text controlText = new Text((WIDTH/2 -280), 440, "W / UP: Accelerate     A / LEFT: Turn Left     D / RIGHT: Turn Right");
        controlText.setFill(Color.WHITE);
        controlText.setStyle("-fx-font: 20 arial;");
        controlText.setOpacity(.4);
        pane1.getChildren().add(controlText);

        Text controlText1 = new Text((WIDTH/2 - 170), 480, "Space: Shoot    Shift: Hyperspace Jump");
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

//        Asteroid.createAsteroids(12, WIDTH, HEIGHT, pane1);

//        List<Asteroid> asteroids = new ArrayList<>();
//        for (int i = 0; i < 12; i++) {
//            Random rnd= new Random();
//            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT), AsteroidType.SMALL);
//            asteroids.add(asteroid);
//        }
//        asteroids.forEach(asteroid -> pane1.getChildren().add(asteroid.getCharacter()));
//
//        List <AbstractGameElement> characters = new ArrayList<>();
//        for (Asteroid asteroid : asteroids) {
//            characters.add(asteroid);
//        }

        List<Asteroid> asteroids = new ArrayList<>();
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                Asteroid.createAsteroids(12, WIDTH, HEIGHT, pane1);

                List <AbstractGameElement> characters = new ArrayList<>();
                for (Asteroid asteroid : asteroids) {
                    characters.add(asteroid);
                }

                if (asteroids.size() < 10) {
                    Random rnd= new Random();
                    Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT), AsteroidType.SMALL);
                    asteroids.add(asteroid);
                    pane1.getChildren().add(asteroid.getCharacter());
                    }

                if ((pressedKeys.getOrDefault(KeyCode.A, false)||pressedKeys.getOrDefault(KeyCode.LEFT, false))&& !pressedKeys.getOrDefault(KeyCode.D, false)&& !pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnLeft();
                }

                if ((pressedKeys.getOrDefault(KeyCode.D, false)||pressedKeys.getOrDefault(KeyCode.RIGHT, false))&& !pressedKeys.getOrDefault(KeyCode.A, false)&& !pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnRight();
                }

                if (pressedKeys.getOrDefault(KeyCode.W, false)||pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }

                if (framesSinceLastHyperJump > 20){
                    if (pressedKeys.getOrDefault(KeyCode.SHIFT, false)||pressedKeys.getOrDefault(KeyCode.SHIFT, false)) {
                        ship.moveSomewhereSafe(characters, 100);
                        framesSinceLastHyperJump = 0;
                    }
                }

                boolean moveAndShootPressed = (pressedKeys.getOrDefault(KeyCode.D, false) || pressedKeys.getOrDefault(KeyCode.A, false) || pressedKeys.getOrDefault(KeyCode.W, false))
                        && pressedKeys.getOrDefault(KeyCode.SPACE, false);

                //can use an or operator here, as logically if moveAndShootPressed is true, then Space must be pressed
                if ((pressedKeys.getOrDefault(KeyCode.SPACE, false) || moveAndShootPressed) && shoots.size()<=6) {
                    // Check if enough frames have passed since the last shot
                    if (framesSinceLastShot >= 16) {
                        // When shooting the bullet in the same direction as the ship
                        Projectile shot = ship.shoot();

                        shoots.add(shot);
                        pane1.getChildren().add(shot.getCharacter());
                        framesSinceLastShot = 0;
                    }
                }
                // Increment the framesSinceLastShot counter on each frame
                framesSinceLastShot++;
                framesSinceLastHyperJump++;

                ship.move();
                asteroids.forEach(asteroid -> asteroid.move());

                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        ship.character.setTranslateX((double) WIDTH / 2);
                        ship.character.setTranslateY(HEIGHT/2);
                        Point2D point = new Point2D(0, 0);
                        ship.setMovement(point);
                        while (asteroid.collide(ship)) {
                            ship.character.setTranslateX(Math.random() * WIDTH);
                            ship.character.setTranslateY(Math.random() * HEIGHT);
                        }
                    }
                    });

                Iterator<Projectile> iterator = shoots.iterator();
                while(iterator.hasNext()) {
                    Projectile shoot = iterator.next();
                    if(shoot.outOfBounds()) {
                        iterator.remove();
                        pane1.getChildren().remove(shoot.getCharacter());
                    } else {
                        shoot.move();
                    }
                }

                List<Projectile> destroy_asteroid = shoots.stream().filter(shot -> {
                    List<Asteroid> destroy = asteroids.stream()
                            .filter(asteroids -> asteroids.collide(shot))
                            .toList();
                    if (destroy.isEmpty()) {
                        return false;
                    }
                    // Remove destroyed asteroid
                    destroy.forEach(delete -> {
                        asteroids.remove(delete);
                        pane1.getChildren().remove(delete.getCharacter());

                    });
                    return true;
                }).toList();

                destroy_asteroid.forEach(shot -> {
                    pane1.getChildren().remove(shot.getCharacter());
                    shoots.remove(shot);
                });

                shoots.forEach(shot -> shot.move());
            }

        }.start();
        stage.setTitle("Controls");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }
}

