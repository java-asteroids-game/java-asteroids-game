package com.example.javaproject;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.Random;

import static java.lang.System.currentTimeMillis;
public class EnemyShip extends AbstractGameElement {


    private long createdAt = currentTimeMillis();

    private final static long MAX_FLIGHT_TIME = 5000; // 5 seconds
    private static final int MAX_MOVE_TIME = 100; // maximum time between random direction changes
    private int moveTimer; // timer for random direction changes


    private final Random random = new Random();
    private long startTime; // time when the alien was created
    //creates a green polygon
    private static Polygon createPolygon() {
        Polygon polygon = new Polygon(-15, 0, -10, -10, 10, -10, 15, 0, 10, 10, -10, 10);
        polygon.setFill(Color.BLUE);
        return polygon;
    }
    public EnemyShip(int x, int y) {
        super(createPolygon(), x, y);
        this.maxFlightTime = MAX_FLIGHT_TIME;
        this.startTime = System.currentTimeMillis();
        moveTimer = MAX_MOVE_TIME;

    }

    //getter method for the created time variable
    public long getCreatedTime() {
        return createdAt;
    }

    //sets new value for created time value
    public void setCreatedTime(long newValue) {
        createdAt = newValue;

    }

    @Override
    public void move() {
        super.move();

        // check if it's time to change direction
        if (moveTimer <= 0) {
            // reset the timer
            moveTimer = random.nextInt(MAX_MOVE_TIME) + 1;

            // change the direction
            double changeX = (random.nextDouble() * 3 - 1) * 0.3;
            double changeY = (random.nextDouble() * 3 - 1) * 0.3;
            Point2D newMovement = movement.add(changeX, changeY);

            if (newMovement.magnitude() < 5) {
                movement = newMovement;
                setMovement(getMovement().multiply(0.7));
            }
        } else {
            moveTimer--;
        }


        startTime = System.currentTimeMillis();
    }

    //alien ship shoot player ship
    public EnemyProjectile shootAtTarget(PlayerShip target) {
        EnemyProjectile enemyProjectile = new EnemyProjectile((int) this.getCharacter().getTranslateX(), (int) this.getCharacter().getTranslateY());
        //get player ship direction
        Point2D direction = new Point2D(target.getCharacter().getTranslateX() - enemyProjectile.getCharacter().getTranslateX(),
                target.getCharacter().getTranslateY() - enemyProjectile.getCharacter().getTranslateY());

        enemyProjectile.setMovement(direction.normalize().multiply(2));
        return enemyProjectile;
    }
    public boolean collide(AbstractGameElement other) {
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }
}
