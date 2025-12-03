package com.tankwar.tankwargame.entities.tanks;

import com.tankwar.tankwargame.ai.TankBehavior;
import com.tankwar.tankwargame.ai.PlayerBehavior;
import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.entities.projectiles.Missile;
import com.tankwar.tankwargame.util.Direction;
import javafx.scene.input.KeyCode;
import java.util.*;

/**
 * Player-controlled tank class.
 * Uses Strategy pattern for input handling and inherits from Tank.
 * @author Iyed Acheche
 */
public class PlayerTank extends Tank {
    private TankBehavior behavior;
    
    public PlayerTank(double x, double y, Direction direction) {
        super(x, y, direction, true);
        this.behavior = new PlayerBehavior();
    }
    
    /**
     * Updates tank based on player keyboard input.
     * Uses Strategy pattern to delegate input handling.
     */
    public void updateWithBehavior(Set<KeyCode> pressedKeys, List<GameObject> obstacles, List<Missile> missiles) {
        behavior.update(this, pressedKeys, obstacles, missiles);
    }
}


