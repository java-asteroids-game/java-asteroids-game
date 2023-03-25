package application;

import javafx.scene.shape.*; //implements polygon
import java.util.HashMap;
import java.util.Map;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.shape.Polygon;
import javafx.geometry.Point2D;

public class launchgame extends Application {
	
//	https://java-programming.mooc.fi/part-14/3-larger-application-asteroids asteroid
	
	public class Ship {

	    private Polygon character;
	    private Point2D movement;

	    public Ship(int x, int y) {
	        this.character = new Polygon(-5, -5, 10, 0, -5, 5);
	        this.character.setTranslateX(x);
	        this.character.setTranslateY(y);

	        this.movement = new Point2D(0, 0);
	    }

	    public Polygon getCharacter() {
	        return character;
	    }

	    public void turnLeft() {
	        this.character.setRotate(this.character.getRotate() - 5);
	    }

	    public void turnRight() {
	        this.character.setRotate(this.character.getRotate() + 5);
	    }

	    public void move() {
	        this.character.setTranslateX(this.character.getTranslateX() + this.movement.getX());
	        this.character.setTranslateY(this.character.getTranslateY() + this.movement.getY());
	    }
	    
	    public void accelerate() {
	        double changeX = Math.cos(Math.toRadians(this.character.getRotate()));
	        double changeY = Math.sin(Math.toRadians(this.character.getRotate()));

	        this.movement = this.movement.add(changeX, changeY);
	        
	        changeX *= 0.02;
	        changeY *= 0.02;
	    }
	    
	    
	}

	
    @Override
    public void start(Stage stage) throws Exception {
        Pane pane = new Pane();
        pane.setPrefSize(600, 400);

    	Ship ship = new Ship(150, 100);
//        Polygon ship = new Polygon(-5, -5, 10, 0, -5, 5);
        
//        ship.setTranslateX(300);
//        ship.setTranslateY(200);
//        ship.setRotate(30);

    	pane.getChildren().add(ship.getCharacter());
//        pane.getChildren().add(ship);

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        
        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();

        scene.setOnKeyPressed(event -> {
            pressedKeys.put(event.getCode(), Boolean.TRUE);
        });

        scene.setOnKeyReleased(event -> {
            pressedKeys.put(event.getCode(), Boolean.FALSE);
        });
        
        Point2D movement = new Point2D(1, 0);

        new AnimationTimer() {

            @Override
            public void handle(long now) {
                if(pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnLeft();
                }

                if(pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnRight();
                }

                if(pressedKeys.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }

                ship.move();
            }
        }.start();

        
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    
}
