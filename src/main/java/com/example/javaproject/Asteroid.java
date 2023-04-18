package com.example.javaproject;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.Random;

public class Asteroid extends AbstractGameElement {
    public double move_speed;
    private AsteroidType type;
    public double rotationalMovement;
    public int x;
    public int y;
    public double size;


        public Asteroid(int x, int y, /*double s,*/ double l, AsteroidType type) {

            super(new Polygon(type.getSize(), 0.0, type.getSize() * Math.cos(Math.PI * 2 / 5), -1 * type.getSize() * Math.sin(Math.PI * 2 / 5),
                            -1 * type.getSize() * Math.cos(Math.PI / 5), -1 * type.getSize() * Math.sin(Math.PI * 4 / 5),
                            -1 * type.getSize() * Math.cos(Math.PI / 5), type.getSize() * Math.sin(Math.PI * 4 / 5),
                            type.getSize() * Math.cos(Math.PI * 2 / 5), type.getSize() * Math.sin(Math.PI * 2 / 5)),
                    x, y);
            Random rnd = new Random();
            this.type = type;
            this.move_speed = l;

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

        // Update the position of the asteroid
        this.getCharacter().setTranslateX(currentX + changeX * this.move_speed);
        this.getCharacter().setTranslateY(currentY + changeY * this.move_speed);

        wrapScreen();
    }

    /*
    public double getSize(){
        return this.size;
    }
     */
    public double Move_speed_up(){
        this.move_speed=0.05;
        return this.move_speed;
    }

    public AsteroidType getType(){
        return this.type;
    }

}
