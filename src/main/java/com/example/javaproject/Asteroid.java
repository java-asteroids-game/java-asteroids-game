package com.example.javaproject;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.Random;

public class Asteroid<move_speed> extends AbstractGameElement {
    public double move_speed;
    public double rotationalMovement;
    public int x;
    public int y;
    public double size;

    public Asteroid(int x, int y, double s,double l) {

        super(new Polygon(s, 0.0, s * Math.cos(Math.PI * 2 / 5), -1 * s * Math.sin(Math.PI * 2 / 5),
                        -1 * s * Math.cos(Math.PI / 5), -1 * s * Math.sin(Math.PI * 4 / 5),
                        -1 * s * Math.cos(Math.PI / 5), s * Math.sin(Math.PI * 4 / 5),
                        s * Math.cos(Math.PI * 2 / 5), s * Math.sin(Math.PI * 2 / 5)),
                x, y);
        Random rnd = new Random();
        this.size=s;
        this.move_speed=l;

        super.getCharacter().setRotate(rnd.nextInt(360));
//        /* int accelerationAmount = 1 + rnd.nextInt(10);
//        for (int i = 0; i < accelerationAmount; i++) {
//            accelerate();
//        } */
        this.rotationalMovement = 0.5 - rnd.nextDouble();
    }
//    /* public double get_size(){
//        double S = this.size;
//        return S;
//    } */

    public void move() {
        double changeX = Math.cos(Math.toRadians(this.getCharacter().getRotate()));
        double changeY = Math.sin(Math.toRadians(this.getCharacter().getRotate()));

        // Get the current position of the asteroid
        double currentX = this.getCharacter().getTranslateX();
        double currentY = this.getCharacter().getTranslateY();

        // Calculate the new position of the asteroid
        double newX = currentX + changeX * this.move_speed;
        double newY = currentY + changeY * this.move_speed;

        // Check if the asteroid is off the screen
        if (newX < -this.getSize()) {
            // Wrap the asteroid to the right side of the screen
            newX = GameWindow.WIDTH + this.getSize();
        } else if (newX > GameWindow.WIDTH + this.getSize()) {
            // Wrap the asteroid to the left side of the screen
            newX = -this.getSize();
        }

        if (newY < -this.getSize()) {
            // Wrap the asteroid to the bottom of the screen
            newY = GameWindow.HEIGHT + this.getSize();
        } else if (newY > GameWindow.HEIGHT + this.getSize()) {
            // Wrap the asteroid to the top of the screen
            newY = -this.getSize();
        }

        // Update the position of the asteroid
        this.getCharacter().setTranslateX(newX);
        this.getCharacter().setTranslateY(newY);
    }
    public double getSize(){
        return this.size;
    }
    public double Move_speed_up(){
        this.move_speed=0.05;
        return this.move_speed;
    }

    public boolean collide(AbstractGameElement other) {
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }
}
