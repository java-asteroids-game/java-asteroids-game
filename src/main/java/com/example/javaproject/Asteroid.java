package com.example.javaproject;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Asteroid extends AbstractGameElement {
    public double move_speed;
    private final AsteroidType type;
    public double rotationalMovement;


    public Asteroid(int x, int y, AsteroidType type) {

        super(new CharacterFactory().createAsteroid(type),x, y);
        Random rnd = new Random();
        this.type = type;
        this.move_speed = type.getSpeed();
        super.getCharacter().setRotate(rnd.nextInt(360));
        this.rotationalMovement = 0.5 - rnd.nextDouble();
    }

    public static List<Asteroid> createAsteroids(int numOfAsteroids, Pane pane1) {
        List<Asteroid> asteroids = new ArrayList<>();
        for (int i = 0; i < numOfAsteroids; i++) {
            Random rnd= new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(GameWindow.WIDTH), rnd.nextInt(GameWindow.HEIGHT), AsteroidType.SMALL);
            asteroids.add(asteroid);
        }

        return asteroids;
    }

    public void move() {
        double changeX = Math.cos(Math.toRadians(this.getCharacter().getRotate()));
        double changeY = Math.sin(Math.toRadians(this.getCharacter().getRotate()));

        // Get the current position of the asteroid
        double currentX = this.getCharacter().getTranslateX();
        double currentY = this.getCharacter().getTranslateY();

        // Update the position of the asteroid
        this.getCharacter().setTranslateX(currentX + changeX * this.move_speed);
        this.getCharacter().setTranslateY(currentY + changeY * this.move_speed);

        this.getCharacter().setRotate(this.getCharacter().getRotate() + 0.1);

        wrapScreen();
    }

    public double Move_speed_up(){
        this.move_speed=0.05;
        return this.move_speed;
    }

    public List<Asteroid> onDestroy(){

        List<Asteroid> newAsteroids = new ArrayList<Asteroid>();
        AsteroidType newAsteroidType = null;

        switch (this.getType()) {
            case SPECIAL -> newAsteroidType = AsteroidType.LARGE;
            case LARGE -> newAsteroidType = AsteroidType.MEDIUM;
            case MEDIUM -> newAsteroidType = AsteroidType.SMALL;
            case SMALL -> {}
        }

        if (newAsteroidType != null){
            for (int i = 0; i < 2; i++){
                newAsteroids.add(new Asteroid((int) this.getCharacter().getTranslateX(), (int) this.character.getTranslateY(), newAsteroidType));
            }
        }

        return newAsteroids;
    }

    public AsteroidType getType(){
        return this.type;
    }

}
