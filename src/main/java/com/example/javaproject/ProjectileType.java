package com.example.javaproject;

public enum ProjectileType {
    ALIEN("#91b37d"),
    PLAYER("#adddf0");

    private final String color;

    ProjectileType(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}

