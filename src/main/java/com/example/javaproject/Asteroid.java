package com.example.javaproject;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Asteroid extends AbstractGameElement {
    public double move_speed;
    private final AsteroidType type;
    public double rotationalMovement;


    //this constructor takes x and y positions, and the AsteroidType enum as arguments, creates new Asteroid object
    public Asteroid(int x, int y, AsteroidType type) {

        super(new CharacterFactory().createAsteroid(type),x, y);
        Random rnd = new Random();
        this.type = type;
        this.move_speed = type.getSpeed();
        super.getCharacter().setRotate(rnd.nextInt(360));
        this.rotationalMovement = 0.5 - rnd.nextDouble();
    }

    //this static method creates a list of asteroids, of size numOfAsteroids. Asteroids in random location on screen
    public static List<Asteroid> createAsteroids(int numOfAsteroids, Pane pane1) {
        List<Asteroid> asteroids = new ArrayList<>();
        for (int i = 0; i < numOfAsteroids; i++) {
            Random rnd= new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(GameWindow.WIDTH), rnd.nextInt(GameWindow.HEIGHT), AsteroidType.SMALL);
            asteroids.add(asteroid);
        }

        return asteroids;
    }

    //a method for moving an Asteroid Object
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

    //this method increases the move speed of the Asteroid object
    public double Move_speed_up(){
        this.move_speed=0.05;
        return this.move_speed;
    }

    //this method creates new asteroids on destruction, where AsteroidType is SPECIAL, LARGE or MEDIUM
    public List<Asteroid> onDestroy(){

        List<Asteroid> newAsteroids = new ArrayList<Asteroid>();
        AsteroidType newAsteroidType = null;

        //defines newAsteroidType based on AsteroidType of destroyed Asteroid object
        switch (this.getType()) {
            case SPECIAL -> newAsteroidType = AsteroidType.LARGE;
            case LARGE -> newAsteroidType = AsteroidType.MEDIUM;
            case MEDIUM -> newAsteroidType = AsteroidType.SMALL;
            case SMALL -> {}
        }

        // creates two new asteroids of the required AsteroidType
        if (newAsteroidType != null){
            for (int i = 0; i < 2; i++){
                newAsteroids.add(new Asteroid((int) this.getCharacter().getTranslateX(), (int) this.character.getTranslateY(), newAsteroidType));
            }
        }

        return newAsteroids;
    }

    //a getter method to return AsteroidType
    public AsteroidType getType(){
        return this.type;
    }

}
