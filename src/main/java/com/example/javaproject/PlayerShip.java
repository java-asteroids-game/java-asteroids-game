package com.example.javaproject;

import javafx.scene.shape.Shape;

public class PlayerShip extends AbstractGameElement {

    public PlayerShip(int x, int y) {
        super(new CharacterFactory().createShip(), x, y);
    }
    @Override
    public void move() {
        this.character.setTranslateX(this.character.getTranslateX() + this.movement.getX());
        this.character.setTranslateY(this.character.getTranslateY() + this.movement.getY());

        wrapScreen();

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

    public boolean collide(AbstractGameElement other) {
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

}