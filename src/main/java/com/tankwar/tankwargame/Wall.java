package com.tankwar.tankwargame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Wall obstacle class that extends GameObject.
 * Static objects that block movement and missiles.
 * @author Iyed Acheche
 */
public class Wall extends GameObject {
    
    public Wall(double x, double y) {
        super(x, y, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
    }
    
    // Walls don't need to update (static objects)
    @Override
    public void update() {
        // No update needed for static walls
    }
    
    // Override parent render method to draw rectangle instead of image
    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.GRAY);
        gc.fillRect(x, y, width, height);
        gc.setStroke(Color.DARKGRAY);
        gc.strokeRect(x, y, width, height);
    }
}

