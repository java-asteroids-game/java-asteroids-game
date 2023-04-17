package com.example.javaproject;

// Imports required packages

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameWindow{
    //game window size
    public static final int WIDTH = 960;
    public static final int HEIGHT = 600;
    public static int MAX_ALIENS = 2;
    public int MAX_GENERATE_ALIENS = 3;
    int count_aliens = 0;
    int framesSinceLastShot = 0;

    public void updatePoints(AtomicInteger points, AtomicInteger HP, AtomicInteger level, List<Asteroid> asteroids)  {
        points.set(points.get() + 100);

        if (points.get() % 1000==0) {
            HP.set(HP.get() + 1);
            level.set(level.get() + 1);
        }
        asteroids.forEach(asteroid -> {
            asteroid.move_speed += (0.01 * level.get());
        });
    }

    public void updateGameInformation(AtomicInteger points, AtomicInteger HP, AtomicInteger level, List<Asteroid> asteroids, Text text, Text text1, Text text2){
        text.setText("Points: " + points);
        text2.setText("Lives: " + HP);
        text1.setText("Level: " + level);
    }


    public void load(Stage stage, int numAsteroids){
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);
        pane.setStyle("-fx-background-color: black");


        PlayerShip ship = new PlayerShip(WIDTH/2, HEIGHT/2);
        final EnemyShip alienShip = new EnemyShip(WIDTH/2, HEIGHT / 2);

        //get children method to add a shape
        pane.getChildren().add(ship.getCharacter());

        // Show points ,level, and HP
        Text text = new Text(30, 40, "Points: 0");
        Text text1 = new Text(30,80,"Level: 1");
        Text text2 = new Text(850,40,"Lives: 3");
        text.setFill(Color.WHITE);
        text1.setFill(Color.WHITE);
        text2.setFill(Color.WHITE);
        text.setStyle("-fx-font-family: 'Bank Gothic'; -fx-font-size: 20px;");
        text1.setStyle("-fx-font-family: 'Bank Gothic'; -fx-font-size: 20px;");
        text2.setStyle("-fx-font-family: 'Bank Gothic'; -fx-font-size: 20px;");
        pane.getChildren().add(text);
        pane.getChildren().add(text1);
        pane.getChildren().add(text2);

        // Set atomic integers with initial values
        AtomicInteger points = new AtomicInteger(0);
        AtomicInteger level = new AtomicInteger(1);
        AtomicInteger HP= new AtomicInteger(3);

        // Creates the generator for the alien. Sets up 2 timeline events to check if the alien is alive and shoot bullets
        Timeline alienGenerator = new Timeline();
        alienGenerator.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(10), event -> {
                    if (!alienShip.isAlive()) {
                        alienShip.getCharacter().setTranslateX(Math.random() * WIDTH);
                        alienShip.getCharacter().setTranslateY(Math.random() * WIDTH);
                        pane.getChildren().add(alienShip.getCharacter());
                        alienShip.setAlive(true);
                    }
                }),
                new KeyFrame(Duration.seconds(2), event -> {
                    if (alienShip.isAlive()) {
                        alienShip.shootAtTarget(ship);
                    }
                })
        );

        alienGenerator.setCycleCount(Timeline.INDEFINITE);
        alienGenerator.play();



        List<Asteroid> asteroids = new ArrayList<>();
        double l = 0.1;
        for (int i = 0; i < numAsteroids; i++) {
            Random rnd= new Random();
            double rnd_1= Math.random()*10+30;
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT), rnd_1, l);
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


//        //alien projectile move
//        alienProjectiles.forEach(projectile -> projectile.move());
//        //alien projectiles collide player ship
//        alienProjectiles.forEach(projectile -> {
//            if (ship.collide(projectile)) {
//                HP.set(HP.get() - 1);
//                if(HP.get()>0) {
//                    //get Children method to add a shape
//                    ship.character.setTranslateX((double) WIDTH / 2);
//                    ship.character.setTranslateY(500);
//                    while (!isPositionSafe(WIDTH / 2, 500, ship, asteroids, shoots, alienShip , 100));
//                    text2.setText("HP: " + HP);
//                }else
//                {
//                    text2.setText("GameOver");
//
//                }
//            }
//        });
//        //alien projectiles steam, remove dead alien projectiles
//        List<Projectile> deadAlienProjectiles = alienProjectiles.stream()
//                .filter(projectile -> !projectile.isAlive())
//                .toList();
//
//        deadAlienProjectiles.forEach(projectile -> {
//            pane.getChildren().remove(projectile.getCharacter());
//            alienProjectiles.remove(projectile);
//        });


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

                boolean moveAndShootPressed = (pressedKeys.getOrDefault(KeyCode.D, false)||pressedKeys.getOrDefault(KeyCode.A, false)||pressedKeys.getOrDefault(KeyCode.W, false))
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
                asteroids.forEach(asteroid -> {
                    asteroid.move();
                });


                // -------------------------------------- MANAGE BULLET COLLISIONS WITH ASTEROIDS --------------------------------------------------
                List<Asteroid> destroyedAsteroids = new ArrayList<>();

                //iterator required to avoid concurrent modification exception (removing item from list while iterating through it)
                Iterator<Projectile> projectileIterator = shoots.iterator();
                while (projectileIterator.hasNext()) {
                    Projectile shoot = projectileIterator.next();

                    // Check if the projectile has gone out of bounds
                    if (shoot.outOfBounds()) {
                        projectileIterator.remove();
                        pane.getChildren().remove(shoot.getCharacter());
                    // else update the position of the projectile
                    } else {
                        shoot.move();

                        // Create a list of broken asteroids
                        destroyedAsteroids = asteroids.stream()
                                .filter(asteroid -> asteroid.collide(shoot))
                                .toList();

                        // If any asteroids have been hit by a projectile, remove them
                        if (!destroyedAsteroids.isEmpty()) {
                            destroyedAsteroids.forEach(asteroid -> {
                                asteroids.remove(asteroid);
                                pane.getChildren().remove(asteroid.getCharacter());

                                // Define asteroid functionality on destroy
                                for (int i = 0; i < 2; i++) {
                                    if (asteroid.getSize() > 10) {
                                        Asteroid newAsteroid = new Asteroid((int) asteroid.getCharacter().getTranslateX(),
                                                (int) asteroid.getCharacter().getTranslateY(), asteroid.getSize() * scale, l + 0.2);
                                        asteroids.add(newAsteroid);
                                        pane.getChildren().add(newAsteroid.getCharacter());
                                    }
                                }
                            });

                            // Handle points update
                            updatePoints(points, HP, level, asteroids);

                            // Remove the projectile that collided with the asteroid
                            projectileIterator.remove();
                            pane.getChildren().remove(shoot.getCharacter());
                        }
                    }
                }


                // ------------------------------------ MANAGE ASTEROIDS ON SCREEN -------------------------------------------
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
                    text1.setText("Level: " + level.get());
                }

                // ---------------------------------  DEFINE HYPERSPACE ---------------------------------------------------

                // Hyper jumps and random reborn position
                double random_x = Math.random() * 1000 % WIDTH;
                double random_y = Math.random() * 700 % HEIGHT;


                // -------------------------------- MANAGE SHIP AND ASTEROID COLLISION ----------------------------------
                asteroids.forEach(asteroid -> {
                    // If the ship collides with an asteroid
                    if (ship.collide(asteroid)) {
                        // Reduce ship HP
                        HP.decrementAndGet();

                        if (HP.get() > 0) {

                            //get children method to add a shape
                            ship.character.setTranslateX((double) WIDTH / 2);
                            ship.character.setTranslateY((double) HEIGHT / 2);

                            while(!isPositionSafe(new Point2D(Math.random()*WIDTH, Math.random()*HEIGHT), ship, asteroids, shoots, alienShip, 10)){
                                ship.character.setTranslateX(Math.random()*WIDTH);
                                ship.character.setTranslateY(Math.random()*HEIGHT);
                            }
                            text2.setText("Lives: " + HP);
                        }
                        else
                        {
                            stop();
                            pane.getChildren().clear();
                            int finalPoints = points.get();
                            Scene gameOverScene = new GameOver().showGameOverScreen(stage, finalPoints);
                            stage.setScene(gameOverScene);
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

                updateGameInformation(points, HP, level, asteroids, text, text1, text2);
            }


        }.start();


        //show everything in window
        stage.setTitle("Asteroids!");
        stage.setScene(scene);
        stage.show();
    }



    //Determine if the location is safe
    public boolean isPositionSafe(Point2D newPosition, PlayerShip player, List<Asteroid> asteroids, List<Projectile> projectiles, EnemyShip alien, double safeDistance) {

        // Check for collisions with asteroids
        for (Asteroid asteroid : asteroids) {
            if (newPosition.distance(asteroid.getCharacter().getTranslateX(), asteroid.getCharacter().getTranslateY()) < safeDistance) {
                return false;
            }
        }

        // Check for collisions with projectiles
        for (Projectile projectile : projectiles) {
            if (newPosition.distance(projectile.getCharacter().getTranslateX(), projectile.getCharacter().getTranslateY()) < safeDistance) {
                return false;
            }
        }

        // Check for collisions with alien ship
        if (newPosition.distance(alien.getCharacter().getTranslateX(), alien.getCharacter().getTranslateY()) < safeDistance) {
            return false;
        }


        return true;
    }
}