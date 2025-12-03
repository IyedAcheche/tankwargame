package com.tankwar.tankwargame.entities.pickups;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.entities.tanks.Tank;
import com.tankwar.tankwargame.util.GameConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Medical pack class that heals tanks on contact.
 * Restores full health when collected.
 * @author Iyed Acheche
 */
public class MedPack extends GameObject {
    private int healAmount = GameConstants.MEDPACK_HEAL_AMOUNT;
    
    public MedPack(double x, double y) {
        super(x, y, GameConstants.MEDPACK_SIZE, GameConstants.MEDPACK_SIZE);
    }
    
    @Override
    public void update() {
        // No update needed for static medpacks
    }
    
    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(x, y, width, height);
        gc.setFill(Color.WHITE);
        gc.fillRect(x + 5, y + 12, 20, 6);
        gc.fillRect(x + 12, y + 5, 6, 20);
    }
    
    /**
     * Check if tank collected this medpack.
     * Restores full health when collected.
     */
    public boolean collectBy(Tank tank) {
        if (intersects(tank) && active) {
            tank.heal(tank.getMaxHealth());
            active = false;
            return true;
        }
        return false;
    }
    
    public int getHealAmount() { return healAmount; }
}


