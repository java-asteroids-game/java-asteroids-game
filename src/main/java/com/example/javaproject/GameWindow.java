package com.example.javaproject;

// Imports required packages

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class GameWindow extends BaseGame{

    // Set additional frameCounters
    int framesSinceLastAlienShot = 50;
    int framesSinceLastGodMode = 0;

    // Declare alien and alienShoots information
    EnemyShip alienShip = new EnemyShip(WIDTH / 2, HEIGHT / 2, ship);
    List<Projectile> alienShoots = new ArrayList<>();

    // Declare boolean for cheater state of the game
    public boolean isCheating = false;

    // Set atomic integers with initial values
    AtomicInteger points = new AtomicInteger(0);
    AtomicInteger level = new AtomicInteger(1);
    AtomicInteger HP = new AtomicInteger(3);
    ProgressBar burnoutBar;

    // Declare boolean for gameOver
    boolean gameOver = false;

    public void load(Stage stage, int numAsteroids) {
        // Set game over to false when game is loaded
        gameOver = false;
        this.stage = stage;

        // Set up the game with however many asteroids are required
        setupGame(numAsteroids);
        burnoutBar = setupBurnoutBar(pane);
        setupAlienGenerator();
        spawnSpecialAsteroid();
        isCheating = false;

        // Start the animation timer
        new AnimationTimer() {

            // Call the animation handle method, and if the game is over, stop.
            @Override
            public void handle(long now) {
                animationHandle();
                if (gameOver){
                    stop();
                }
            }
        }.start();

        //show everything in window
        stage.setTitle("Asteroids!");
        stage.setScene(scene);
        stage.show();
    }

    // Method to handle player input, inherits from the base class
    // Additional functionality for cheat code, player can press U to cheat
    @Override
    protected void handleKeyInput(){
        super.handleKeyInput();
        if (pressedKeys.getOrDefault(KeyCode.U, false)) {
            handleCheating();
        }
    }

    // Create an observable list of bullets to display on the progress bar
    // When bullets are added or removed from the shoots list the progress bar is updated
    private void updateBurnoutBar(){
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
        burnoutBar.progressProperty().bind(progressBinding);
    }

    // Checks whether alien is alive, and how long it has been since the last alien shot
    // Calls the alienShip.shoot method to shoot at the player every 100 frames
    private void handleAlienShooting() {
        if (alienShip.isAlive()) {
            if (framesSinceLastAlienShot >= 100) {
                Projectile alienShot = alienShip.shoot();
                alienShoots.add(alienShot);
                pane.getChildren().add(alienShot.getCharacter());
                // Reset the framesSinceLastShot counter
                framesSinceLastAlienShot = 0;
            }
        }
    }

    // If the player is past level 3 and it's been 250 frames since the last cheat
    // Call the cheat method, which removes all asteroids from the pane
    private void handleCheating(){
        if  (level.get() >= 3 && framesSinceLastGodMode > 250){
            cheat(pane);
            framesSinceLastGodMode = 0;
        }
    }

    // Use the BaseGame's handle collision method plus adds the functionality to
    // handle alien bullet collisions with the player.
    @Override
    protected void handleCollisions() {
        super.handleCollisions();
        manageAlienBulletCollisions();
    }

    // Use the BaseGame's check bullet collisions method to check for asteroid / bullet collisions
    // Add the ability to check for alien / bullet collisions
    // Add the ability to update Points on asteroid/bullet collisions
    @Override
    protected int checkBulletCollisions(Projectile shoot){
        int deadAsteroidCount= super.checkBulletCollisions(shoot);

        if (alienShip.isAlive() && alienShip.collide(shoot)) {
            alienShip.setAlive(false);
            pane.getChildren().remove(alienShip.getCharacter());
        }
        for (int i = 0; i < deadAsteroidCount; i++){
            asteroidsHitUpdatePoints();
        }

        return deadAsteroidCount;
    }

    // Use the BaseGame's manage asteroids method, but reduce HP when the ship and
    // asteroid collide
    @Override
    protected void manageAsteroids(){
        super.manageAsteroids();

        asteroids.forEach(asteroid -> {
            if (ship.collide(asteroid)) {
                // Reduce ship HP
                damageShip();
            }
        });
    }

    // Use the BaseGame's handle move objects method, but add in movement for the alien
    // and its bullets
    @Override
    protected void handleMoveObjects() {
        super.handleMoveObjects();
        alienShip.move();
        alienShoots.forEach(alienShoot -> alienShoot.move());
    }

    // Use the Base Game's handle update frame counters
    // Also include the alien bullet counter and the cheat code counter
    @Override
    protected void handleUpdateFrameCounters(){
        super.handleUpdateFrameCounters();
        framesSinceLastAlienShot++;
        framesSinceLastGodMode++;
    }

    // Use the Base Game's move ship to safety method
    // Add the alienship and alien bullets to characters to avoid
    @Override
    protected void handleMoveShipToSafety(){
        characters.add(alienShip);
        characters.addAll(alienShoots);
        super.handleMoveShipToSafety();
    }

    // Override then Base Game's animation handle to include the
    // animations for aliens,
    @Override
    protected void animationHandle() {
        super.animationHandle();
        handleAlienShooting();
        updateGameInformation(UITextElements);
        updateBurnoutBar();

    }

    // Create a special asteroid and add it to the scene
    private void spawnSpecialAsteroid(){
        Asteroid asteroid_special = new Asteroid(WIDTH / 2, 500, AsteroidType.SPECIAL);
        asteroids.add(asteroid_special);
        pane.getChildren().add(asteroid_special.getCharacter());
    }

    // Iterate through the alien's bullets, check if it's gone out of bounds to remove
    // Check if it's collided with the ship. If so damage the ship
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
            }
        }
    }

    // Damage the ship if it is not invincible. Checks if HP has gone below 0. If not,
    // Set the ship to invincible and blink. If it has, set game over to true call the game over method.
    // and set the screen to the game over screen.
    private void damageShip() {
        if (!ship.isInvincible()) {
            HP.decrementAndGet();

            if (HP.get() > 0) {
                handleMoveShipToSafety();
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
                gameOver = true;
                pane.getChildren().clear();
                int finalPoints = points.get();
                Scene gameOverScene = new GameOver().showGameOverScreen(stage, finalPoints);
                stage.setScene(gameOverScene);
            }
        }

    }

    // Set up the UI elements. Create the text, place it on the screen, add it to the pane
    @Override
    protected List<Text> setupUITextElements(Pane pane){
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

    // Set up the burnout bar, place it on the screen, add it to the pane.
    private ProgressBar setupBurnoutBar(Pane pane){
        burnoutBar = new ProgressBar();
        burnoutBar.setPrefWidth(WIDTH - 870);
        burnoutBar.setLayoutX(30);
        burnoutBar.setLayoutY(HEIGHT - 500);
        burnoutBar.setProgress(0);
        pane.getChildren().add(burnoutBar);

        return burnoutBar;
    }

    // Increment points by 100 every time an asteroid is hit, increment the level
    // every 1000 points and add a life every 5000 points.
    private void asteroidsHitUpdatePoints() {
        if(!isCheating){
            points.set(points.get() + 100);
            if (points.get() % 1000 == 0) {
                level.incrementAndGet();
                AsteroidType.increaseSpeeds(0.2);
            }
            if (points.get()% 5000 == 0){
                HP.incrementAndGet();
            }
        }
    }
    
    // Method to update the displayed Points, Lives, and Level
    private void updateGameInformation(List<Text> textElements) {
        textElements.get(0).setText("Points: " + points);
        textElements.get(2).setText("Lives: " + HP);
        textElements.get(1).setText("Level: " + level);
        textElements.forEach(textElement -> textElement.toFront());
    }
    
    // Method for cheating. Destroys the asteroids on the screen and lets you know
    // that cheaters never prosper
    private void cheat(Pane pane){
        isCheating = true;
        //creates text to display when user cheats
        Text cheatText = new Text("CHEATERS LOSE THEIR POINTS");
        cheatText.setFont(Font.font("Consolas", FontWeight.BOLD, 24));
        cheatText.setFill(Color.RED);
        cheatText.setLayoutX(310);
        cheatText.setLayoutY(300);
        cheatText.setOpacity(0.0);

        pane.getChildren().add(cheatText);

        // Sets the duration and opacity for appearance of cheatText (alternates on/off)
        Timeline cheatTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> cheatText.setOpacity(1.0)),
                new KeyFrame(Duration.seconds(1.0), e -> cheatText.setOpacity(0.0))
        );
        // Sets the cycle as indefinite - remains until gameover
        cheatTimeline.setCycleCount(Timeline.INDEFINITE);
        cheatTimeline.play();
        
        // Clears asteroids
        asteroids.forEach(asteroid -> {
            pane.getChildren().remove(asteroid.getCharacter());
        });
        asteroids.clear();
        framesSinceLastGodMode = 0;
        level.set(3);
        points.set(0);

    }

    // Set up the timeline for the alien, every 15 seconds he shows up
    // Set the timeline to set the alien to alive, position him somewhere on the screen,
    // and add him to the pane
    private void setupAlienGenerator(){
        // Creates the generator for the alien. Sets up timeline events to check if the alien is alive
        Timeline alienGenerator = new Timeline();
        alienGenerator.getKeyFrames().add(
                new KeyFrame(Duration.seconds(15), event -> {
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
    }

}