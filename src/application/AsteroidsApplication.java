package asteroids;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

public class AsteroidsApplication extends Application {

	public static int WIDTH = 800;
    public static int HEIGHT = 450;

    long lastProjectileTime = 0;
    long lastAsteroidTime = 0;
    long timeBetweenAsteroids = 5_000_000_000L;

    
	@Override
	public void start(Stage stage) throws Exception {
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);

        Alien alien = new Alien((int)Math.random() * WIDTH, (int)Math.random()* HEIGHT);
        
        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2);
        List<Projectile> projectiles = new ArrayList<>();
        List<Asteroid> asteroids = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT));
            asteroids.add(asteroid);
        }

        pane.getChildren().add(ship.getCharacter());
        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));
        pane.getChildren().add(alien.getCharacter());

        Scene scene = new Scene(pane);
        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();

        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });

        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
        });
        
        Point2D movement = new Point2D(1, 0);
        
        new AnimationTimer() {

            @Override
            public void handle(long now) {

                if(pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnLeft();
                }

                if(pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnRight();
                }
                if(pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }

                if (pressedKeys.getOrDefault(KeyCode.SPACE, false)) {
                    long now1 = System.nanoTime();
                    if (now1 - lastProjectileTime > 500_000_000) { // fire at most twice per second
                        lastProjectileTime = now1;
                        Projectile projectile = new Projectile((int) ship.getCharacter().getTranslateX(), (int) ship.getCharacter().getTranslateY());
                        projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
                        projectiles.add(projectile);
                        projectile.accelerate();
                        projectile.setMovement(projectile.getMovement().normalize().multiply(3));
                        pane.getChildren().add(projectile.getCharacter());
                    }
                }

                ship.move();
                alien.move();

                asteroids.forEach(asteroid -> asteroid.move());
                projectiles.forEach(projectile -> projectile.move());
                projectiles.forEach(projectile -> {
                    asteroids.forEach(asteroid -> {
                        if(projectile.collide(asteroid)) {
                            projectile.setAlive(false);
                            asteroid.setAlive(false);
                        }
                    });

                });
                projectiles.forEach(projectile -> {
                	if(projectile.collide(alien)) {
                        projectile.setAlive(false);
                        alien.setAlive(false);
                    }

                });
                
               
                List<Projectile> projectilesToRemove = projectiles.stream().filter(projectile -> {
                    List<Asteroid> collisions = asteroids.stream()
                                                                .filter(asteroid -> asteroid.collide(projectile))
                                                                .collect(Collectors.toList());

                    if(collisions.isEmpty()) {
                        return false;
                    }

                    collisions.stream().forEach(collided -> {
                        asteroids.remove(collided);
                        pane.getChildren().remove(collided.getCharacter());
                    });

                    return true;
                }).collect(Collectors.toList());
                
                

                projectilesToRemove.forEach(projectile -> {
                    pane.getChildren().remove(projectile.getCharacter());
                    projectiles.remove(projectile);
                });

                
                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        stop();
                    }
                });
             // Check for collisions between ship and aliens
                if (ship.collide(alien)) {
                    stop();
                }
            }

        }.start();

        stage.setTitle("Asteroids!");
        stage.setScene(scene);
        stage.show();
        
    }

    
    public static void main(String[] args) {
        launch(args);
    }
}