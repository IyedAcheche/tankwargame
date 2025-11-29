package com.tankwar.tankwargame;

import javafx.scene.input.KeyCode;
import java.util.*;

/**
 * AI-controlled enemy tank class.
 * Uses Strategy pattern for AI behavior and inherits from Tank.
 * @author Iyed Acheche
 */
public class EnemyTank extends Tank {
    private TankBehavior behavior; // Strategy pattern - AI behavior
    
    public EnemyTank(double x, double y, Direction direction) {
        super(x, y, direction, false); // Call parent constructor (not player)
        this.behavior = new EnemyBehavior(); // Default to AI behavior
    }
    
    // Uses Strategy pattern for AI decisions
    public void updateWithBehavior(Set<KeyCode> pressedKeys, List<GameObject> obstacles, List<Missile> missiles) {
        behavior.update(this, pressedKeys, obstacles, missiles);
    }
    
    // Allows changing AI behavior (Strategy pattern)
    public void setBehavior(TankBehavior behavior) {
        this.behavior = behavior;
    }
}

