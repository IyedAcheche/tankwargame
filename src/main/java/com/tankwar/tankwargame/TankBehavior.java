package com.tankwar.tankwargame;

import javafx.scene.input.KeyCode;
import java.util.*;

/**
 * Strategy pattern interface for tank behaviors (player vs AI).
 * @author Iyed Acheche
 */
public interface TankBehavior {
    // Strategy method - different implementations for player vs AI
    void update(Tank tank, Set<KeyCode> pressedKeys, List<GameObject> obstacles, List<Missile> missiles);
}

/**
 * Player behavior - handles keyboard input
 */
class PlayerBehavior implements TankBehavior {
    @Override
    public void update(Tank tank, Set<KeyCode> pressedKeys, List<GameObject> obstacles, List<Missile> missiles) {
        if (!tank.isActive()) return;
        
        // Arrow key movement
        if (pressedKeys.contains(KeyCode.UP)) {
            tank.move(Direction.UP, obstacles);
        }
        if (pressedKeys.contains(KeyCode.DOWN)) {
            tank.move(Direction.DOWN, obstacles);
        }
        if (pressedKeys.contains(KeyCode.LEFT)) {
            tank.move(Direction.LEFT, obstacles);
        }
        if (pressedKeys.contains(KeyCode.RIGHT)) {
            tank.move(Direction.RIGHT, obstacles);
        }
        
        // Spacebar to fire
        if (pressedKeys.contains(KeyCode.SPACE)) {
            Missile missile = tank.fire();
            if (missile != null) {
                missiles.add(missile);
            }
        }
    }
}

/**
 * Enemy AI behavior - random movement and shooting
 */
class EnemyBehavior implements TankBehavior {
    private Random random = new Random();
    
    @Override
    public void update(Tank tank, Set<KeyCode> pressedKeys, List<GameObject> obstacles, List<Missile> missiles) {
        if (!tank.isActive()) return;
        
        // Random movement
        if (random.nextInt(100) < GameConstants.ENEMY_MOVE_CHANCE) {
            Direction[] directions = Direction.values();
            Direction newDir = directions[random.nextInt(directions.length)];
            tank.move(newDir, obstacles);
        }
        
        // Random shooting
        if (random.nextInt(100) < GameConstants.ENEMY_SHOOT_CHANCE) {
            Missile missile = tank.fire();
            if (missile != null) {
                missiles.add(missile);
            }
        }
    }
}

