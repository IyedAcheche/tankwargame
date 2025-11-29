package com.tankwar.tankwargame;

/**
 * Factory for creating game objects (Factory + Singleton patterns).
 * @author Iyed Acheche
 */
public class GameObjectFactory {
    // Singleton instance
    private static GameObjectFactory instance;
    
    // Private constructor for Singleton
    private GameObjectFactory() {}
    
    // Get singleton instance
    public static GameObjectFactory getInstance() {
        if (instance == null) {
            instance = new GameObjectFactory();
        }
        return instance;
    }
    
    // Factory method for creating tanks
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
    
    // Factory methods for other objects
    public Missile createMissile(double x, double y, Direction direction, Tank owner) {
        return new Missile(x, y, direction, owner);
    }
    
    public Wall createWall(double x, double y) {
        return new Wall(x, y);
    }
    
    public MedPack createMedPack(double x, double y) {
        return new MedPack(x, y);
    }
    
    public Explosion createExplosion(double x, double y) {
        return new Explosion(x, y);
    }
    
    // Enum for tank types
    public enum TankType {
        PLAYER, ENEMY
    }
}

