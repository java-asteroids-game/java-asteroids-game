package com.example.javaproject;

public enum AsteroidType {
    SMALL(13.0, 0.5),
    MEDIUM(24.0, 0.2),
    LARGE(35.0, 0.1),
    SPECIAL(68.0, 0.4);

    private final double size;
    private double speed;
    //private final double defaultRadius = 50;

    //AsteroidType constructor
    AsteroidType(double size, double speed) {
        this.size = size;
        this.speed = speed;
    }

    //getter method for Asteroid size
    public double getSize() {
        return this.size;
    }

    //getter method for Asteroid speed
    public double getSpeed(){
        return this.speed;
    }

    //setter method for Asteroid speed
    private void setSpeed(double speed){
        this.speed = speed;
    }

    //increases the speeds of all asteroids by a given double speedIncrease
    public static void increaseSpeeds(double speedIncrease) {
        for (AsteroidType asteroidType : AsteroidType.values()){
            asteroidType.setSpeed(asteroidType.speed + speedIncrease);
        }
    }

    //resets speeds to default values
    public static void resetSpeeds(){
        SMALL.setSpeed(0.5);
        MEDIUM.setSpeed(0.2);
        LARGE.setSpeed(0.1);
        SPECIAL.setSpeed(0.4);
    }

}
