package com.example.javaproject;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public class Projectile extends AbstractGameElement {

    private static final int MAX_FLIGHT_TIME = 180;
    private static final int MAX_TRAVEL_DISTANCE = 1000;
    private final int timeToLive;
    private double traveledDistance;
    private final long creationTime;
    public Projectile(int x, int y) {

        super(new Polygon(1, -1, 1, 1, -1, 1, -1, -1), x, y);
        this.timeToLive = MAX_FLIGHT_TIME;
        this.creationTime = System.nanoTime();
        this.setAlive(true);
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
