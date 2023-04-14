package com.example.javaproject;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class PlayerShip extends AbstractGameElement {

    public PlayerShip(int x, int y) {
        super(new Polygon(-5, -5, 10, 0, -5, 5), x, y);
    }
    @Override
    public void move() {
        this.character.setTranslateX(this.character.getTranslateX() + this.movement.getX());
        this.character.setTranslateY(this.character.getTranslateY() + this.movement.getY());

        if (this.character.getTranslateX() < 0) {
            this.character.setTranslateX(this.character.getTranslateX() + GameWindow.WIDTH);
        }

        if (this.character.getTranslateX() > GameWindow.WIDTH) {
            this.character.setTranslateX(this.character.getTranslateX() % GameWindow.WIDTH);
        }

        if (this.character.getTranslateY() < 0) {
            this.character.setTranslateY(this.character.getTranslateY() + GameWindow.HEIGHT);
        }

        if (this.character.getTranslateY() > GameWindow.HEIGHT) {
            this.character.setTranslateY(this.character.getTranslateY() % GameWindow.HEIGHT);
        }

    }

    public void turnLeft() {
        this.character.setRotate(this.character.getRotate() - 3);
    }


    public void turnRight() {
        this.character.setRotate(this.character.getRotate() + 3);
    }

    public void accelerate() {
        double changeX = Math.cos(Math.toRadians(this.getCharacter().getRotate()));
        double changeY = Math.sin(Math.toRadians(this.getCharacter().getRotate()));

        changeX *= 0.01;
        changeY *= 0.01;
        this.movement = this.movement.add(changeX, changeY);
    }

    public boolean collide(AbstractGameElement other) {
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

}