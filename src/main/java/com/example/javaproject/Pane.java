package com.example.javaproject;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Pane {

    public static final double WIDTH = 960;
    public static final double HEIGHT = 600;

    private static Pane instance = new Pane();
    private Stage stage;
    private GameWindow game = new GameWindow();
//    private ScoreList scorelist = new ScoreList();

    private Pane(){}

    public static Pane getInstance(){
        return instance;
    }

    public void scene(Stage stage){
        AnchorPane root = new AnchorPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("Asteroid");
        stage.setResizable(false);
        stage.setScene(scene);
        this.stage = stage;
        toIndex();
        stage.show();
    }

    public void toIndex() {
        Index.load(stage);
    }

    public void gameStart() {
        game.load(stage,3);
    }

//    public void scorelist() {
//        scorelist.load(stage);
//    }
}
