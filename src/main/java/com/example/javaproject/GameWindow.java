package com.example.javaproject;

// Imports required packages

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GameWindow{
    //game window size
    public static final int WIDTH = 960;
    public static final int HEIGHT = 600;
    int framesSinceLastShot = 0;


    public void load(Stage stage, int numAsteroids){
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);
        // Background color
        pane.setStyle("-fx-background-color: black");


        // Center position
        Random alien_rnd = new Random();

        PlayerShip ship = new PlayerShip(WIDTH/2, HEIGHT/2);
        EnemyShip alienShip = new EnemyShip(alien_rnd.nextInt(WIDTH), alien_rnd.nextInt(HEIGHT));

        //getchildren method to add a shape
        pane.getChildren().add(ship.getCharacter());
        pane.getChildren().add(alienShip.getCharacter());

        // Show current points ,current level, and current HP
        Text text = new Text(10, 20, "Current Points: 0");
        Text text1 = new Text(10,40,"Current Level: 1");
        Text text2 = new Text(10,60,"Current HP: 3");
        text.setFill(Color.WHITE);
        text1.setFill(Color.WHITE);
        text2.setFill(Color.WHITE);
        text.setStyle("-fx-font: 20 arial;");
        text1.setStyle("-fx-font: 20 arial;");
        text2.setStyle("-fx-font: 20 arial;");
        pane.getChildren().add(text);
        pane.getChildren().add(text1);
        pane.getChildren().add(text2);
        AtomicInteger points = new AtomicInteger();
        // Initial value
        AtomicInteger level = new AtomicInteger(1);
        AtomicInteger HP= new AtomicInteger(3);


        List<Asteroid> asteroids = new ArrayList<>();
        double l=0.1;
        for (int i = 0; i < numAsteroids; i++) {
            Random rnd= new Random();
            double rnd_1= Math.random()*10+30;
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT),rnd_1,l);
            asteroids.add(asteroid);
        }
        double scale=0.5;
        Asteroid asteroid_special=new Asteroid(WIDTH/2,500,40,l);
        asteroids.add(asteroid_special);


        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));


        List<Projectile> shoots = new ArrayList<>();
        //control ship
        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();
        Scene scene = new Scene(pane);
        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });
        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
        });


        new AnimationTimer() {

            @Override
            public void handle(long now) {

                boolean moveAndShootPressed = (pressedKeys.getOrDefault(KeyCode.LEFT, false)||pressedKeys.getOrDefault(KeyCode.RIGHT, false)||pressedKeys.getOrDefault(KeyCode.UP, false))
                        && pressedKeys.getOrDefault(KeyCode.SPACE, false);

                if (pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnRight();
                }
                if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }


                //can use an or operator here, as logically if moveAndShootPressed is true, then Space must be pressed
                if (pressedKeys.getOrDefault(KeyCode.SPACE, false)||moveAndShootPressed) {
                    // Check if enough frames have passed since the last shot
                    if (framesSinceLastShot >= 10) {
                        // When shooting the bullet in the same direction as the ship
                        Projectile shot = new Projectile((int) ship.getCharacter().getTranslateX(),
                                (int) ship.getCharacter().getTranslateY());

                        shot.getCharacter().setRotate(ship.getCharacter().getRotate());
                        shoots.add(shot);
                        shot.move();
                        pane.getChildren().add(shot.getCharacter());

                        // Reset the framesSinceLastShot counter
                        framesSinceLastShot = 0;
                    }
                    //pressedKeys.clear();
                }

                // Increment the framesSinceLastShot counter on each frame
                framesSinceLastShot++;

                if(pressedKeys.getOrDefault(KeyCode.SHIFT,false)){
                    asteroids.forEach(asteroid -> {
                        ship.character.setTranslateX(Math.random()*WIDTH);
                        ship.character.setTranslateY(Math.random()*HEIGHT);
                        while(asteroid.collide(ship))
                        {
                            ship.character.setTranslateX(Math.random()*WIDTH);
                            ship.character.setTranslateY(Math.random()*HEIGHT);
                        }
                    });
                    //pressedKeys.clear();

                }

                ship.move();
                alienShip.move();
                asteroids.forEach(asteroid -> asteroid.move());

                /*
                // shooting
                shoots.forEach(shoot -> {
                    if(shoot.outOfBounds()) {
                        shoots.remove(shoot);
                    }else{shoot.move();}
                });
                 */
                //iterator required to avoid concurrent modification exception (removing item from list while iterating through it)
                Iterator<Projectile> iterator = shoots.iterator();
                while(iterator.hasNext()) {
                    Projectile shoot = iterator.next();
                    if(shoot.outOfBounds()) {
                        iterator.remove();
                    } else {
                        shoot.move();
                    }
                }

                List<Projectile> destroy_asteroid = shoots.stream().filter(shot -> {
                    List<Asteroid> destroy = asteroids.stream()
                            .filter(asteroids -> asteroids.collide(shot))
                            .collect(Collectors.toList());
                    if (destroy.isEmpty()) {
                        return false;
                    }
                    // Remove destroyed asteroid
                    destroy.stream().forEach(delete -> {
                        asteroids.remove(delete);
                        pane.getChildren().remove(delete.getCharacter());
                        for (int i = 0; i < 2 ; i++) {
                            if (delete.getSize()>10){
                                Asteroid asteroid = new Asteroid((int) delete.getCharacter()
                                        .getTranslateX(),(int)delete.getCharacter().getTranslateY(),delete.getSize()*scale,l + 0.2);
                                asteroids.add(asteroid);

                                pane.getChildren().add(asteroid.getCharacter());
                            }
                        }
                        // Count Level up
                    });
                    return true;
                }).collect(Collectors.toList());

                // Add points,HP and level
                destroy_asteroid.forEach(shot -> {
                    pane.getChildren().remove(shot.getCharacter());
                    shoots.remove(shot);
                    points.set(points.get()+100);
                    if(points.get()%1000==0){

                        HP.set(HP.get() + 1);
                        text2.setText("Current HP: " + HP);

                        level.set(level.get() + 1);
                        text1.setText("Current level: " + level);
                        asteroids.forEach(asteroid -> {
                            asteroid.move_speed+=(0.1 * level.get());
                        });
                    }
                    text.setText("Current Points: " + points);

                });

                if (asteroids.isEmpty()) {
                    int newNumAsteroids = numAsteroids + 1;
                    double newScale = scale + 0.1;
                    for (int i = 0; i < newNumAsteroids; i++) {
                        Random rnd= new Random();
                        double rnd_1= Math.random()*10+30;
                        Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT),rnd_1,newScale);
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                    level.incrementAndGet();
                    text1.setText("Current Level: " + level.get());
                }

                // Hyper jumps and random reborn position
                double random_x = Math.random() * 1000 % WIDTH;
                double random_y = Math.random() * 700 % HEIGHT;
                asteroids.forEach(asteroid -> asteroid.move());
                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        HP.set(HP.get() - 1);
                        if(HP.get()>0) {
                            //getchildredn method to add a shape
                            ship.character.setTranslateX(WIDTH / 2);
                            ship.character.setTranslateY(500);
                            while(asteroid.collide(ship))
                            {
                                ship.character.setTranslateX(Math.random()*WIDTH);
                                ship.character.setTranslateY(Math.random()*HEIGHT);
                            }
                            text2.setText("Current HP: " + HP);
                        }else
                        {
                            stop();
                            text2.setText("GameOver");

                        }
                    }
                });

                //Recreate random position asteroids
                if(Math.random() < 0.005) {
                    double rnd_2= Math.random()*10+30;
                    double rnd_3=Math.random()*1000;
                    Asteroid asteroid = new Asteroid((int) rnd_3%WIDTH, 0,rnd_2,l+0.3*level.get());
                    if(!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                }
            }

        }.start();


        //show everything in window
        stage.setTitle("Asteroids!");
        stage.setScene(scene);
        stage.show();
    }
}