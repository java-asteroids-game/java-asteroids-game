package com.example.javaproject;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;

public class UIManager {
    private GameEngine gameEngine;

    public UIManager(GameEngine gameEngine){
        this.gameEngine = gameEngine;
    }

    public List<Text> setupGameTextElements(){
        // Show current points ,current level, and current HP
        Text pointsText = new Text(30, 40, "Points: 0");
        Text levelText = new Text(30, 80, "Level: 1");
        Text livesText = new Text(850, 40, "Lives: 3");
        Text overheatText = new Text(30, 140, "Overheat");
        List<Text> textElements = Arrays.asList(pointsText, levelText, livesText, overheatText);

        textElements.forEach(textElement -> {
            textElement.setFill(Color.WHITE);
            textElement.setStyle("-fx-font: 20 consolas;");
            pane.getChildren().add(textElement);
        });

        return textElements;
    }
}
