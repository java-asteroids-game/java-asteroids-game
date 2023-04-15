package com.example.javaproject;


import javafx.scene.shape.Polygon;

    public class EnemyProjectile extends AbstractGameElement {

    private static final int MAX_FLIGHT_TIME = 180;
    private static final int MAX_TRAVEL_DISTANCE = 1000;
    private int timeToLive;
    private double traveledDistance;
    private final long creationTime;


    public EnemyProjectile(int x, int y) {
        super(new Polygon(2, -2, 2, 2, -2, 2, -2, -2), x, y);
        this.timeToLive = MAX_FLIGHT_TIME;
        this.creationTime = System.nanoTime();
        this.setAlive(true);
    }

    @Override
    public void move() {
        double oldX = getCharacter().getTranslateX();
        double oldY = getCharacter().getTranslateY();

        super.move();

        double newX = getCharacter().getTranslateX();
        double newY = getCharacter().getTranslateY();

        traveledDistance += Math.sqrt(Math.pow(newX - oldX, 2) + Math.pow(newY - oldY, 2));

        this.timeToLive--;

        if (this.timeToLive <= 0 || traveledDistance > MAX_TRAVEL_DISTANCE) {
            this.setAlive(false);
        }
    }
    @Override
    public boolean isAlive() {
        long elapsedTime = (System.nanoTime() - creationTime) / 1_000_000_000;
        return alive && elapsedTime < timeToLive;
    }

}

