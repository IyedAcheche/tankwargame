package com.tankwar.tankwargame.events;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.entities.pickups.MedPack;
import com.tankwar.tankwargame.entities.projectiles.Missile;
import com.tankwar.tankwargame.entities.tanks.Tank;
import java.util.*;

/**
 * Subject for Observer pattern - manages and notifies observers.
 * @author Iyed Acheche
 */
public class GameEventSubject {
    private List<GameEventObserver> observers = new ArrayList<>();
    
    public void addObserver(GameEventObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(GameEventObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyTankDestroyed(Tank tank) {
        observers.forEach(observer -> observer.onTankDestroyed(tank));
    }
    
    public void notifyMedPackCollected(MedPack medPack, Tank tank) {
        observers.forEach(observer -> observer.onMedPackCollected(medPack, tank));
    }
    
    public void notifyMissileHit(Missile missile, GameObject target) {
        observers.forEach(observer -> observer.onMissileHit(missile, target));
    }
    
    public void notifyGameOver(boolean playerWon) {
        observers.forEach(observer -> observer.onGameOver(playerWon));
    }
}

