package com.example.javaproject;

// Imports required packages

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class GameWindow {
    //game window size
    public static final int WIDTH = 960;
    public static final int HEIGHT = 600;

    // Set atomic integers with initial values
    AtomicInteger points = new AtomicInteger(0);
    AtomicInteger level = new AtomicInteger(1);
    AtomicInteger HP = new AtomicInteger(3);

    int framesSinceLastShot = 15;
    int framesSinceLastAlienShot = 0;
    int framesSinceLastRandomAsteroid = 0;
    int framesSinceLastGodMode = 0;

    List<Asteroid> asteroids = new ArrayList<>();
    List<Projectile> shoots = new LinkedList<>();
    List<Projectile> alienShoots = new ArrayList<>();
    List <AbstractGameElement> characters = new ArrayList<>();
    PlayerShip ship = new PlayerShip(WIDTH / 2, HEIGHT / 2);
    final EnemyShip alienShip = new EnemyShip(WIDTH / 2, HEIGHT / 2);


    public void asteroidsHitUpdatePoints() {
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
        Text overheatText = new Text(30, 140, "Overheat");
        pointsText.setFill(Color.WHITE);
        levelText.setFill(Color.WHITE);
        livesText.setFill(Color.WHITE);
        overheatText.setFill(Color.WHITE);
        pointsText.setStyle("-fx-font: 20 consolas;");
        levelText.setStyle("-fx-font: 20 consolas;");
        livesText.setStyle("-fx-font: 20 consolas;");
        overheatText.setStyle("-fx-font: 20 consolas;");
        pane.getChildren().add(pointsText);
        pane.getChildren().add(levelText);
        pane.getChildren().add(livesText);
        pane.getChildren().add(overheatText);

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

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(WIDTH - 870);
        progressBar.setLayoutX(30);
        progressBar.setLayoutY(HEIGHT - 500);
        progressBar.setProgress(0);
        pane.getChildren().add(progressBar);

        double l = 0.1;
        for (int i = 0; i < numAsteroids; i++) {
            Random rnd = new Random();
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

                //creates text to display when user cheats
                Text cheatText = new Text("CHEATERS LOSE THEIR POINTS");
                cheatText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                cheatText.setFill(Color.RED);
                cheatText.setLayoutX(310);
                cheatText.setLayoutY(300);
                cheatText.setOpacity(0.0);

                //sets the duration and opacity for appearance of cheatText (alternates on/off)
                Timeline cheatTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(0.5), e -> cheatText.setOpacity(1.0)),
                        new KeyFrame(Duration.seconds(1.0), e -> cheatText.setOpacity(0.0))
                );
                //sets the cycle as indefinite - remains until gameover
                cheatTimeline.setCycleCount(Timeline.INDEFINITE);
                pane.getChildren().add(cheatText);


                //sets 'u' key as a 'cheat' key which destroys all asteroids, sets (reduces) level to 3, displays annoying text
                //level 3 and over only, waits 250 frames, usage sets points to 0
                if (pressedKeys.getOrDefault(KeyCode.U, false) && level.get()>=3 && framesSinceLastGodMode>250) {
                    asteroids.forEach(asteroid -> {
                        pane.getChildren().remove(asteroid.getCharacter());
                    });
                    asteroids.removeAll(asteroids);
                    framesSinceLastGodMode = 0;
                    //level.set(level.get()-2);
                    level.set(3);
                    points.set(0);
                    framesSinceLastGodMode = 0;
                    cheatTimeline.play();
                }
                framesSinceLastGodMode ++;

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

            }

            private void handleShipShooting() {
                if (framesSinceLastShot >= 15 && shoots.size() < 8) {
                    // When shooting the bullet in the same direction as the ship
                    Projectile shot = new Projectile((int) ship.getCharacter().getTranslateX(),
                            (int) ship.getCharacter().getTranslateY());

                    shot.setSpeed(ship.getSpeed());

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
                    if (framesSinceLastAlienShot >= 100) {
                        Projectile alienShot = alienShip.shootAtTarget(ship);
                        alienShot.setSpeed(alienShip.getSpeed());
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
                characters.add(alienShip);
                characters.addAll(alienShoots);
                characters.addAll(asteroids);
                ship.moveSomewhereSafe(characters, 200);
                characters.clear();
            }


            private void moveObjects() {
                ship.move();
                alienShip.move();
                asteroids.forEach(asteroid -> asteroid.move());
                handleAlienShooting();
            }

            private void damageShip() {
                if (!ship.isInvincible()) {
                    HP.decrementAndGet();

                if (HP.get() > 0) {
                    //get children method to add a shape
                    handleHyperJump();
                    ship.setMovement(ship.getMovement().normalize());
                    updateGameInformation(pointsText, levelText, livesText);

                    ship.setInvincible(true); // Set ship invincible

                    // Blinking animation
                    Timeline blinkTimeline = new Timeline(
                            new KeyFrame(Duration.ZERO, e -> ship.getCharacter().setVisible(true)),
                            new KeyFrame(Duration.millis(100), e -> ship.getCharacter().setVisible(false)),
                            new KeyFrame(Duration.millis(200), e -> ship.getCharacter().setVisible(true))
                    );
                    blinkTimeline.setCycleCount(15); // Number of blinks (15 blinks in 3 seconds)
                    blinkTimeline.play();

                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ship.setInvincible(false); // Remove invincibility after 3 seconds
                            ship.getCharacter().setVisible(true); // Ensure the ship is visible after the invincibility period
                        }
                    }, 3000);
                } else {
                    stop();
                    pane.getChildren().clear();
                    int finalPoints = points.get();
                    Scene gameOverScene = new GameOver().showGameOverScreen(stage, finalPoints);
                    stage.setScene(gameOverScene);
                }

                    updateGameInformation(pointsText, levelText, livesText);
                }
//                HP.decrementAndGet();
//
//                if (HP.get() > 0) {
//                    //get children method to add a shape
//                    ship.character.setTranslateX(Math.random() * WIDTH);
//                    ship.character.setTranslateY(Math.random() * HEIGHT);
//
//                    while (isPositionNotSafe(new Point2D(ship.getCharacter().getTranslateX(), ship.getCharacter().getTranslateY()), 200)) {
//                        ship.character.setTranslateX(Math.random() * WIDTH);
//                        ship.character.setTranslateY(Math.random() * HEIGHT);
//                    }
//                    ship.setMovement(ship.getMovement().normalize());
//                    updateGameInformation(pointsText, levelText, livesText);
//                } else {
//                    stop();
//                    pane.getChildren().clear();
//                    int finalPoints = points.get();
//                    Scene gameOverScene = new GameOver().showGameOverScreen(stage, finalPoints);
//                    stage.setScene(gameOverScene);
//                }
//
//                updateGameInformation(pointsText, levelText, livesText);
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
                List<Asteroid> destroyedAsteroids;

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
                            asteroidsHitUpdatePoints();

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

                ObservableList<Projectile> observableShots = FXCollections.observableList(shoots);
                DoubleBinding progressBinding = Bindings.createDoubleBinding((Callable<Double>) () -> {
                    if (observableShots.size() == 0) {
                        return 0.0;
                    } else if (observableShots.size() == 6) {
                        return 1.0;
                    } else {
                        return (double) observableShots.size() / 6;
                    }
                }, (Observable) observableShots);
                progressBar.progressProperty().bind(progressBinding);

            }

        }.start();


        //show everything in window
        stage.setTitle("Asteroids!");
        stage.setScene(scene);
        stage.show();
    }
}