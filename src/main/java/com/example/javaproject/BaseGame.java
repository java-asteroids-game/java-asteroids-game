package com.example.javaproject;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public abstract class BaseGame {

    // BaseGame must have a stage
    Stage stage;

    // Size for both the GameWindow and controlWindow
    public static final int WIDTH = 960;
    public static final int HEIGHT = 600;

    // Initialize frame counters
    int framesSinceLastShot = 15;
    int framesSinceLastRandomAsteroid = 0;
    int framesSinceLastHyperJump = 0;

    // Initialize characters on the screen
    List<Asteroid> asteroids = new ArrayList<>();
    List<Projectile> shoots = new LinkedList<>();
    List<AbstractGameElement> characters = new ArrayList<>();
    PlayerShip ship = new PlayerShip(WIDTH / 2, HEIGHT / 2);

    // Initialize the number of asteroids
    int numAsteroids;

    // Intialize the pane, scene, and keys hashnap
    Pane pane = new Pane();
    Scene scene;
    Map<KeyCode, Boolean> pressedKeys = new HashMap<>();

    // Initialize list of UI elements
    List<Text> UITextElements;


    // Setup game method. This method will be called in the subclasses load method
    protected void setupGame(int numAsteroids){
        // Initialize a new pane with the game width and height
        pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);
        pane.setStyle("-fx-background-color: black");
        // Create a new scene
        scene = new Scene(pane);
        // Call the method to set up the key input hashmap
        populateKeyHashMap(scene);
        // Call the method that has been implemented by the subclass to set up UI elements
        UITextElements = setupUITextElements(pane);
        // Add the ship to the pane
        pane.getChildren().add(ship.getCharacter());
        // Reset the asteroid speeds
        AsteroidType.resetSpeeds();
        // Spawn the required number of asteroids
        spawnAsteroids(numAsteroids);
    }


    // Abstract method. All subclasses of the BaseGame class must have a setupUIText Elements
    // which will create text elements and place them in the pane
    protected abstract List<Text> setupUITextElements(Pane pane);

    // Abstract method. All subclasses of the BaseGame class must have a load method with a
    // stage and a number of asteroids
    protected abstract void load(Stage stage, int AsteroidCount);

    // Method to capture all methods that should be called when a scene is being animated
    protected void animationHandle() {
        handleKeyInput();
        handleMoveObjects();
        handleCollisions();
        handleUpdateFrameCounters();
    }

    // Adds keys being pressed on the scene to the pressedKeys hashmap
    // Allows for holding the keys
    protected void populateKeyHashMap(Scene scene){
        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });
        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
         });
    }

    // Method to take key input and call other methods accordingly.
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

    // Method to spawn asteroids in the game pane.
    protected void spawnAsteroids(int asteroidCount){
        for (int i = 0; i < asteroidCount; i++) {
            Random rnd= new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT), AsteroidType.SMALL);
            asteroids.add(asteroid);
        }
        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));
    }


    // Method to handle ship shooting, makes sure there aren't more than 4 bullets on the screen at a time.
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

    // Method for handling hyper jump, calls the move ship to safety function and prevents hyperjumps
    // with less than 20 frames in between.
    protected void handleHyperJump() {
        if (framesSinceLastHyperJump >= 20) {
            handleMoveShipToSafety();
            // Reset the framesSinceLastHyperJump counter
            framesSinceLastHyperJump = 0;
        }
    }

    // Calls game methods to handle collisions. Checks for asteroid bullet collisions
    // and asteroid collisions with the ship
    protected void handleCollisions() {
        manageBulletCollisions();
        manageAsteroids();
    }

    // Checks if a bullet has collided with an asteroid and removes bullets that have gone out
    // of bounds of the screen.
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
                // Get the number of asteroids that have been destroyed by bullets
                int deadAsteroidCount = checkBulletCollisions(shoot);

                // If any of the asteroids have been destroyed
                if (deadAsteroidCount > 0) {
                    // Remove the projectile that collided with the asteroid
                    projectileIterator.remove();
                    pane.getChildren().remove(shoot.getCharacter());
                }
            }
        }
    }

    // Method that checks if bullets have collided with asteroids. Returns the number
    // of asteroids destroyed so that the manageBulletCollisions method knows which bullets to delete.
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
                // Call the asteroids onDestroy method, which returns smaller asteroids
                List<Asteroid> newAsteroids = delete.onDestroy();
                newAsteroids.forEach(asteroid -> {
                    asteroids.add(asteroid);
                    pane.getChildren().add(asteroid.getCharacter());
                });
            });
        }
        return destroyedAsteroids.size();
    }

    // Manage asteroids, spawn them as necessary and
    protected void manageAsteroids() {
        if (asteroids.isEmpty()) {
            int newNumAsteroids = numAsteroids + 1;
            spawnAsteroids(newNumAsteroids);
        }

        // Generate a new random asteroid
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

    // Increment all the frameCounters, call this method in the animation timer
    protected void handleUpdateFrameCounters() {
        framesSinceLastShot++;
        framesSinceLastRandomAsteroid++;
        framesSinceLastHyperJump++;
    }

    // Handle the movement of all of the objects on screen
    // Calls the move methods for all of the objects
    protected void handleMoveObjects() {
        ship.move();
        shoots.forEach(shot -> shot.move());
        asteroids.forEach(asteroid -> asteroid.move());
    }

    // Moves the ship to a safe space on the screen
    // Creates a list of all the characters to avoid, and calls the moveSomewhereSafe method on the ship
    protected void handleMoveShipToSafety() {
        characters.addAll(asteroids);
        ship.moveSomewhereSafe(characters, 150);
        characters.clear();
    }

}