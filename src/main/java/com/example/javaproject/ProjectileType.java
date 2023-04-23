package com.example.javaproject;

public enum ProjectileType {
    ALIEN("#91b37d"),
    PLAYER("#adddf0");

    // a final variable, stores the String 'color' or the projectile
    private final String color;

    //constructor for the ProjectileType enum
    ProjectileType(String color) {
        this.color = color;
    }

    //a getter method for the color
    public String getColor() {
        return color;
    }
}

