package asteroids;
import javafx.scene.paint.Color;
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
    
    // OR
    public class Projectile extends java.lang.Character {

    public int despawn_timer;
    public long spawn_time = System.currentTimeMillis();

    public Projectile(int x, int y) {
        super(new Polygon(2, -2, 2, 2, -2, 2, -2, -2), x, y);
        this.despawn_timer = 300;
        this.getCharacter().setFill(Color.BLACK);
    }
    public void timeDown() {
        this.despawn_timer -= 1;
    }
}

