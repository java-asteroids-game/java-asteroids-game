package com.example.javaproject;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

// Projectile class extends the abstract base class AbstractGameElement
public class Projectile extends AbstractGameElement {

    // Speed of the projectile
    public double speed;
    // Type of the projectile, defined by an enumeration called ProjectileType
    public final ProjectileType type;

    // Constructor for the Projectile class
    public Projectile(int x, int y, ProjectileType type) {
        // Calling the parent class constructor (AbstractGameElement) with the necessary parameters
        super(new CharacterFactory().createBullet(), x, y);

        // Set the fill color and stroke color of the projectile according to the projectile type
        this.character.setFill(Color.valueOf(type.getColor()));
        this.character.setStroke(Color.valueOf(type.getColor()));
        // Set the projectile type
        this.type = type;
    }

    // Set the speed of the projectile
    public void setSpeed(double speed) {
        // Define a minimum speed for the projectile
        double minSpeed = 5.0;

        // Set the projectile speed, ensuring it is at least the minimum speed
        this.speed = Math.max(minSpeed, speed);
    }

    // Move the projectile based on its current rotation and speed
    public void move() {
        // Calculate the movement vector based on the rotation angle and speed
        this.movement = new Point2D(Math.cos(Math.toRadians(this.getCharacter().getRotate())),
                Math.sin(Math.toRadians(this.getCharacter().getRotate()))).multiply(this.speed);

        // Update the position of the projectile using the movement vector
        this.getCharacter().setTranslateX(getCharacter().getTranslateX() + this.movement.getX());
        this.getCharacter().setTranslateY(this.getCharacter().getTranslateY() + this.movement.getY());
    }

    // Check if the projectile collides with another game element
    public boolean collide(AbstractGameElement other) {
        // Calculate the intersection area between the projectile and the other game element
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());

        // If the width of the collision area is not -1, then the projectile has collided with the other game element
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

    // Check if the projectile has moved out of the game window's bounds
    public boolean outOfBounds() {
        // Check if the projectile's position is outside the game window's boundaries
        if (this.getCharacter().getTranslateX() < 0 || this.getCharacter().getTranslateX() > GameWindow.WIDTH ||
                this.getCharacter().getTranslateY() < 0 || this.getCharacter().getTranslateY() > GameWindow.HEIGHT) {
            // If the projectile is out of bounds, return true
            return true;
        }
        // If the projectile is within the game window's boundaries, return false
        return false;
    }
}
