package com.tankwar.tankwargame.factory;

import com.tankwar.tankwargame.entities.effects.Explosion;
import com.tankwar.tankwargame.entities.pickups.MedPack;
import com.tankwar.tankwargame.entities.tanks.*;
import com.tankwar.tankwargame.util.Direction;

/**
 * Factory for creating game objects (Factory + Singleton patterns).
 * @author Iyed Acheche
 */
public class GameObjectFactory {
    private static GameObjectFactory instance;
    
    private GameObjectFactory() {}
    
    public static GameObjectFactory getInstance() {
        if (instance == null) {
            instance = new GameObjectFactory();
        }
        return instance;
    }
    
    public Tank createTank(double x, double y, Direction direction, TankType type) {
        switch (type) {
            case PLAYER:
                return new PlayerTank(x, y, direction);
            case ENEMY:
                return new EnemyTank(x, y, direction);
            default:
                throw new IllegalArgumentException("Unknown tank type: " + type);
        }
    }
    
    public MedPack createMedPack(double x, double y) {
        return new MedPack(x, y);
    }
    
    public Explosion createSmallExplosion(double x, double y) {
        return new Explosion(x, y, Explosion.Size.SMALL);
    }
    
    public Explosion createMediumExplosion(double x, double y) {
        return new Explosion(x, y, Explosion.Size.MEDIUM);
    }
    
    public Explosion createLargeExplosion(double x, double y) {
        return new Explosion(x, y, Explosion.Size.LARGE);
    }
    
    public enum TankType {
        PLAYER, ENEMY
    }
}

