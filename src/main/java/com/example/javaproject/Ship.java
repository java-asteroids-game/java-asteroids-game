package com.example.javaproject;

public interface Ship {

    void move();

    // Check if the AbstractGameElement collides with another AbstractGameElement
    boolean collide(AbstractGameElement other);

    public Projectile shoot();
}
