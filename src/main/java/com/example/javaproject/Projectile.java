package com.example.javaproject;

import javafx.scene.shape.Shape;

public class Projectile extends AbstractGameElement {

    public Projectile(int x, int y) {
        super(new CharacterFactory().createBullet(), x, y);
    }


    public void move() {
        double changeX = Math.cos(Math.toRadians(this.getCharacter().getRotate()));
        double changeY = Math.sin(Math.toRadians(this.getCharacter().getRotate()));

        this.getCharacter().setTranslateX(getCharacter().getTranslateX() + changeX * 5);
        this.getCharacter().setTranslateY(this.getCharacter().getTranslateY() + changeY * 5);

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
            //this.Alive=false;
            return true;
        }
        return false;
    }
}
