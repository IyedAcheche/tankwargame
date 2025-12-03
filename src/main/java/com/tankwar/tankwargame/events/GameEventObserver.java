package com.tankwar.tankwargame.events;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.entities.pickups.MedPack;
import com.tankwar.tankwargame.entities.projectiles.Missile;
import com.tankwar.tankwargame.entities.tanks.Tank;

/**
 * Observer pattern interface for game events.
 * @author Iyed Acheche
 */
public interface GameEventObserver {
    void onTankDestroyed(Tank tank);
    void onMedPackCollected(MedPack medPack, Tank tank);
    void onMissileHit(Missile missile, GameObject target);
    void onGameOver(boolean playerWon);
}


