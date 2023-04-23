package com.example.javaproject;

import javafx.geometry.Point2D;

import static java.lang.System.currentTimeMillis;

public class EnemyShip extends AbstractGameElement implements Ship{

    private long createdAt = currentTimeMillis();
    public double speed;
    private boolean alive = false;
    private final PlayerShip target;

    //creates a green polygon
    public EnemyShip(int x, int y, PlayerShip playerShip) {
        super(new CharacterFactory().createEnemyShip(), x, y);
        this.character.setFill(javafx.scene.paint.Color.GREEN);
        this.target = playerShip;
    }

    //getter method for the created time variable
    public long getCreatedTime() {
        return createdAt;
    }

    //sets new value for created time value
    public void setCreatedTime(long newValue) {
        createdAt = newValue;

    }

    /*
     * Since the alien ship needs to be able to move randomly, we set two times to monitor the length of the alien ship's movement in one direction,
     * and change its direction of movement by random values when it exceeds a certain value
     */
    public void move() {
        long currentTime = currentTimeMillis();
        long createdTime = this.getCreatedTime();

        // Change direction every 4 seconds
        if (currentTime - createdTime > 4000) {
            double angle = Math.random() * 2 * Math.PI;
            double changeX = Math.cos(angle) * (Math.random() * 2 );
            double changeY = Math.sin(angle) * (Math.random() * 2 );
            this.movement = new Point2D(changeX, changeY);
            setCreatedTime(currentTime);
        }

        // Move the alien ship
        this.getCharacter().setTranslateX(getCharacter().getTranslateX() + movement.getX());
        this.getCharacter().setTranslateY(getCharacter().getTranslateY() + movement.getY());

        // Methods to move the ship.
        //wrapScreen();
        bounceOffScreen();
    }

    /*
    * This method allows the alien ship to shoot at the player ship,
    * creates a bullet at the alien ship's position, calculates the angle with the player ship to determine the direction,
    * and then sets the bullet's movement
    * */
    public Projectile shoot() {
        // Set location to enemy ship location
        Projectile alienShoot = new Projectile((int) this.getCharacter().getTranslateX(), (int) this.getCharacter().getTranslateY(), ProjectileType.ALIEN);

        // Calculate angle between alien ship and target ship
        double angle = Math.toDegrees(Math.atan2(this.target.getCharacter().getTranslateY() - alienShoot.getCharacter().getTranslateY(),
                this.target.getCharacter().getTranslateX() - alienShoot.getCharacter().getTranslateX()));

        // Set the rotation of the projectile
        alienShoot.getCharacter().setRotate(angle);

        alienShoot.setSpeed(this.getSpeed());

        return alienShoot;
    }

    public boolean isAlive(){
        return this.alive;
    }

    public void setAlive(boolean alive){
        this.alive = alive;
    }
}
