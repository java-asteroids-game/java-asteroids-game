package com.example.javaproject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class IndexController {

    @FXML
    private Label startgame;

    @FXML
    private Label highscore;

    @FXML
    void mouseClickStart(MouseEvent event) {
        Pane.getInstance().gameStart();
    }
    @FXML
    void mouseClickScore(MouseEvent event) {
        Stage stage = new Stage(); // Create a new Stage instance
        Pane.getInstance().scorelist(stage); // Pass the Stage instance to the scorelist() method

//        Pane.getInstance().scorelist();
    }
    @FXML
    void mouseEnterScore(MouseEvent event) {
        highscore.setOpacity(0.7);
    }

    @FXML
    void mouseEnterStart(MouseEvent event) {
        startgame.setOpacity(0.7);
    }

    @FXML
    void mouseExitScore(MouseEvent event) {
        highscore.setOpacity(1);
    }

    @FXML
    void mouseExiteStart(MouseEvent event) {
        startgame.setOpacity(1);
    }

}
