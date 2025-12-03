package com.tankwar.tankwargame.entities.environment;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.entities.tanks.Tank;
import com.tankwar.tankwargame.util.GameConstants;
import javafx.scene.image.Image;

/**
 * Golden Apple - the objective that must be protected from enemy tanks.
 * 
 * OOP Concepts: Inheritance, Encapsulation, Polymorphism
 * Design Patterns: Observer (potential), State
 * 
 * @author Iyed Acheche
 */
public class GoldenApple extends GameObject {
    private boolean underThreat = false;
    private long lastThreatTime = 0;
    private final long threatCooldown = 1000;
    
    public GoldenApple(double x, double y) {
        super(x, y, GameConstants.GOLDEN_APPLE_SIZE, GameConstants.GOLDEN_APPLE_SIZE);
        loadAppleImage();
    }
    
    private void loadAppleImage() {
        try {
            image = new Image(getClass().getResourceAsStream("/com/tankwar/tankwargame/images/golden_apple.jpeg"));
        } catch (Exception e) {
            System.err.println("Could not load golden apple image: " + e.getMessage());
        }
    }
    
    @Override
    public void update() {
        if (underThreat && System.currentTimeMillis() - lastThreatTime > threatCooldown) {
            underThreat = false;
        }
    }
    
    public void setUnderThreat(boolean threat) {
        if (threat && !underThreat) {
            underThreat = true;
            lastThreatTime = System.currentTimeMillis();
            onThreatDetected();
        }
    }
    
    public boolean isContactedBy(GameObject other) {
        if (other instanceof Tank && !((Tank) other).isPlayer()) {
            onDestroy();
            return true;
        }
        return false;
    }
    
    private void onThreatDetected() {
        // Could trigger warning effects
    }
    
    private void onDestroy() {
        active = false;
    }
    
    public boolean isInDangerZone(Tank enemyTank) {
        if (enemyTank.isPlayer() || !enemyTank.isActive()) {
            return false;
        }
        
        double distance = Math.sqrt(
            Math.pow(x - enemyTank.getX(), 2) + 
            Math.pow(y - enemyTank.getY(), 2)
        );
        
        return distance < GameConstants.APPLE_DANGER_RADIUS;
    }
    
    public boolean isUnderThreat() { return underThreat; }
    public boolean isDestroyed() { return !active; }
    
    public double getCenterX() {
        return x + width / 2.0;
    }
    
    public double getCenterY() {
        return y + height / 2.0;
    }
}


