package com.example.javaproject;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import static java.lang.System.currentTimeMillis;

public class EnemyShip extends AbstractGameElement {

    private long createdAt = currentTimeMillis();

    //creates a green polygon
    public EnemyShip(int x, int y) {
        super(new Polygon(-5, -5, 20, 20, -5, 25), x, y);
        this.character.setFill(javafx.scene.paint.Color.GREEN);

    }

    //getter method for the created time variable
    public long getCreatedTime() {
        return createdAt;
    }

    //sets new value for created time value
    public void setCreatedTime(long newValue) {
        createdAt = newValue;

    }

    public void move() {

        long currentTime = currentTimeMillis();
        long createdTime = this.getCreatedTime();

        //makes sure that the timelapse since the last rotation isn't less than 4 seconds
        if (currentTime - createdTime > 4000) {
            this.getCharacter().setRotate(Math.random() * 360);
            setCreatedTime(currentTime);

        }
//        print(this.character.getTranslat)

        //variables for speed
        double changeX = Math.cos(Math.toRadians(this.getCharacter().getRotate()));
        double changeY = Math.sin(Math.toRadians(this.getCharacter().getRotate()));

        //determines alien ship speed
        this.getCharacter().setTranslateX(getCharacter().getTranslateX() + changeX * -1);
        this.getCharacter().setTranslateY(getCharacter().getTranslateY() + changeY * -0.5);


        //ship stays on the screen
        if (this.getCharacter().getTranslateX() < GameWindow.WIDTH / 96 || this.getCharacter().getTranslateY() < GameWindow.HEIGHT / 60) {
            this.getCharacter().setTranslateX(changeX);
            this.getCharacter().setTranslateX(changeY);
        }
    }

    public boolean collide(AbstractGameElement other) {
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }
}
