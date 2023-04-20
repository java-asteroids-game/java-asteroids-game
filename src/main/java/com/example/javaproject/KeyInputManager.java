package com.example.javaproject;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashMap;
import java.util.Map;

public class KeyInputManager {
    public BaseGame game;

    private boolean isGameWindow(){
        return game instanceof GameWindow;
    }

    Map<KeyCode, Boolean> pressedKeys = new HashMap<>();

    public KeyInputManager(GameWindow game) {
        this.game = game;
    }

    public void onKeyPressed(KeyEvent event) {
        pressedKeys.put(event.getCode(), Boolean.TRUE);
    }

    public void onKeyReleased(KeyEvent event) {
        pressedKeys.put(event.getCode(), Boolean.FALSE);
    }

    private void handleInput() {

        // ^ is the same as XOR
        if (pressedKeys.getOrDefault(KeyCode.A, false) ^ pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
            game.ship.turnLeft();
        }

        if (pressedKeys.getOrDefault(KeyCode.D, false) ^ pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
            game.ship.turnRight();
        }

        if (pressedKeys.getOrDefault(KeyCode.W, false) ^ pressedKeys.getOrDefault(KeyCode.UP, false)) {
            game.ship.accelerate();
        }

        if (pressedKeys.getOrDefault(KeyCode.SPACE, false)) {
            game.handleShipShooting();
        }

        if (pressedKeys.getOrDefault(KeyCode.SHIFT, false)) {
            game.handleHyperJump();
        }

        //sets 'u' key as a 'cheat' key which destroys all asteroids, sets (reduces) level to 3, displays annoying text
        //level 3 and over only, waits 250 frames, usage sets points to 0
        if (this.isGameWindow()) {
            if (pressedKeys.getOrDefault(KeyCode.U, false)) {
                game.handleCheating();
            }
        }
    }



}
