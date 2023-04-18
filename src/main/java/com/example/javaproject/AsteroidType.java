package com.example.javaproject;

public enum AsteroidType {
    SMALL(13.0, 0.5),
    MEDIUM(24.0, 0.2),
    LARGE(35.0, 0.1),
    SPECIAL(68.0, 0.1);

    private final double size;
    private final double speed;
    //private final double defaultRadius = 50;

    AsteroidType(double size, double speed) {
        this.size = size;
        this.speed = speed;
    }

    public double getSize() {
        return size;
    }

    public double getSpeed(){
        return speed;
    }

}
