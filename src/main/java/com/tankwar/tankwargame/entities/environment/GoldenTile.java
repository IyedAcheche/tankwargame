package com.tankwar.tankwargame.entities.environment;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.util.GameConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Golden tile for player spawn area.
 * Provides a special visual indicator for the player starting position.
 * @author Iyed Acheche
 */
public class GoldenTile extends GameObject {
    
    public GoldenTile(double x, double y) {
        super(x, y, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
    }
    
    @Override
    public void update() {
        // Golden tiles don't need updates - they're static
    }
    
    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.GOLD);
        gc.fillRect(x, y, width, height);
        
        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);
    }
}


