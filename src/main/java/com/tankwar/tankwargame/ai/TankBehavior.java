package com.tankwar.tankwargame.ai;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.entities.projectiles.Missile;
import com.tankwar.tankwargame.entities.tanks.Tank;
import javafx.scene.input.KeyCode;
import java.util.*;

/**
 * Strategy pattern interface for tank behaviors.
 * Allows different control schemes to be plugged in.
 * @author Iyed Acheche
 */
public interface TankBehavior {
    /**
     * Strategy method - handles tank behavior each frame.
     */
    void update(Tank tank, Set<KeyCode> pressedKeys, List<GameObject> obstacles, List<Missile> missiles);
}


