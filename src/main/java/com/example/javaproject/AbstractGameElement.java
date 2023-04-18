package com.example.javaproject;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public abstract class AbstractGameElement {

    public Polygon character;
    public Point2D movement;
    public boolean Alive;
    public static double size;

    public AbstractGameElement(Polygon polygon, int x, int y) {
        this.character = polygon;
        this.character.setTranslateX(x);
        this.character.setTranslateY(y);
        this.movement = new Point2D(0, 0);

        // Object border color
        this.character.setStroke(Color.WHITE);

    }

    public Polygon getCharacter() {
        return character;
    }

    public void wrapScreen() {
        // Store X and Y values in variables
        double x = this.character.getTranslateX();
        double y = this.character.getTranslateY();

        // Appear on right if character goes too far to the left
        if (x < 0) {
            x = GameWindow.WIDTH;
        }

        // Appear on left if character goes too far to the right
        else if (x > GameWindow.WIDTH) {
            x = 0;
        }

        // Appear on the bottom if character goes too far to the top
        if (y < 0) {
            y = GameWindow.HEIGHT;
        }

        // Appear on the top if character goes too far to the bottom
        else if ( y > GameWindow.HEIGHT)
            y = 0;

        // Update x and y
        this.character.setTranslateX(x);
        this.character.setTranslateY(y);
    }

    public void bounceOffScreen(){
        // Check if the ship has gone beyond the left or right edge of the window
        if (this.getCharacter().getTranslateX() < 0 || this.getCharacter().getTranslateX() > GameWindow.WIDTH) {
            this.movement = new Point2D(-1 * this.movement.getX(), this.movement.getY());
        }

        // Check if the ship has gone beyond the top or bottom edge of the window
        if (this.getCharacter().getTranslateY() < 0 || this.getCharacter().getTranslateY() > GameWindow.HEIGHT) {
            this.movement = new Point2D(this.movement.getX(), -1 * this.movement.getY());
        }
    }

    public Point2D getMovement(){
        return this.movement;
    }

    public void setMovement(Point2D movement){
        this.movement = movement;
    }

    public Point2D getPosition(){
        return new Point2D(this.getCharacter().getTranslateX(), this.getCharacter().getTranslateY());
    }

    public abstract void move();

    public boolean collide(AbstractGameElement other) {
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

    public double getSpeed(){
        return this.movement.distance(0,0);
    }




}