package com.example.javaproject;

public enum AsteroidType {
    SMALL(13.0),
    MEDIUM(24.0),
    LARGE(35.0),
    SPECIAL(68.0);

    private final double size;
    //private final double defaultRadius = 50;

    AsteroidType(double size) {
        this.size = size;
    }

    public double getSize() {
        return size;
    }

}
