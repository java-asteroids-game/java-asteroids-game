package asteroids;

import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Alien extends Character {

    private final static double SPEED = 2.0;
    private final static long MAX_FLIGHT_TIME = 5000; // 5 seconds
    private static final int MAX_MOVE_TIME = 50; // maximum time between random direction changes
    private int moveTimer; // timer for random direction changes


    private Random random = new Random();
    public Alien(int x, int y) {
        super(createPolygon(), x, y);
        this.maxFlightTime = MAX_FLIGHT_TIME;
        this.startTime = System.currentTimeMillis();
        moveTimer = MAX_MOVE_TIME;
    }

    private static Polygon createPolygon() {
        Polygon polygon = new Polygon(-15, 0, -10, -10, 10, -10, 15, 0, 10, 10, -10, 10);
        polygon.setFill(Color.BLUE);
        return polygon;
    }

    @Override
    public void move() {
    	super.move();
        double changeX = (random.nextDouble() * 3 - 1) * 0.3;
        double changeY = (random.nextDouble() * 3 - 1) * 0.3;

        Point2D newMovement = movement.add(changeX, changeY);
        
        if (newMovement.magnitude() < 5) {
            movement = newMovement;
            setMovement(getMovement().multiply(0.7));
        }
        

    }
    
}
    
//    private void shoot() {

