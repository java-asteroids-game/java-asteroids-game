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
    private Label controls;

    @FXML
    void mouseClickStart(MouseEvent event) {
        StartPane.getInstance().gameStart();
    }
    @FXML
    void mouseClickScore(MouseEvent event) {
        Stage stage = new Stage(); // Create a new Stage instance
        StartPane.getInstance().scorelist(stage); // Pass the Stage instance to the scorelist() method

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


    @FXML
    void mouseEnterControls(MouseEvent event) {
        controls.setOpacity(0.7);
    }

    @FXML
    void mouseExitControls(MouseEvent event) {
        controls.setOpacity(1);
    }

    @FXML
    void mouseClickControls(MouseEvent event) {
        Stage stage1 = new Stage(); // Create a new Stage instance
        StartPane.getInstance().showControls(stage1); // Pass the Stage instance to the showControls() method
    }

    public void getStartScreen(){

    }
}
