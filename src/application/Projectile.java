package asteroids;

import javafx.scene.shape.Polygon;

public class Projectile extends Character {

	private static final int MAX_FLIGHT_TIME = 30;
    private int timeToLive;
	
	
    public Projectile(int x, int y) {
        super(new Polygon(2, -2, 2, 2, -2, 2, -2, -2), x, y);
        this.timeToLive = MAX_FLIGHT_TIME;
    }
    
    @Override
    public void move() {
        super.move();
        this.timeToLive--;
        if (this.timeToLive <= 0) {
        	  this.setAlive(false);
        }
    }

}