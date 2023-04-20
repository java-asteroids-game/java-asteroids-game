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

public class GameWindow extends BaseGame{

    // Set additional frameCounters
    int framesSinceLastAlienShot = 50;
    int framesSinceLastGodMode = 0;

    // Declare alien and alienShoots information
    EnemyShip alienShip = new EnemyShip(WIDTH / 2, HEIGHT / 2, ship);
    List<Projectile> alienShoots = new ArrayList<>();

    public boolean isCheating = false;

    // Set atomic integers with initial values
    AtomicInteger points = new AtomicInteger(0);
    AtomicInteger level = new AtomicInteger(1);
    AtomicInteger HP = new AtomicInteger(3);
    ProgressBar progressBar;

    boolean gameOver = false;

    public void load(Stage stage, int numAsteroids) {
        gameOver = false;
        this.stage = stage;
        setupGame(numAsteroids);
        progressBar = setupProgressBar(pane);
        setupAlienGenerator();
        spawnSpecialAsteroid();
        isCheating = false;

        new AnimationTimer() {

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

    @Override
    protected void handleKeyInput(){
        super.handleKeyInput();
        if (pressedKeys.getOrDefault(KeyCode.U, false)) {
            handleCheating();
        }
    }
    @Override
    protected void handleShipShooting() {
        super.handleShipShooting();

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
    private void handleCheating(){
        if  (level.get() >= 3 && framesSinceLastGodMode > 250){
            cheat(pane);
            framesSinceLastGodMode = 0;
        }
    }
    @Override
    protected void handleCollisions() {
        super.handleCollisions();
        manageAlienBulletCollisions();
    }
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
    @Override
    protected void handleMoveObjects() {
        super.handleMoveObjects();
        alienShip.move();
        alienShoots.forEach(alienShoot -> alienShoot.move());
    }
    @Override
    protected void handleUpdateFrameCounters(){
        super.handleUpdateFrameCounters();
        framesSinceLastAlienShot++;
        framesSinceLastGodMode++;
    }
    @Override
    protected void handleMoveShipToSafety(){
        characters.add(alienShip);
        characters.addAll(alienShoots);
        super.handleMoveShipToSafety();
    }
    @Override
    protected void animationHandle() {
        super.animationHandle();
        handleAlienShooting();
        updateGameInformation(UITextElements);
    }
    private void spawnSpecialAsteroid(){
        Asteroid asteroid_special = new Asteroid(WIDTH / 2, 500, AsteroidType.SPECIAL);
        asteroids.add(asteroid_special);
        pane.getChildren().add(asteroid_special.getCharacter());
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
    private void updateGameInformation(List<Text> textElements) {
        textElements.get(0).setText("Points: " + points);
        textElements.get(2).setText("Lives: " + HP);
        textElements.get(1).setText("Level: " + level);
        textElements.forEach(Node::toFront);
    }
    private void cheat(Pane pane){
        isCheating = true;
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



        // Clears asteroids
        asteroids.forEach(asteroid -> {
            pane.getChildren().remove(asteroid.getCharacter());
        });
        asteroids.clear();
        framesSinceLastGodMode = 0;
        level.set(3);
        points.set(0);

    }
    private void setupAlienGenerator(){
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
    }

}