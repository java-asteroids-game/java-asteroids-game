package com.example.javaproject;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Projectile extends Character {

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
