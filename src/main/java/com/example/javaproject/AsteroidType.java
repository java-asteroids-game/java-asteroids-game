package com.example.javaproject;

public enum AsteroidType {
    SMALL(13.0, 0.5),
    MEDIUM(24.0, 0.2),
    LARGE(35.0, 0.1),
    SPECIAL(68.0, 0.4);

    private final double size;
    private double speed;
    //private final double defaultRadius = 50;

    AsteroidType(double size, double speed) {
        this.size = size;
        this.speed = speed;
    }

    public double getSize() {
        return this.size;
    }

    public double getSpeed(){
        return this.speed;
    }

    private void setSpeed(double speed){
        this.speed = speed;
    }

    public static void increaseSpeeds(double speedIncrease) {
        for (AsteroidType asteroidType : AsteroidType.values()){
            asteroidType.setSpeed(asteroidType.speed + speedIncrease);
        }
    }

    public static void resetSpeeds(){
        SMALL.setSpeed(0.5);
        MEDIUM.setSpeed(0.2);
        LARGE.setSpeed(0.1);
        SPECIAL.setSpeed(0.4);
    }

}
