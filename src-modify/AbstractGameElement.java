package com.example.javaproject;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public abstract class AbstractGameElement {

    protected Polygon character;
    protected Point2D movement;

    protected boolean alive;
    protected long startTime;
    protected long maxFlightTime;



    public AbstractGameElement(Polygon polygon, int x, int y) {
        this.character = polygon;
        this.character.setTranslateX(x);
        this.character.setTranslateY(y);

        this.movement = new Point2D(0, 0);
        this.alive = true;
        polygon.setFill(Color.WHITE);

    }

    public AbstractGameElement(Polygon createAlien, Point2D point2d) {
        // TODO Auto-generated constructor stub
    }

    public Point2D getMovement() {
        return movement;
    }

    public void setMovement(Point2D movement) {
        this.movement = movement;
    }

    public Polygon getCharacter() {
        return character;
    }

    public void turnLeft() {
        this.character.setRotate(this.character.getRotate() - 5);
    }

    public void turnRight() {
        this.character.setRotate(this.character.getRotate() + 5);
    }

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

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        return alive && elapsedTime < maxFlightTime;
    }

    public void accelerate() {
        double changeX = Math.cos(Math.toRadians(this.character.getRotate()));
        double changeY = Math.sin(Math.toRadians(this.character.getRotate()));

        changeX *= 0.015;
        changeY *= 0.015;

        this.movement = this.movement.add(changeX, changeY);
    }

//    public boolean collide(Character other) {
//        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
//        return collisionArea.getBoundsInLocal().getWidth() != -1;
//    }
}