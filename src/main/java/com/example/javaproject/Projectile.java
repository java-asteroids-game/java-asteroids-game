package com.example.javaproject;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class Projectile extends AbstractGameElement {

    private static final int MAX_FLIGHT_TIME = 180;
    public  boolean alive;
    public double speed;

    public Projectile(int x, int y, ProjectileType type) {
        super(new CharacterFactory().createBullet(), x, y);
        this.character.setFill(Color.valueOf(type.getColor()));
        this.character.setStroke(Color.valueOf(type.getColor()));
        this.alive = true;
    }

    public void setSpeed(double speed) {
        double minSpeed = 5.0;
        this.speed = Math.max(minSpeed, speed);
    }

    public void move() {
        this.movement = new Point2D(Math.cos(Math.toRadians(this.getCharacter().getRotate())),
                Math.sin(Math.toRadians(this.getCharacter().getRotate()))).multiply(this.speed);

        this.getCharacter().setTranslateX(getCharacter().getTranslateX() + this.movement.getX());
        this.getCharacter().setTranslateY(this.getCharacter().getTranslateY() + this.movement.getY());

    }

    public boolean collide(AbstractGameElement other) {
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

    public boolean outOfBounds(){
        // Check if projectile is out of bounds
        if (this.getCharacter().getTranslateX() < 0 || this.getCharacter().getTranslateX() > GameWindow.WIDTH ||
                this.getCharacter().getTranslateY() < 0 || this.getCharacter().getTranslateY() > GameWindow.HEIGHT) {
            // Remove the projectile from the game window
            this.Alive = false;
            return true;
        }
        return false;
    }

}
