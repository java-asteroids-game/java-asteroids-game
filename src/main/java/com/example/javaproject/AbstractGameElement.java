package com.example.javaproject;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public abstract class AbstractGameElement {

    public Polygon character;
    public Point2D movement;
    public boolean Alive;
    public static double size;

    public AbstractGameElement(Polygon polygon, int x, int y) {
        this.character = polygon;
        this.character.setTranslateX(x);
        this.character.setTranslateY(y);
        // Object border color
        this.character.setStroke(Color.WHITE);

        this.movement = new Point2D(0, 0);
    }

    public Polygon getCharacter() {
        return character;
    }

    public abstract void move();
    public abstract boolean collide(AbstractGameElement other);
//    public boolean setAlive(boolean alive){ return !alive; }
//
//    public boolean isAlive(){ return true; }

}