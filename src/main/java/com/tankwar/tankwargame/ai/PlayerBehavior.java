package com.tankwar.tankwargame.ai;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.entities.projectiles.Missile;
import com.tankwar.tankwargame.entities.tanks.Tank;
import com.tankwar.tankwargame.util.Direction;
import javafx.scene.input.KeyCode;
import java.util.*;

/**
 * Player behavior - handles keyboard input for player-controlled tank.
 * Arrow keys for movement, spacebar to fire.
 * @author Iyed Acheche
 */
public class PlayerBehavior implements TankBehavior {
    @Override
    public void update(Tank tank, Set<KeyCode> pressedKeys, List<GameObject> obstacles, List<Missile> missiles) {
        if (!tank.isActive()) return;
        
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
        
        if (pressedKeys.contains(KeyCode.SPACE)) {
            Missile missile = tank.fire();
            if (missile != null) {
                missiles.add(missile);
            }
        }
    }
}

