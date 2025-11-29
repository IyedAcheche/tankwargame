package com.tankwar.tankwargame;

import javafx.scene.input.KeyCode;
import java.util.*;

/**
 * Player-controlled tank class.
 * Uses Strategy pattern for behavior and inherits from Tank.
 * @author Iyed Acheche
 */
public class PlayerTank extends Tank {
    private TankBehavior behavior; // Strategy pattern - can change behavior
    
    public PlayerTank(double x, double y, Direction direction) {
        super(x, y, direction, true); // Call parent constructor
        this.behavior = new PlayerBehavior(); // Default to player behavior
    }
    
    // Uses Strategy pattern to handle input
    public void updateWithBehavior(Set<KeyCode> pressedKeys, List<GameObject> obstacles, List<Missile> missiles) {
        behavior.update(this, pressedKeys, obstacles, missiles);
    }
    
    // Allows changing behavior at runtime (Strategy pattern)
    public void setBehavior(TankBehavior behavior) {
        this.behavior = behavior;
    }
}

