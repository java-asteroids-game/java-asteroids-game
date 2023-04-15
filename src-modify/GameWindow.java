package com.example.javaproject;

// Imports required packages

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static javafx.application.Application.launch;

public class GameWindow{
    //game window size
    public static final int WIDTH = 960;
    public static final int HEIGHT = 600;
    public static int MAX_ALIENS = 2;
    public int MAX_GENERATE_ALIENS = 3;
    int count_aliens = 0;

    long lastProjectileTime = 0;
    long lastAsteroidTime = 0;
    long timeBetweenAsteroids = 5_000_000_000L;



    public void load(Stage stage){
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);
        // Background color
        pane.setStyle("-fx-background-color: black");


        // Center position
        Random alien_rnd = new Random();

        PlayerShip ship = new PlayerShip(WIDTH/2, HEIGHT/2);


        //getchildredn method to add a shape
        pane.getChildren().add(ship.getCharacter());

        // Show current points ,current level, and current HP
        Text text = new Text(10, 20, "Current Points: 0");
        Text text1 = new Text(10,40,"Current Level: 1");
        Text text2 = new Text(10,60,"Current HP: 3");
        text.setFill(Color.WHITE);
        text1.setFill(Color.WHITE);
        text2.setFill(Color.WHITE);
        text.setStyle("-fx-font: 20 arial;");
        text1.setStyle("-fx-font: 20 arial;");
        text2.setStyle("-fx-font: 20 arial;");
        pane.getChildren().add(text);
        pane.getChildren().add(text1);
        pane.getChildren().add(text2);
        AtomicInteger points = new AtomicInteger();
        // Initial value
        AtomicInteger level = new AtomicInteger(1);
        AtomicInteger HP= new AtomicInteger(3);

        List<EnemyShip> alienship = new ArrayList<>();
        Timeline alienGenerator = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            if (alienship.size() < MAX_ALIENS && count_aliens < MAX_GENERATE_ALIENS) {
                EnemyShip alien = new EnemyShip((int)Math.random() * WIDTH, (int)Math.random()* HEIGHT);
                alienship.add(alien);
                pane.getChildren().add(alien.getCharacter());
                count_aliens ++;
            }
        }));
        alienGenerator.setCycleCount(Timeline.INDEFINITE);
        alienGenerator.play();

        //create the alien project, duration.second means the interval of shooting.
        List<EnemyProjectile> alienProjectiles = new ArrayList<>();
        Timeline alienProjectileGenerator = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            for (EnemyShip alien : alienship) {
                    EnemyProjectile alienProjectile = alien.shootAtTarget(ship);
                    alienProjectiles.add(alienProjectile);
                    pane.getChildren().add(alienProjectile.getCharacter());

            }
        }));
        alienProjectileGenerator.setCycleCount(Timeline.INDEFINITE);
        alienProjectileGenerator.play();

        List<Asteroid> asteroids = new ArrayList<>();
        double l=0.1;
        for (int i = 0; i < 5; i++) {
            Random rnd= new Random();
            double rnd_1= Math.random()*10+30;
            Asteroid asteroid = new Asteroid(rnd.nextInt(WIDTH / 3), rnd.nextInt(HEIGHT),rnd_1,l);
            asteroids.add(asteroid);
        }
        double scale=0.5;
        Asteroid asteroid_special=new Asteroid(WIDTH/2,500,40,l);
        asteroids.add(asteroid_special);


        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getCharacter()));


        List<Projectile> shoots = new ArrayList<>();
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

            @Override
            public void handle(long now) {
                if (pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnLeft();
                }

                if (pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnRight();
                }
                if (pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }

                if (pressedKeys.getOrDefault(KeyCode.SPACE, false)){
                    // When shooting the bullet in the same direction as the ship
                    Projectile shot = new Projectile((int) ship.getCharacter().getTranslateX(),
                            (int) ship.getCharacter().getTranslateY());

                    shot.getCharacter().setRotate(ship.getCharacter().getRotate());
                    shoots.add(shot);
                    shot.move();
                    pane.getChildren().add(shot.getCharacter());
                    pressedKeys.clear();
                }
                //Hyperspace jump
                scene.setOnKeyPressed(event -> {
                    pressedKeys.put(event.getCode(), Boolean.TRUE);

                    if (event.getCode() == KeyCode.SHIFT) {
                        double newX, newY;
                        do {
                            newX = Math.random() * WIDTH;
                            newY = Math.random() * HEIGHT;
                        } while (!isPositionSafe(newX, newY, ship, asteroids, shoots, alienship , 100)); // 100 is the safe distance

                        ship.hyperspaceJump(newX, newY);
                    }
                });

                ship.move();
                alienship.forEach(alien -> {
                    alien.move();
                });

                asteroids.forEach(asteroid -> asteroid.move());

                // shooting
                shoots.forEach(shoot -> shoot.move());

                //alien projectile move
                alienProjectiles.forEach(projectile -> projectile.move());
                //alien projectiles collide player ship
                alienProjectiles.forEach(projectile -> {
                    if (ship.collide(projectile)) {
                        HP.set(HP.get() - 1);
                        if(HP.get()>0) {
                            //getchildredn method to add a shape
                            ship.character.setTranslateX(WIDTH / 2);
                            ship.character.setTranslateY(500);
                            while (!isPositionSafe(WIDTH / 2, 500, ship, asteroids, shoots, alienship , 100));
                            text2.setText("Current HP: " + HP);
                        }else
                        {
                            stop();
                            text2.setText("GameOver");

                        }
                    }
                });
                //alien projectiles steam, remove dead alien projectiles
                List<EnemyProjectile> deadAlienProjectiles = alienProjectiles.stream()
                        .filter(projectile -> !projectile.isAlive())
                        .collect(Collectors.toList());

                deadAlienProjectiles.forEach(projectile -> {
                    pane.getChildren().remove(projectile.getCharacter());
                    alienProjectiles.remove(projectile);
                });


                List<Projectile> destroy_asteroid = shoots.stream().filter(shot -> {
                    List<Asteroid> destroy = asteroids.stream()
                            .filter(asteroids -> asteroids.collide(shot))
                            .collect(Collectors.toList());
                    if (destroy.isEmpty()) {
                        return false;
                    }
                    // Remove destroyed asteroid
                    destroy.stream().forEach(delete -> {
                        asteroids.remove(delete);
                        pane.getChildren().remove(delete.getCharacter());
                        for (int i = 0; i < 2 ; i++) {
                            if (delete.getSize()>10){
                                Asteroid asteroid = new Asteroid((int) delete.getCharacter()
                                        .getTranslateX(),(int)delete.getCharacter().getTranslateY(),delete.getSize()*scale,l + 0.2);
                                asteroids.add(asteroid);

                                pane.getChildren().add(asteroid.getCharacter());
                            }
                        }
                        // Count Level up
                    });
                    return true;
                }).collect(Collectors.toList());

                List<Projectile> destroy_alien = shoots.stream().filter(shot -> {
                    List<EnemyShip> destroy = alienship.stream()
                            .filter(alien -> alien.collide(shot))
                            .collect(Collectors.toList());
                    if (destroy.isEmpty()) {
                        return false;
                    }
                    // Remove destroyed asteroid
                    destroy.stream().forEach(delete -> {
                        alienship.remove(delete);
                        pane.getChildren().remove(delete.getCharacter());

                        // Count Level up
                    });
                    return true;
                }).collect(Collectors.toList());

                // Add points,HP and level
                destroy_asteroid.forEach(shot -> {
                    pane.getChildren().remove(shot.getCharacter());
                    shoots.remove(shot);
                    points.set(points.get()+100);
                    if(points.get()%1000==0){

                        HP.set(HP.get() + 1);
                        text2.setText("Current HP: " + HP);

                        level.set(level.get() + 1);
                        text1.setText("Current level: " + level);
                        asteroids.forEach(asteroid -> {
                            asteroid.move_speed+=(0.3 * level.get());
                        });
                    }
                    text.setText("Current Points: " + points);

                });

                // Hyper jumps and random reborn position
                double random_x = Math.random() * 1000 % WIDTH;
                double random_y = Math.random() * 700 % HEIGHT;
                asteroids.forEach(asteroid -> asteroid.move());
                asteroids.forEach(asteroid -> {
                    if (ship.collide(asteroid)) {
                        HP.set(HP.get() - 1);
                        if(HP.get()>0) {
                            //getchildredn method to add a shape
                            ship.character.setTranslateX(WIDTH / 2);
                            ship.character.setTranslateY(500);
                            while(asteroid.collide(ship))
                            {
                                ship.character.setTranslateX(Math.random()*WIDTH);
                                ship.character.setTranslateY(Math.random()*HEIGHT);
                            }
                            text2.setText("Current HP: " + HP);
                        }else
                        {
                            stop();
                            text2.setText("GameOver");

                        }
                    }
                });


                //Recreate random position asteroids
                if(Math.random() < 0.005) {
                    double rnd_2= Math.random()*10+30;
                    double rnd_3=Math.random()*1000;
                    Asteroid asteroid = new Asteroid((int) rnd_3%WIDTH, 0,rnd_2,l+0.3*level.get());
                    if(!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                }
            }

        }.start();


        //show everything in window
        stage.setTitle("Asteroids!");
        stage.setScene(scene);
        stage.show();
    }
    //Determine if the location is safe
    public boolean isPositionSafe(double x, double y, PlayerShip player, List<Asteroid> asteroids, List<Projectile> projectiles, List<EnemyShip> aliens, double safeDistance) {
        Point2D newPosition = new Point2D(x, y);

        // Check for collisions with asteroids
        for (Asteroid asteroid : asteroids) {
            if (newPosition.distance(asteroid.getCharacter().getTranslateX(), asteroid.getCharacter().getTranslateY()) < safeDistance) {
                return false;
            }
        }

        // Check for collisions with projectiles
        for (Projectile projectile : projectiles) {
            if (newPosition.distance(projectile.getCharacter().getTranslateX(), projectile.getCharacter().getTranslateY()) < safeDistance) {
                return false;
            }
        }

        // Check for collisions with alien ships
        for (EnemyShip  alien : aliens) {
            if (newPosition.distance(alien.getCharacter().getTranslateX(), alien.getCharacter().getTranslateY()) < safeDistance) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        launch(args);
    }
}