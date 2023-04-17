package com.example.javaproject;

import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;

import java.util.List;

public class PlayerShip extends AbstractGameElement {

    private double speed = 0.0;

    private boolean invincible = false;
    public PlayerShip(int x, int y) {
        super(new CharacterFactory().createShip(), x, y);
    }
    @Override
    public void move() {
        this.character.setTranslateX(this.character.getTranslateX() + this.movement.getX());
        this.character.setTranslateY(this.character.getTranslateY() + this.movement.getY());
        wrapScreen();

    }

    public double getSpeed(){
        return (double) this.movement.distance(0,0);
    }

    public void turnLeft() {
        this.character.setRotate(this.character.getRotate() - 4);
    }


    public void turnRight() {
        this.character.setRotate(this.character.getRotate() + 4);
    }


    public void accelerate() {
        double changeX = Math.cos(Math.toRadians(this.getCharacter().getRotate()));
        double changeY = Math.sin(Math.toRadians(this.getCharacter().getRotate()));

        changeX *= 0.04;
        changeY *= 0.04;
        this.movement = this.movement.add(changeX, changeY);
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }
    public boolean collide(AbstractGameElement other) {
        if (invincible) {
            return false;
        }
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

    private boolean isPositionNotSafe(List<AbstractGameElement> characters, int safeDistance) {
        Point2D newPosition = new Point2D(this.getCharacter().getTranslateX(), this.getCharacter().getTranslateX());
        // Check for collisions with other characters
        for (AbstractGameElement character : characters){
            if (newPosition.distance(character.getCharacter().getTranslateX(), character.getCharacter().getTranslateY()) < safeDistance){
                return true;
            }
        }
        return false;
    }

    public void moveSomewhereSafe(List<AbstractGameElement> characters, int safeDistance){
            this.character.setTranslateY(Math.random() * GameWindow.WIDTH);
            this.character.setTranslateY(Math.random() * GameWindow.HEIGHT);
            while (this.isPositionNotSafe(characters, safeDistance)) {
                this.character.setTranslateX(Math.random() * GameWindow.WIDTH);
                this.character.setTranslateY(Math.random() * GameWindow.HEIGHT);
            };
    }


}