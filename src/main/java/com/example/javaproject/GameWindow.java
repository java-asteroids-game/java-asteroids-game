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

public class GameWindow {
    //game window size
    public static final int WIDTH = 960;
    public static final int HEIGHT = 600;

    // Set atomic integers with initial values
    AtomicInteger points = new AtomicInteger(0);
    AtomicInteger level = new AtomicInteger(1);
    AtomicInteger HP = new AtomicInteger(3);

    int framesSinceLastShot = 0;
    int framesSinceLastAlienShot = 0;
    int framesSinceLastRandomAsteroid = 0;

    List<Asteroid> asteroids = new ArrayList<>();
    List<Projectile> shoots = new LinkedList<>();
    List<Projectile> alienShoots = new ArrayList<>();
    PlayerShip ship = new PlayerShip(WIDTH / 2, HEIGHT / 2);
    final EnemyShip alienShip = new EnemyShip(WIDTH / 2, HEIGHT / 2);


    public void asteroidsHitUpdatePoints(AtomicInteger points, AtomicInteger HP, AtomicInteger level, List<Asteroid> asteroids) {
        points.set(points.get() + 100);

        if (points.get() % 1000 == 0) {
            HP.incrementAndGet();
            level.incrementAndGet();
        }
    }

    private void updateGameInformation(Text pointsText, Text levelText, Text livesText) {
        pointsText.setText("Points: " + points);
        livesText.setText("Lives: " + HP);
        levelText.setText("Level: " + level);
    }

    private boolean isPositionNotSafe(Point2D newPosition, double safeDistance) {
        // Check for collisions with asteroids
        for (Asteroid asteroid : asteroids) {
            if (newPosition.distance(asteroid.getCharacter().getTranslateX(), asteroid.getCharacter().getTranslateY()) < safeDistance) {
                return true;
            }
        }
        // Check for collisions with projectiles
        for (Projectile alienShoot : alienShoots) {
            if (newPosition.distance(alienShoot.getCharacter().getTranslateX(), alienShoot.getCharacter().getTranslateY()) < safeDistance) {
                return true;
            }
        }
        // Check for collisions with alien ship
        if (alienShip.isAlive() && newPosition.distance(alienShip.getCharacter().getTranslateX(), alienShip.getCharacter().getTranslateY()) < safeDistance) {
            return true;
        }
        return false;
    }

    public void load(Stage stage, int numAsteroids) {
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);
        pane.setStyle("-fx-background-color: black");

        //get children method to add a shape
        pane.getChildren().add(ship.getCharacter());

        // Show current points ,current level, and current HP
        Text pointsText = new Text(30, 40, "Points: 0");
        Text levelText = new Text(30, 80, "Level: 1");
        Text livesText = new Text(850, 40, "Lives: 3");
        pointsText.setFill(Color.WHITE);
        levelText.setFill(Color.WHITE);
        livesText.setFill(Color.WHITE);
        pointsText.setStyle("-fx-font: 20 consolas;");
        levelText.setStyle("-fx-font: 20 consolas;");
        livesText.setStyle("-fx-font: 20 consolas;");
        pane.getChildren().add(pointsText);
        pane.getChildren().add(levelText);
        pane.getChildren().add(livesText);


        // Creates the generator for the alien. Sets up timeline events to check if the alien is alive
        Timeline alienGenerator = new Timeline();
        alienGenerator.getKeyFrames().add(
                new KeyFrame(Duration.seconds(10), event -> {
                    if (!alienShip.isAlive()) {
                        alienShip.getCharacter().setTranslateX(Math.random() * WIDTH);
                        alienShip.getCharacter().setTranslateY(Math.random() * HEIGHT);
                        pane.getChildren().add(alienShip.getCharacter());
                        alienShip.setAlive(true);
                    }
                })
        );

        alienGenerator.setCycleCount(Timeline.INDEFINITE);
        alienGenerator.play();


        double l = 0.1;
        for (int i = 0; i < numAsteroids; i++) {
            Random rnd = new Random();
            double rnd_1 = Math.random() * 25 + 30;
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT),/*25,*/0.1, AsteroidType.LARGE);
            asteroids.add(asteroid);
        }
        double scale = 0.5;
        Asteroid asteroid_special = new Asteroid(WIDTH / 2, 500, .4, AsteroidType.SPECIAL);
        asteroids.add(asteroid_special);
        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));

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

            private void handleInput() {
                //for debugging
                //System.out.println("Asteroids: " + asteroids.size());
                //System.out.println("Shoots: " + shoots.size());

                if (pressedKeys.getOrDefault(KeyCode.A, false) && !pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.D, false) && !pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnRight();
                }

                if (pressedKeys.getOrDefault(KeyCode.W, false)) {
                    ship.accelerate();
                }
                if (pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnRight();
                }
                if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }

                boolean moveAndShootPressed = (pressedKeys.getOrDefault(KeyCode.D, false) || pressedKeys.getOrDefault(KeyCode.A, false) || pressedKeys.getOrDefault(KeyCode.W, false))
                        && pressedKeys.getOrDefault(KeyCode.SPACE, false);

                if (pressedKeys.getOrDefault(KeyCode.SPACE, false) || moveAndShootPressed) {
                    handleShipShooting();
                }

                scene.setOnKeyPressed(event -> {
                    pressedKeys.put(event.getCode(), Boolean.TRUE);

                    if (event.getCode() == KeyCode.SHIFT) {
                        handleHyperJump();
                    }
                });

//                if (pressedKeys.getOrDefault(KeyCode.SHIFT, false)) {
//                    handleHyperJump();
//                }
            }

            private void handleShipShooting() {
                if (framesSinceLastShot >= 15) {
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
                // Increment the framesSinceLastShot counter on each frame
                framesSinceLastShot++;
            }

            private void handleAlienShooting() {
                if (alienShip.isAlive()) {
                    if (framesSinceLastAlienShot >= 180) {
                        Projectile alienShot = alienShip.shootAtTarget(ship);

                        alienShoots.add(alienShot);
                        alienShot.move();
                        pane.getChildren().add(alienShot.getCharacter());

                        // Reset the framesSinceLastShot counter
                        framesSinceLastAlienShot = 0;
                    }
                }

                // Increment the framesSinceLastShot counter on each frame
                framesSinceLastAlienShot++;
            }

            private void handleHyperJump() {
                ship.character.setTranslateX(Math.random() * WIDTH);
                ship.character.setTranslateY(Math.random() * HEIGHT);
                while (isPositionNotSafe(new Point2D(ship.getCharacter().getTranslateX(), ship.getCharacter().getTranslateY()), 200)) {
                    ship.character.setTranslateX(Math.random() * WIDTH);
                    ship.character.setTranslateY(Math.random() * HEIGHT);
                };
            }

            private void moveObjects() {
                ship.move();
                alienShip.move();
                asteroids.forEach(asteroid -> asteroid.move());
                handleAlienShooting();
            }

            private void damageShip() {
                HP.decrementAndGet();

                if (HP.get() > 0) {
                    //get children method to add a shape
                    ship.character.setTranslateX(Math.random() * WIDTH);
                    ship.character.setTranslateY(Math.random() * HEIGHT);

                    while (isPositionNotSafe(new Point2D(ship.getCharacter().getTranslateX(), ship.getCharacter().getTranslateY()), 200)) {
                        ship.character.setTranslateX(Math.random() * WIDTH);
                        ship.character.setTranslateY(Math.random() * HEIGHT);
                    }
                    ship.setMovement(ship.getMovement().normalize());
                    updateGameInformation(pointsText, levelText, livesText);
                } else {
                    stop();
                    pane.getChildren().clear();
                    int finalPoints = points.get();
                    Scene gameOverScene = new GameOver().showGameOverScreen(stage, finalPoints);
                    stage.setScene(gameOverScene);
                }

                updateGameInformation(pointsText, levelText, livesText);
            }
            private void handleCollisions() {
                manageBulletCollisions();
                manageAlienBulletCollisions();
                manageAsteroids();
            }

            private void manageAsteroids() {
                if (asteroids.isEmpty()) {

                    int newNumAsteroids = numAsteroids + 1;
                    double newScale = scale + 0.1;
                    for (int i = 0; i < newNumAsteroids; i++) {

                        Random rnd = new Random();
                        double rnd_1 = Math.random() * 10 + 30;

                        Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT), newScale, AsteroidType.LARGE);
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }

                    level.incrementAndGet();
                    levelText.setText("Level: " + level.get());
                }

                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        // Reduce ship HP
                        damageShip();
                    }
                });

                //if numAsteroids <15 && numFramesSinceRandomAsteroid > 10
                //Recreate random position asteroids

                //if (Math.random() < 0.005) {
                if(framesSinceLastRandomAsteroid >10 && Math.random() < 0.005){
                    double rnd_2 = Math.random() * 1000;
                    Asteroid asteroid = new Asteroid((int) rnd_2 % WIDTH, 0, l, AsteroidType.MEDIUM);
                    if (!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                    framesSinceLastRandomAsteroid = 0;
                }

                updateGameInformation(pointsText, levelText, livesText);
                framesSinceLastRandomAsteroid++;
            }

            private void manageBulletCollisions() {
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


                        if (alienShip.isAlive() && alienShip.collide(shoot)) {
                            alienShip.setAlive(false);
                            pane.getChildren().remove(alienShip.getCharacter());
                        }


                        // Create a list of broken asteroids
                        destroyedAsteroids = asteroids.stream()
                                .filter(asteroid -> asteroid.collide(shoot))
                                .toList();

                        // If any asteroids have been hit by a projectile, remove them
                        if (!destroyedAsteroids.isEmpty()) {
                            destroyedAsteroids.forEach(delete -> {
                                asteroids.remove(delete);
                                pane.getChildren().remove(delete.getCharacter());
                                for (int i = 0; i < 2; i++) {
                                    if (delete.getType() == AsteroidType.SPECIAL) {
                                        Asteroid asteroid = new Asteroid((int) delete.getCharacter()
                                                .getTranslateX(), (int) delete.getCharacter().getTranslateY(),/*25,*/l + 0.2, AsteroidType.LARGE);
                                        asteroids.add(asteroid);
                                        pane.getChildren().add(asteroid.getCharacter());
                                    } else if (delete.getType() == AsteroidType.LARGE) {
                                        Asteroid asteroid = new Asteroid((int) delete.getCharacter()
                                                .getTranslateX(), (int) delete.getCharacter().getTranslateY(),/*25,*/l + 0.2, AsteroidType.MEDIUM);
                                        asteroids.add(asteroid);
                                        pane.getChildren().add(asteroid.getCharacter());
                                    } else if (delete.getType() == AsteroidType.MEDIUM) {
                                        Asteroid asteroid = new Asteroid((int) delete.getCharacter()
                                                .getTranslateX(), (int) delete.getCharacter().getTranslateY(),/*12,*/l + 0.2, AsteroidType.SMALL);
                                        asteroids.add(asteroid);
                                        pane.getChildren().add(asteroid.getCharacter());
                                    }
                                }
                            });

                            // Handle points update
                            asteroidsHitUpdatePoints(points, HP, level, asteroids);

                            // Remove the projectile that collided with the asteroid
                            projectileIterator.remove();
                            pane.getChildren().remove(shoot.getCharacter());
                        }
                    }
                }
            }

            private void manageAlienBulletCollisions(){
                //iterator required to avoid concurrent modification exception (removing item from list while iterating through it)
                Iterator<Projectile> alienProjectileIterator = alienShoots.iterator();
                while (alienProjectileIterator.hasNext()) {
                    Projectile alienShoot = alienProjectileIterator.next();

                    // Check if the projectile has gone out of bounds
                    if (alienShoot.outOfBounds()) {
                        alienProjectileIterator.remove();
                        pane.getChildren().remove(alienShoot.getCharacter());
                        // else update the position of the projectile
                    } else if (alienShoot.collide(ship)) {
                        alienProjectileIterator.remove();
                        pane.getChildren().remove(alienShoot.getCharacter());
                        damageShip();
                    } else {
                        alienShoot.move();
                    }
                }
            }

            @Override
            public void handle(long now) {
                handleInput();
                moveObjects();
                handleCollisions();
                updateGameInformation(pointsText, levelText, livesText);
            }

        }.start();


        //show everything in window
        stage.setTitle("Asteroids!");
        stage.setScene(scene);
        stage.show();
    }
}