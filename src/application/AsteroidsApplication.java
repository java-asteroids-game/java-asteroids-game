// Yuntao
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
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
public class AsteroidsApplication extends Application {

	public static int WIDTH = 800;
    public static int HEIGHT = 450;
    public static int MAX_ALIENS = 3;

    long lastProjectileTime = 0;
    long lastAsteroidTime = 0;
    long timeBetweenAsteroids = 5_000_000_000L;

    
	@Override
	public void start(Stage stage) throws Exception {
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);

        
        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2);
        
//        Alien alien = new Alien((int)Math.random() * WIDTH, (int)Math.random()* HEIGHT);
        //Generate an alien ship every certain time
        List<Alien> aliens = new ArrayList<>();
        Timeline alienGenerator = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            if (aliens.size() < MAX_ALIENS) {
                Alien alien = new Alien((int)Math.random() * WIDTH, (int)Math.random()* HEIGHT);
                aliens.add(alien);
                pane.getChildren().add(alien.getCharacter());
            }
        }));
        alienGenerator.setCycleCount(Timeline.INDEFINITE);
        alienGenerator.play();
        List<Projectile> projectiles = new ArrayList<>();
        List<Asteroid> asteroids = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT));
            asteroids.add(asteroid);
        }

        pane.getChildren().add(ship.getCharacter());
        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));
        aliens.forEach(alien -> pane.getChildren().add(alien.getCharacter()));
        
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
                
//            	long timeBetweenAlienProjectiles = 10_000_000_000L;
//                long now2 = System.nanoTime();
//                long lastAlienProjectileTime = 0;
//				if (now2 - lastAlienProjectileTime > timeBetweenAlienProjectiles) {
//                    lastAlienProjectileTime = now2;
//                    Projectile alienProjectile = new Projectile((int) alien.getCharacter().getTranslateX(), (int) alien.getCharacter().getTranslateY());
//                    alienProjectile.getCharacter().setRotate(alien.getCharacter().getRotate());
//                    projectiles.add(alienProjectile);
//                    alienProjectile.accelerate();
//                    alienProjectile.setMovement(new Point2D(Math.cos(Math.toRadians(alien.getCharacter().getRotate())), Math.sin(Math.toRadians(alien.getCharacter().getRotate()))).multiply(3));
//                    pane.getChildren().add(alienProjectile.getCharacter());
//                }

                ship.move();
                aliens.forEach(alien -> alien.move());
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
                	aliens.forEach(alien -> {
                        if(projectile.collide(alien)) {
                            projectile.setAlive(false);
                            alien.setAlive(false);
                        }
                    });

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
                
                List<Projectile> projectilesToRemove1 = projectiles.stream().filter(projectile -> {
                    List<Alien> collisions = aliens.stream()
                                                                .filter(alien -> alien.collide(projectile))
                                                                .collect(Collectors.toList());
                    

                    if(collisions.isEmpty()) {
                        return false;
                    }

                    collisions.stream().forEach(collided -> {
                        aliens.remove(collided);
                        pane.getChildren().remove(collided.getCharacter());
                    });

                    return true;
                }).collect(Collectors.toList());
                

                projectilesToRemove.forEach(projectile -> {
                    pane.getChildren().remove(projectile.getCharacter());
                    projectiles.remove(projectile);
                });

                projectilesToRemove1.forEach(projectile -> {
                    pane.getChildren().remove(projectile.getCharacter());
                    projectiles.remove(projectile);
                });
                
                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        stop();
                    }
                });
             // Check for collisions between ship and aliens
                aliens.forEach(alien -> {
                    if (ship.collide(alien)) {
                        stop();
                    }
                });
//                if (ship.collide(alien)) {
//                    stop();
//                }
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
// Conor
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
