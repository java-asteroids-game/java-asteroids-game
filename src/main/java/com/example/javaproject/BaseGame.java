package com.example.javaproject;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public abstract class BaseGame {

    Stage stage;
    //game window size
    public static final int WIDTH = 960;
    public static final int HEIGHT = 600;

    int framesSinceLastShot = 15;
    int framesSinceLastRandomAsteroid = 0;
    int framesSinceLastHyperJump = 0;

    List<Asteroid> asteroids = new ArrayList<>();
    List<Projectile> shoots = new LinkedList<>();
    List<AbstractGameElement> characters = new ArrayList<>();
    PlayerShip ship = new PlayerShip(WIDTH / 2, HEIGHT / 2);
    int numAsteroids;

    Pane pane = new Pane();
    Scene scene;
    Map<KeyCode, Boolean> pressedKeys = new HashMap<>();
    List<Text> UITextElements;

    protected void setupGame(int numAsteroids){
        pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);
        pane.setStyle("-fx-background-color: black");
        scene = new Scene(pane);
        populateKeyHashMap(scene);
        UITextElements = setupUITextElements(pane);
        pane.getChildren().add(ship.getCharacter());

        AsteroidType.resetSpeeds();
        spawnAsteroids(numAsteroids);
    }

    protected abstract List<Text> setupUITextElements(Pane pane);

    protected abstract void load(Stage stage, int AsteroidCount);

    protected void animationHandle() {
        handleKeyInput();
        handleMoveObjects();
        handleCollisions();
        handleUpdateFrameCounters();
    }

    protected void populateKeyHashMap(Scene scene){
        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });
        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
         });
    }

    protected void handleKeyInput() {

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

        if (pressedKeys.getOrDefault(KeyCode.SHIFT, false)) {
            handleHyperJump();
        }
    }

    protected void spawnAsteroids(int asteroidCount){
        for (int i = 0; i < asteroidCount; i++) {
            Random rnd= new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT), AsteroidType.SMALL);
            asteroids.add(asteroid);
        }
        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));
    }


    protected void handleShipShooting() {
        if (framesSinceLastShot >= 15 && shoots.size() < 4) {
            // When shooting the bullet in the same direction as the ship
            Projectile shot = ship.shoot();
            shoots.add(shot);
            pane.getChildren().add(shot.getCharacter());
            // Reset the framesSinceLastShot counter
            framesSinceLastShot = 0;
        }


    }

    protected void handleHyperJump() {
        if (framesSinceLastHyperJump >= 20) {
            handleMoveShipToSafety();
            framesSinceLastHyperJump = 0;
        }
    }

    protected void handleCollisions() {
        manageBulletCollisions();
        manageAsteroids();
    }

    protected void manageBulletCollisions() {

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

                int deadAsteroidCount = checkBulletCollisions(shoot);

                if (deadAsteroidCount > 0) {
                    // Remove the projectile that collided with the asteroid
                    projectileIterator.remove();
                    pane.getChildren().remove(shoot.getCharacter());
                }
            }
        }
    }

    protected int checkBulletCollisions(Projectile shoot) {
        List<Asteroid> destroyedAsteroids;

        // Create a list of broken asteroids
        destroyedAsteroids = asteroids.stream()
                .filter(asteroid -> asteroid.collide(shoot))
                .toList();

        // If any asteroids have been hit by a projectile, remove them
        if (!destroyedAsteroids.isEmpty()) {
            destroyedAsteroids.forEach(delete -> {
                asteroids.remove(delete);
                pane.getChildren().remove(delete.getCharacter());
                List<Asteroid> newAsteroids = delete.onDestroy();
                newAsteroids.forEach(asteroid -> {
                    asteroids.add(asteroid);
                    pane.getChildren().add(asteroid.getCharacter());
                });
            });
        }

        return destroyedAsteroids.size();
    }

    protected void manageAsteroids() {
        if (asteroids.isEmpty()) {
            int newNumAsteroids = numAsteroids + 1;
            for (int i = 0; i < newNumAsteroids; i++) {
                Random rnd = new Random();
                Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT), AsteroidType.LARGE);
                asteroids.add(asteroid);
                pane.getChildren().add(asteroid.getCharacter());
            }
        }
        if (framesSinceLastRandomAsteroid > 10 && Math.random() < 0.005) {
            double rnd_2 = Math.random() * 1000;
            Asteroid asteroid = new Asteroid((int) rnd_2 % WIDTH, 0, AsteroidType.MEDIUM);
            if (!asteroid.collide(ship)) {
                asteroids.add(asteroid);
                pane.getChildren().add(asteroid.getCharacter());
            }
            framesSinceLastRandomAsteroid = 0;
        }
    }

    protected void handleUpdateFrameCounters() {
        framesSinceLastShot++;
        framesSinceLastRandomAsteroid++;
        framesSinceLastHyperJump++;
    }

    protected void handleMoveObjects() {
        ship.move();
        shoots.forEach(shot -> shot.move());
        asteroids.forEach(asteroid -> asteroid.move());
    }

    protected void handleMoveShipToSafety() {
        characters.addAll(asteroids);
        ship.moveSomewhereSafe(characters, 150);
        characters.clear();
    }

}








//            private void handleCheating(){
//                if  (level.get() >= 3 && framesSinceLastGodMode > 250){
//                    cheat(pane);
//                    framesSinceLastGodMode = 0;
//                }
//            }

//            private void handleAlienShooting() {
//                if (alienShip.isAlive()) {
//                    if (framesSinceLastAlienShot >= 100) {
//                        Projectile alienShot = alienShip.shootAtTarget(ship);
//                        alienShoots.add(alienShot);
//                        pane.getChildren().add(alienShot.getCharacter());
//                        // Reset the framesSinceLastShot counter
//                        framesSinceLastAlienShot = 0;
//                    }
//                }
//            }













//            private void damageShip() {
//                if (!ship.isInvincible()) {
//                    HP.decrementAndGet();
//
//                    if (HP.get() > 0) {
//                        moveShipToSafety();
//                        ship.setMovement(ship.getMovement().normalize());
//
//                        ship.setInvincible(true); // Set ship invincible
//
//                        // Blinking animation
//                        Timeline blinkTimeline = new Timeline(
//                                new KeyFrame(Duration.ZERO, e -> ship.getCharacter().setVisible(true)),
//                                new KeyFrame(Duration.millis(100), e -> ship.getCharacter().setVisible(false)),
//                                new KeyFrame(Duration.millis(200), e -> ship.getCharacter().setVisible(true))
//                        );
//                        blinkTimeline.setCycleCount(15); // Number of blinks (15 blinks in 3 seconds)
//                        blinkTimeline.play();
//
//                        Timer timer = new Timer();
//                        timer.schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                ship.setInvincible(false); // Remove invincibility after 3 seconds
//                                ship.getCharacter().setVisible(true); // Ensure the ship is visible after the invincibility period
//                            }
//                        }, 3000);
//                    } else {
//                        stop();
//                        pane.getChildren().clear();
//                        int finalPoints = points.get();
//                        Scene gameOverScene = new GameOver().showGameOverScreen(stage, finalPoints);
//                        stage.setScene(gameOverScene);
//                    }
//                }
//
//            }

//