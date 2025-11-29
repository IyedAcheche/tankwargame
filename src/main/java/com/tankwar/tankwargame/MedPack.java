package com.tankwar.tankwargame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Medical pack class that heals tanks on contact.
 * Extends GameObject and provides healing functionality.
 * @author Iyed Acheche
 */
public class MedPack extends GameObject {
    private int healAmount = GameConstants.MEDPACK_HEAL_AMOUNT; // Amount to heal
    
    public MedPack(double x, double y) {
        super(x, y, GameConstants.MEDPACK_SIZE, GameConstants.MEDPACK_SIZE);
    }
    
    // MedPacks are static objects
    @Override
    public void update() {
        // No update needed for static medpacks
    }
    
    // Draw red cross symbol
    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(x, y, width, height);
        gc.setFill(Color.WHITE);
        gc.fillRect(x + 5, y + 12, 20, 6);  // Horizontal line
        gc.fillRect(x + 12, y + 5, 6, 20);  // Vertical line
    }
    
    // Check if tank collected this medpack
    public boolean collectBy(Tank tank) {
        if (intersects(tank) && active) {
            tank.heal(healAmount); // Heal the tank
            active = false; // Remove medpack after use
            return true;
        }
        return false;
    }
    
    // Getter for encapsulation
    public int getHealAmount() { return healAmount; }
}

