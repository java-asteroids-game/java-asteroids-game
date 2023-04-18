package com.example.javaproject;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class CharacterFactory {
    // Static variable to allows us to adjust the scale of the UI elements if needed

    public Polygon createShip() {

        final double SCALE = 2;

        // Construct a shape centered around the position point passed in, multiplied by the global scale
        Polygon shipShape = new Polygon(
                -3.8 * SCALE,	-4.5 * SCALE,
                -3.05 * SCALE,	-2.7 * SCALE,
                -4.85 * SCALE,	-1.8 * SCALE,
                -5 * SCALE,	0 * SCALE,
                -4.85 * SCALE,	1.8 * SCALE,
                -3.05 * SCALE,	2.85 * SCALE,
                -3.8 * SCALE,	4.65 * SCALE,
                -3.8 * SCALE,	4.8 * SCALE,
                -3.65 * SCALE,	4.8 * SCALE,
                -2 * SCALE,	4.8 * SCALE,
                -0.65 * SCALE,	4.2 * SCALE,
                -0.2 * SCALE,	3.75 * SCALE,
                1.3 * SCALE,	3.9 * SCALE,
                3.55 * SCALE,	3.9 * SCALE,
                5.5 * SCALE,	3 * SCALE,
                9.7 * SCALE,	0.6 * SCALE,
                10 * SCALE,	0 * SCALE,
                9.7 * SCALE,	-0.6 * SCALE,
                5.5 * SCALE,	-3 * SCALE,
                3.55 * SCALE,	-3.9 * SCALE,
                1.3 * SCALE,	-3.9 * SCALE,
                -0.2 * SCALE,	-3.6 * SCALE,
                -0.65 * SCALE,	-4.05 * SCALE,
                -2 * SCALE,	-4.8 * SCALE,
                -3.65 * SCALE,	-4.65 * SCALE,
                -3.8 * SCALE,	-4.5 * SCALE);


        // Set color of ship
        shipShape.setStroke(Color.WHITE);
        shipShape.setFill(Color.DARKGRAY);

        // Return ship shape
        return shipShape;
    }

    public Polygon createAsteroid(AsteroidType type) {
        
        double asteroidSize = type.getSize();
//
        // Declare the new polygon, asteroidShape
        Polygon asteroidShape = new Polygon(
                asteroidSize,                             0.0,
                asteroidSize * Math.cos(Math.PI * 2 / 5),         -1 * asteroidSize * Math.sin(Math.PI * 2 / 5),
                -1 * asteroidSize * Math.cos(Math.PI / 5),        -1 * asteroidSize * Math.sin(Math.PI * 4 / 5),
                -1 * asteroidSize * Math.cos(Math.PI / 5),        asteroidSize * Math.sin(Math.PI * 4 / 5),
                asteroidSize * Math.cos(Math.PI * 2 / 5),         asteroidSize * Math.sin(Math.PI * 2 / 5));
//
//        // Return the randomly generated asteroid shape
        return asteroidShape;
    }

    public Polygon createBullet() {

        final double SCALE = 2;

        // Construct a shape centered around the position point passed in, multiplied by the global scale
        Polygon bulletShape = new Polygon (
                0.95 * SCALE,	-0.2 * SCALE,
                0.59 * SCALE,	-0.43 * SCALE,
                0.25 * SCALE,	-0.5 * SCALE,
                -1 * SCALE,	-0.5 * SCALE,
                -1 * SCALE,	0.5 * SCALE,
                0.25 * SCALE,	0.5 * SCALE,
                0.59 * SCALE,	0.43 * SCALE,
                0.95 * SCALE,	0.2 * SCALE,
                1 * SCALE,	0 * SCALE,
                0.95 * SCALE,	-0.2 * SCALE);

        // Return bullet shape
        return bulletShape;
    }

    public Polygon createEnemyShip() {

        final double SCALE = 2;

        // Construct a shape centered round the position point passe in, multiplied by the global scale
        Polygon alienShape = new Polygon(
                15 * SCALE,	4.2 * SCALE,
                6 * SCALE,	-2.4 * SCALE,
                5.4 * SCALE,	-4.8 * SCALE,
                4.2 * SCALE,	-6.6 * SCALE,
                2.4 * SCALE,	-7.8 * SCALE,
                0 * SCALE,	-8.4 * SCALE,
                -2.4 * SCALE,	-7.8 * SCALE,
                -4.2 * SCALE,	-6.6 * SCALE,
                -5.4 * SCALE,	-4.8 * SCALE,
                -6 * SCALE,	-2.4 * SCALE,
                -15 * SCALE,	4.2 * SCALE,
                -10.2 * SCALE,	4.2 * SCALE,
                -7.8 * SCALE,	6.6 * SCALE,
                -4.8 * SCALE,	7.8 * SCALE,
                0 * SCALE,	8.4 * SCALE,
                4.8 * SCALE,	7.8 * SCALE,
                7.8 * SCALE,	6.6 * SCALE,
                10.2 * SCALE,	4.2 * SCALE);

        return alienShape;
    }

}
