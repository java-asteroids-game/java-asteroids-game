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
                if (framesSinceLastHyperJump >= 20){
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