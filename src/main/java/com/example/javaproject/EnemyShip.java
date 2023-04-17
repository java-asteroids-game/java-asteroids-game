package com.example.javaproject;

import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;

import static java.lang.System.currentTimeMillis;

public class EnemyShip extends AbstractGameElement {

    private long createdAt = currentTimeMillis();
    public double speed;
    private double changeX = Math.random() * 2 + 2;
    private double changeY = Math.random() * 2 + 2;
    private boolean alive = false;

    //creates a green polygon
    public EnemyShip(int x, int y) {
        super(new CharacterFactory().createEnemyShip(), x, y);
        this.character.setFill(javafx.scene.paint.Color.GREEN);
    }

    //getter method for the created time variable
    public long getCreatedTime() {
        return createdAt;
    }

    //sets new value for created time value
    public void setCreatedTime(long newValue) {
        createdAt = newValue;

    }

    public void move() {
        long currentTime = currentTimeMillis();
        long createdTime = this.getCreatedTime();

        // Change direction every 4 seconds
        if (currentTime - createdTime > 4000) {
            double angle = Math.random() * 2 * Math.PI;
            changeX = Math.cos(angle) * (Math.random() * 2 + 2);
            changeY = Math.sin(angle) * (Math.random() * 2 + 2);
            this.movement = new Point2D(changeX, changeY);
            setCreatedTime(currentTime);
        }

        // Move the alien ship
        this.getCharacter().setTranslateX(getCharacter().getTranslateX() + movement.getX());
        this.getCharacter().setTranslateY(getCharacter().getTranslateY() + movement.getY());

        // Methods to move the ship.
        //wrapScreen();
        bounceOffScreen();
    }


    public Projectile shootAtTarget(PlayerShip target) {
        // Set location to enemy ship location
        Projectile projectile = new Projectile((int) this.getCharacter().getTranslateX(), (int) this.getCharacter().getTranslateY());

        // Calculate angle between alien ship and target ship
        double angle = Math.toDegrees(Math.atan2(target.getCharacter().getTranslateY() - projectile.getCharacter().getTranslateY(),
                target.getCharacter().getTranslateX() - projectile.getCharacter().getTranslateX()));

        // Set the rotation of the projectile
        projectile.getCharacter().setRotate(angle);

        return projectile;
    }


    public boolean collide(AbstractGameElement other) {
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

    public boolean isAlive(){
        return this.alive;
    }

    public void setAlive(boolean alive){
        this.alive = alive;
    }
}
