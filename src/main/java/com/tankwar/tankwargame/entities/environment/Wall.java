package com.tankwar.tankwargame.entities.environment;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.util.GameConstants;
import javafx.scene.image.Image;

/**
 * Indestructible wall obstacle class.
 * Static objects that block movement and missiles.
 * @author Iyed Acheche
 */
public class Wall extends GameObject {
    
    public Wall(double x, double y) {
        super(x, y, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
        loadImage();
    }
    
    private void loadImage() {
        try {
            image = new Image(getClass().getResourceAsStream("/com/tankwar/tankwargame/images/und_wall.gif"));
        } catch (Exception e) {
            System.err.println("Could not load indestructible wall image: und_wall.gif");
        }
    }
    
    @Override
    public void update() {
        // No update needed for static walls
    }
}

