package com.example.javaproject;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

// AbstractGameElement is an abstract class representing a game element in the game
public abstract class AbstractGameElement {

    // The polygon shape representing the game element
    public Polygon character;
    // A Point2D object representing the movement vector of the game element
    public Point2D movement;

    // Constructor for the AbstractGameElement class
    public AbstractGameElement(Polygon polygon, int x, int y) {
        this.character = polygon;     // Assign the polygon shape
        this.character.setTranslateX(x);  // Set the x-coordinate of the game element
        this.character.setTranslateY(y);  // Set the y-coordinate of the game element
        this.movement = new Point2D(0, 0); // Initialize the movement vector to (0, 0)

        // Set the object border color
        this.character.setStroke(Color.WHITE);
    }

    // Getter method to return the Polygon object
    public Polygon getCharacter() {
        return character;
    }

    // Method to wrap the game element around the screen if it goes off the screen's boundaries
    public void wrapScreen() {
        // Store the current x and y coordinates of the game element
        double x = this.character.getTranslateX();
        double y = this.character.getTranslateY();

        // Wrap the game element horizontally
        if (x < 0) {
            x = GameWindow.WIDTH;
        } else if (x > GameWindow.WIDTH) {
            x = 0;
        }

        // Wrap the game element vertically
        if (y < 0) {
            y = GameWindow.HEIGHT;
        } else if (y > GameWindow.HEIGHT) {
            y = 0;
        }

        // Update the x and y coordinates of the game element
        this.character.setTranslateX(x);
        this.character.setTranslateY(y);
    }

    // Method to bounce the game element off the screen's boundaries
    public void bounceOffScreen() {
        // Check if the game element has gone beyond the left or right edge of the window
        if (this.getCharacter().getTranslateX() < 0 || this.getCharacter().getTranslateX() > GameWindow.WIDTH) {
            // Reverse the horizontal movement direction
            this.movement = new Point2D(-1 * this.movement.getX(), this.movement.getY());
        }

        // Check if the game element has gone beyond the top or bottom edge of the window
        if (this.getCharacter().getTranslateY() < 0 || this.getCharacter().getTranslateY() > GameWindow.HEIGHT) {
            // Reverse the vertical movement direction
            this.movement = new Point2D(this.movement.getX(), -1 * this.movement.getY());
        }
    }

    // Getter method to return the movement of a game element
    public Point2D getMovement() {
        return this.movement;
    }

    // Setter method to set the movement of a game element
    public void setMovement(Point2D movement) {
        this.movement = movement;
    }

    // Method to return the current position of the game element as a Point2D object
    public Point2D getPosition() {
        return new Point2D(this.getCharacter().getTranslateX(), this.getCharacter().getTranslateY());
    }

    // Abstract move method to be implemented by subclasses
    // This method will be responsible for updating the game element's position based on its movement
    public abstract void move();

    // Method to check if the game element collides with another game element
    public boolean collide(AbstractGameElement other) {
        // Calculate the intersection area between this game element and the other game element
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());

        // If the width of the collision area is not -1, then the game elements have collided
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

    // Method to return the speed of the game element
    // Calculated as the distance of the movement vector from the origin (0,0)
    public double getSpeed() {
        return this.movement.distance(0, 0);
    }
}