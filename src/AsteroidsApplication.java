import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AsteroidsApplication extends Application {

    public static int WIDTH = 300;
    public static int HEIGHT = 200;

    @Override
    public void start(Stage stage) throws Exception {

            Pane pane = new Pane();
            pane.setPrefSize(WIDTH, HEIGHT);
            Text text = new Text(10, 20, "Points: ");
            pane.getChildren().add(text);

            AtomicInteger points = new AtomicInteger();

            Ship ship = new Ship(WIDTH / 2, HEIGHT / 2);
            List<Asteroid> asteroids = new ArrayList<>();
        List<Projectile> projectiles = new ArrayList<>();

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

            for (int i = 0; i < 5; i++) {
                Random rnd = new Random();
                Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT));
                asteroids.add(asteroid);
            }

            pane.getChildren().add(ship.getCharacter());
            asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));


            //Polygon ship = new Polygon(-5, -5, 10, 0, -5, 5);
            //ship.setTranslateX(300);
            //ship.setTranslateY(200);
            //ship.setRotate(30);
            //pane.getChildren().add(ship);

            Scene scene = new Scene(pane);
            stage.setTitle("Asteroids!");
            stage.setScene(scene);
            stage.show();

            Map<KeyCode, Boolean> pressedKeys = new HashMap<>();

            scene.setOnKeyPressed(event -> {
                pressedKeys.put(event.getCode(), Boolean.TRUE);
            });

            scene.setOnKeyReleased(event -> {
                pressedKeys.put(event.getCode(), Boolean.FALSE);
            });

            new AnimationTimer() {

                @Override
                public void handle(long now) {
                    text.setText("Points: " + points);
                    projectiles.forEach(projectile -> {
                        asteroids.forEach(asteroid -> {
                            if(projectile.collide(asteroid)) {
                                projectile.setAlive(false);
                                asteroid.setAlive(false);
                                points.addAndGet(100);
                            }
                        });

                        if(!projectile.isAlive()) {
                            text.setText("Points: " + points.addAndGet(1000));
                        }
                    });

                    if (pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                        ship.turnLeft();
                    }

                    if (pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                        ship.turnRight();
                    }

                    if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
                        ship.accelerate();
                    }

                    if (pressedKeys.getOrDefault(KeyCode.SPACE, false)) {
                        // we shoot
                        Projectile projectile = new Projectile((int) ship.getCharacter().getTranslateX(), (int) ship.getCharacter().getTranslateY());
                        projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
                        projectiles.add(projectile);

                        projectile.accelerate();
                        projectile.setMovement(projectile.getMovement().normalize().multiply(2));

                        pane.getChildren().add(projectile.getCharacter());
                    }


                    ship.move();
                    asteroids.forEach(asteroid -> asteroid.move());
                    projectiles.forEach(projectile -> projectile.move());
                    projectiles.forEach(projectile -> {
                        List<Asteroid> collisions = asteroids.stream()
                                .filter(asteroid -> asteroid.collide(projectile))
                                .collect(Collectors.toList());

                        collisions.stream().forEach(collided -> {
                            asteroids.remove(collided);
                            pane.getChildren().remove(collided.getCharacter());
                        });
                    });
                    projectiles.forEach(projectile -> {
                        asteroids.forEach(asteroid -> {
                            if(projectile.collide(asteroid)) {
                                projectile.setAlive(false);
                                asteroid.setAlive(false);
                            }
                        });
                    });

                    projectiles.stream()
                            .filter(projectile -> !projectile.isAlive())
                            .forEach(projectile -> pane.getChildren().remove(projectile.getCharacter()));
                    projectiles.removeAll(projectiles.stream()
                            .filter(projectile -> !projectile.isAlive())
                            .collect(Collectors.toList()));

                    asteroids.stream()
                            .filter(asteroid -> !asteroid.isAlive())
                            .forEach(asteroid -> pane.getChildren().remove(asteroid.getCharacter()));
                    asteroids.removeAll(asteroids.stream()
                            .filter(asteroid -> !asteroid.isAlive())
                            .collect(Collectors.toList()));
                }
            }.start();
        }
    public static void main(String[] args) {
        launch(args);
        }
    }