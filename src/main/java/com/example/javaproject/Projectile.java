package com.example.javaproject;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class Projectile extends AbstractGameElement {

    public Projectile(int x, int y) {
        super(new Polygon(1, -1, 1, 1, -1, 1, -1, -1), x, y);
    }


    public void move() {
        double changeX = Math.cos(Math.toRadians(this.getCharacter().getRotate()));
        double changeY = Math.sin(Math.toRadians(this.getCharacter().getRotate()));

        this.getCharacter().setTranslateX(getCharacter().getTranslateX() + changeX * 5);
        this.getCharacter().setTranslateY(this.getCharacter().getTranslateY() + changeY * 5);

//        if (this.character.getTranslateX() < 0) {
//            this.character.setTranslateX(this.character.getTranslateX() + GameWindow.WIDTH);
//        }
//
//        if (this.character.getTranslateX() > GameWindow.WIDTH) {
//            this.character.setTranslateX(this.character.getTranslateX() % GameWindow.WIDTH);
//        }
//
//        if (this.character.getTranslateY() < 0) {
//            this.character.setTranslateY(this.character.getTranslateY() + GameWindow.HEIGHT);
//        }
//
//        if (this.character.getTranslateY() > GameWindow.HEIGHT) {
//            this.character.setTranslateY(this.character.getTranslateY() % GameWindow.HEIGHT);
//        }
    }

    public boolean collide(AbstractGameElement other) {
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }
}
