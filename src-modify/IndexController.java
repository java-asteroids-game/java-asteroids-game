package com.example.javaproject;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class IndexController {

    @FXML
    private Label startgame;

    @FXML
    private Label highscore;

    @FXML
    void mouseClickScore(MouseEvent event) {
//        Pane.getInstance().scorelist();
    }

    @FXML
    void mouseClickStart(MouseEvent event) {
        Pane.getInstance().gameStart();
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
