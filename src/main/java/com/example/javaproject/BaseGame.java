package com.example.javaproject;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public abstract class BaseGame {
    //game window size
    public static final int WIDTH = 960;
    public static final int HEIGHT = 600;

    int framesSinceLastShot = 15;
    int framesSinceLastAlienShot = 50;
    int framesSinceLastRandomAsteroid = 0;
    int framesSinceLastHyperJump = 0;

    List<Asteroid> asteroids = new ArrayList<>();
    List<Projectile> shoots = new LinkedList<>();
    List <AbstractGameElement> characters = new ArrayList<>();
    PlayerShip ship = new PlayerShip(WIDTH / 2, HEIGHT / 2);

    Pane pane;
    Map<KeyCode, Boolean> pressedKeys = new HashMap<>();

    //control ship
    Scene scene = new Scene(pane);

    public keyInputManager = new KeyInputManager(this);

    scene.setOnKeyPressed(event -> {
        pressedKeys.put(event.getCode(), Boolean.TRUE);
    });
    scene.setOnKeyReleased(event -> {
        pressedKeys.put(event.getCode(), Boolean.FALSE);
    });

    public abstract void load(Stage stage, int numAsteroids);

    public void handleInput() {

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

    }
    public abstract List<Text> setupUIElements(Pane pane);

    public abstract void handleCollisions();

    public abstract void moveShipToSafety();

    public void handleShipShooting() {
        if (framesSinceLastShot >= 15 && shoots.size() < 4) {
            // When shooting the bullet in the same direction as the ship
            Projectile shot = ship.shoot();
            shoots.add(shot);
            pane.getChildren().add(shot.getCharacter());
            // Reset the framesSinceLastShot counter
            framesSinceLastShot = 0;
        }
    }

    public void handleHyperJump() {
        if (framesSinceLastHyperJump >= 20){
            moveShipToSafety();
            framesSinceLastHyperJump = 0;
        }
    }

    public void manageAsteroids() {
        if (asteroids.isEmpty()) {

            int newNumAsteroids = numAsteroids + 1;
            for (int i = 0; i < newNumAsteroids; i++) {
                Random rnd = new Random();
                Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT), AsteroidType.LARGE);
                asteroids.add(asteroid);
                pane.getChildren().add(asteroid.getCharacter());
            }

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

    public void manageBulletAsteroidCollisions() {
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
                        List<Asteroid>  newAsteroids = delete.onDestroy();
                        newAsteroids.forEach(asteroid -> {
                            asteroids.add(asteroid);
                            pane.getChildren().add(asteroid.getCharacter());
                        });
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

    public void updateFrameCounters(){
        framesSinceLastShot++;
        framesSinceLastAlienShot++;
        framesSinceLastRandomAsteroid++;
        framesSinceLastHyperJump++;
    }

    public void moveObjects() {
        ship.move();
        shoots.forEach(shot -> shot.move());
        asteroids.forEach(asteroid -> asteroid.move());
    }

}
