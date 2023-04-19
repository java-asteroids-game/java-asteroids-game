package com.example.javaproject;

// Imports required packages

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
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
    int framesSinceLastAlienShot = 50;
    int framesSinceLastRandomAsteroid = 0;
    int framesSinceLastGodMode = 0;
    int framesSinceLastHyperJump = 0;

    List<Asteroid> asteroids = new ArrayList<>();
    List<Projectile> shoots = new LinkedList<>();
    List<Projectile> alienShoots = new ArrayList<>();
    List <AbstractGameElement> characters = new ArrayList<>();
    PlayerShip ship = new PlayerShip(WIDTH / 2, HEIGHT / 2);
    EnemyShip alienShip = new EnemyShip(WIDTH / 2, HEIGHT / 2);

    private List<Text> setupUITextElements(Pane pane){
        // Show current points ,current level, and current HP
        Text pointsText = new Text(30, 40, "Points: 0");
        Text levelText = new Text(30, 80, "Level: 1");
        Text livesText = new Text(850, 40, "Lives: 3");
        Text overheatText = new Text(30, 140, "Overheat");
        List<Text> textElements = Arrays.asList(pointsText, levelText, livesText, overheatText);

        textElements.forEach(textElement -> {
            textElement.setFill(Color.WHITE);
            textElement.setStyle("-fx-font: 20 consolas;");
            pane.getChildren().add(textElement);
        });

        return textElements;
    }
    private ProgressBar setupProgressBar(Pane pane){
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(WIDTH - 870);
        progressBar.setLayoutX(30);
        progressBar.setLayoutY(HEIGHT - 500);
        progressBar.setProgress(0);
        pane.getChildren().add(progressBar);

        return progressBar;
    }
    private void asteroidsHitUpdatePoints() {
        points.set(points.get() + 100);

        if (points.get() % 1000 == 0) {
            HP.incrementAndGet();
            level.incrementAndGet();
        }
    }
    private void updateGameInformation(List<Text> textElements) {
        textElements.get(0).setText("Points: " + points);
        textElements.get(2).setText("Lives: " + HP);
        textElements.get(1).setText("Level: " + level);

        textElements.forEach(Node::toFront);
    }
    private void cheat(Pane pane){
        //creates text to display when user cheats
        Text cheatText = new Text("CHEATERS LOSE THEIR POINTS");
        cheatText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        cheatText.setFill(Color.RED);
        cheatText.setLayoutX(310);
        cheatText.setLayoutY(300);
        cheatText.setOpacity(0.0);

        pane.getChildren().add(cheatText);

        //sets the duration and opacity for appearance of cheatText (alternates on/off)
        Timeline cheatTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> cheatText.setOpacity(1.0)),
                new KeyFrame(Duration.seconds(1.0), e -> cheatText.setOpacity(0.0))
        );
        //sets the cycle as indefinite - remains until gameover
        cheatTimeline.setCycleCount(Timeline.INDEFINITE);
        cheatTimeline.play();
        // Ship blinks for god mode
//        Timeline cheatBlink = new Timeline(
//                new KeyFrame(Duration.ZERO, e -> ship.getCharacter().setVisible(true)),
//                new KeyFrame(Duration.millis(100), e -> ship.getCharacter().setVisible(false)),
//                new KeyFrame(Duration.millis(200), e -> ship.getCharacter().setVisible(true))
//        );
//        cheatBlink.setCycleCount(Timeline.INDEFINITE);
//        cheatBlink.play();



        // Clears asteroids
        asteroids.forEach(asteroid -> {
            pane.getChildren().remove(asteroid.getCharacter());
        });
        asteroids.clear();
        framesSinceLastGodMode = 0;
        level.set(3);
        points.set(0);

    }




    public void load(Stage stage, int numAsteroids) {
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);
        pane.setStyle("-fx-background-color: black");

        //get children method to add a shape
        pane.getChildren().add(ship.getCharacter());

        List<Text> UITextElements = setupUITextElements(pane);

        ProgressBar progressBar = setupProgressBar(pane);

        // Creates the generator for the alien. Sets up timeline events to check if the alien is alive
        Timeline alienGenerator = new Timeline();
        alienGenerator.getKeyFrames().add(
                new KeyFrame(Duration.seconds(20), event -> {
                    if (!alienShip.isAlive()) {
                        framesSinceLastAlienShot = 0;
                        alienShip.getCharacter().setTranslateX(Math.random() * WIDTH);
                        alienShip.getCharacter().setTranslateY(Math.random() * HEIGHT);
                        pane.getChildren().add(alienShip.getCharacter());
                        alienShip.setAlive(true);
                    }
                })
        );
        alienGenerator.setCycleCount(Timeline.INDEFINITE);
        alienGenerator.play();


        for (int i = 0; i < numAsteroids; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT), AsteroidType.LARGE);
            asteroids.add(asteroid);
        }

        Asteroid asteroid_special = new Asteroid(WIDTH / 2, 500, AsteroidType.SPECIAL);
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

                // ^ is the same as XOR
                if (pressedKeys.getOrDefault(KeyCode.A, false) ^ pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.D, false) ^ pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnRight();
                }

                if (pressedKeys.getOrDefault(KeyCode.W, false) ^ pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }

                if (pressedKeys.getOrDefault(KeyCode.SPACE, false)) {
                    handleShipShooting();
                }

                if (pressedKeys.getOrDefault(KeyCode.SHIFT, false)){
                    handleHyperJump();
                }

                //sets 'u' key as a 'cheat' key which destroys all asteroids, sets (reduces) level to 3, displays annoying text
                //level 3 and over only, waits 250 frames, usage sets points to 0
                if (pressedKeys.getOrDefault(KeyCode.U, false)) {
                    handleCheating();
                }
            }

            private void handleShipShooting() {
                if (framesSinceLastShot >= 15 && shoots.size() < 4) {
                    // When shooting the bullet in the same direction as the ship
                    Projectile shot = ship.shoot();
                    shoots.add(shot);
                    pane.getChildren().add(shot.getCharacter());
                    // Reset the framesSinceLastShot counter
                    framesSinceLastShot = 0;
                }
            }

            private void handleCheating(){
                if  (level.get() >= 3 && framesSinceLastGodMode > 250){
                    cheat(pane);
                    framesSinceLastGodMode = 0;
                }
            }

            private void handleAlienShooting() {
                if (alienShip.isAlive()) {
                    if (framesSinceLastAlienShot >= 100) {
                        Projectile alienShot = alienShip.shootAtTarget(ship);
                        alienShoots.add(alienShot);
                        pane.getChildren().add(alienShot.getCharacter());
                        // Reset the framesSinceLastShot counter
                        framesSinceLastAlienShot = 0;
                    }
                }

                ObservableList<Projectile> observableShots = FXCollections.observableList(shoots);
                DoubleBinding progressBinding = Bindings.createDoubleBinding(() -> {
                    if (observableShots.size() == 0) {
                        return 0.0;
                    } else if (observableShots.size() == 4) {
                        return 1.0;
                    } else {
                        return (double) observableShots.size() / 4;
                    }
                }, observableShots);
                progressBar.progressProperty().bind(progressBinding);
            }

            private void handleHyperJump() {
                if (framesSinceLastHyperJump >= 10){
                    moveShipToSafety();
                    framesSinceLastHyperJump = 0;
                }
            }

            private void handleCollisions() {
                manageBulletCollisions();
                manageAlienBulletCollisions();
                manageAsteroids();
            }

            private void manageAsteroids() {
                if (asteroids.isEmpty()) {

                    int newNumAsteroids = numAsteroids + 1;

                    for (int i = 0; i < newNumAsteroids; i++) {
                        Random rnd = new Random();
                        Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT), AsteroidType.LARGE);
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }

                    level.incrementAndGet();
                }

                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        // Reduce ship HP
                        damageShip();
                    }
                });

                if(framesSinceLastRandomAsteroid > 10 && Math.random() < 0.005){
                    double rnd_2 = Math.random() * 1000;
                    Asteroid asteroid = new Asteroid((int) rnd_2 % WIDTH, 0, AsteroidType.MEDIUM);
                    if (!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                    framesSinceLastRandomAsteroid = 0;
                }
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
                                                .getTranslateX(), (int) delete.getCharacter().getTranslateY(), AsteroidType.LARGE);
                                        asteroids.add(asteroid);
                                        pane.getChildren().add(asteroid.getCharacter());
                                    } else if (delete.getType() == AsteroidType.LARGE) {
                                        Asteroid asteroid = new Asteroid((int) delete.getCharacter()
                                                .getTranslateX(), (int) delete.getCharacter().getTranslateY(), AsteroidType.MEDIUM);
                                        asteroids.add(asteroid);
                                        pane.getChildren().add(asteroid.getCharacter());
                                    } else if (delete.getType() == AsteroidType.MEDIUM) {
                                        Asteroid asteroid = new Asteroid((int) delete.getCharacter()
                                                .getTranslateX(), (int) delete.getCharacter().getTranslateY(), AsteroidType.SMALL);
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

            private void updateFrameCounters(){
                framesSinceLastShot++;
                framesSinceLastAlienShot++;
                framesSinceLastGodMode++;
                framesSinceLastRandomAsteroid++;
                framesSinceLastHyperJump++;
            }

            private void moveObjects() {
                ship.move();
                alienShip.move();
                shoots.forEach(shot -> shot.move());
                alienShoots.forEach(alienShoot -> alienShoot.move());
                asteroids.forEach(asteroid -> asteroid.move());
            }

            private void damageShip() {
                if (!ship.isInvincible()) {
                    HP.decrementAndGet();

                    if (HP.get() > 0) {
                        moveShipToSafety();
                        ship.setMovement(ship.getMovement().normalize());

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
                }

            }

            private void moveShipToSafety(){
                characters.add(alienShip);
                characters.addAll(alienShoots);
                characters.addAll(asteroids);
                ship.moveSomewhereSafe(characters, 150);
                characters.clear();
            }

            @Override
            public void handle(long now) {
                handleInput();
                moveObjects();
                handleAlienShooting();
                handleCollisions();
                updateGameInformation(UITextElements);
                updateFrameCounters();
            }

        }.start();


        //show everything in window
        stage.setTitle("Asteroids!");
        stage.setScene(scene);
        stage.show();
    }
}