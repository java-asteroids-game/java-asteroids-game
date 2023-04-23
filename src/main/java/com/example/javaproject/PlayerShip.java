package com.example.javaproject;

import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;

import java.util.List;

public class PlayerShip extends AbstractGameElement implements Ship {

    private boolean invincible = false;
    public PlayerShip(int x, int y) {
        super(new CharacterFactory().createShip(), x, y);
    }
    @Override
    public void move() {
        this.character.setTranslateX(this.character.getTranslateX() + this.movement.getX());
        this.character.setTranslateY(this.character.getTranslateY() + this.movement.getY());
        wrapScreen();

    }
    public void turnLeft() {
        this.character.setRotate(this.character.getRotate() - 4);
    }

    public void turnRight() {
        this.character.setRotate(this.character.getRotate() + 4);
    }

    public void accelerate() {
        double changeX = Math.cos(Math.toRadians(this.getCharacter().getRotate()));
        double changeY = Math.sin(Math.toRadians(this.getCharacter().getRotate()));

        changeX *= 0.04;
        changeY *= 0.04;
        this.movement = this.movement.add(changeX, changeY);
    }

    /*The invincible variable setted default value as false
    Only set to true each time the ship is destroyed and reset
    When invincible is True, the player ship will ignore collide.
    * */
    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;

    }
    public boolean collide(AbstractGameElement other) {
        if (invincible) {
            return false;
        }
        Shape collisionArea = Shape.intersect(this.character, other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

    /*The shoot method will allow player ship fire bullet
    * When calling this method, a projectile object will create at player ship's location, then set the movement for the projectile
    * */
    public Projectile shoot() {
        Projectile shot = new Projectile((int) this.getCharacter().getTranslateX(),
                (int) this.getCharacter().getTranslateY(), ProjectileType.PLAYER);
        shot.setSpeed(this.getSpeed());
        shot.getCharacter().setRotate(this.getCharacter().getRotate());

        return shot;
    }

    /*We add a method to boolean whether a position is safe or not
    this method will use for player ship's reset and SHIFT skill(Hyperspace jump)
    The position shouldn't have the Asteriods, Alien Ship ,or Enemy bullets.
    This can be achieved by passing in the corresponding object
    * */
    private boolean isPositionNotSafe(List<AbstractGameElement> characters, int safeDistance) {
        Point2D newPosition = this.getPosition();
        // Check for collisions with other characters
        for (AbstractGameElement character : characters){
            if (newPosition.distance(character.getPosition()) < safeDistance){
                return true;
            }
        }
        return false;
    }

    /*If the new location is safe, then we can move the player ship to this new position
    * */
    public void moveSomewhereSafe(List<AbstractGameElement> characters, int safeDistance){
        double newTranslateX, newTranslateY;
        do {
            newTranslateX = Math.random() * GameWindow.WIDTH;
            newTranslateY = Math.random() * GameWindow.HEIGHT;
            this.getCharacter().setTranslateX(newTranslateX);
            this.getCharacter().setTranslateY(newTranslateY);
        } while (this.isPositionNotSafe(characters, safeDistance));

    }

}