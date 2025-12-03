package com.tankwar.tankwargame.events;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.entities.effects.Explosion;
import com.tankwar.tankwargame.entities.pickups.MedPack;
import com.tankwar.tankwargame.entities.projectiles.Missile;
import com.tankwar.tankwargame.entities.tanks.Tank;
import java.util.*;

/**
 * Default event handler that responds to game events.
 * @author Iyed Acheche
 */
public class GameEventHandler implements GameEventObserver {
    private List<Explosion> explosions;
    
    public GameEventHandler(List<Explosion> explosions) {
        this.explosions = explosions;
    }
    
    @Override
    public void onTankDestroyed(Tank tank) {
        explosions.add(new Explosion(tank.getX(), tank.getY()));
    }
    
    @Override
    public void onMedPackCollected(MedPack medPack, Tank tank) {
        // Medpack collected - health restored
    }
    
    @Override
    public void onMissileHit(Missile missile, GameObject target) {
        // Missile hit registered
    }
    
    @Override
    public void onGameOver(boolean playerWon) {
        // Game over state handled by GameEngine
    }
}


