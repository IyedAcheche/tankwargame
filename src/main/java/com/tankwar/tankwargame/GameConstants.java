package com.tankwar.tankwargame;

/**
 * Game configuration constants.
 * Final class that cannot be instantiated or extended.
 * @author Iyed Acheche
 */
public final class GameConstants {
    
    // Game window dimensions
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;
    public static final int TILE_SIZE = 40;
    
    // Object sizes
    public static final int TANK_SIZE = 40;
    public static final int MISSILE_SIZE = 10;
    public static final int MEDPACK_SIZE = 30;
    
    // Health and damage values
    public static final int PLAYER_MAX_HEALTH = 100;
    public static final int ENEMY_MAX_HEALTH = 50;
    public static final int MISSILE_DAMAGE = 25;
    public static final int MEDPACK_HEAL_AMOUNT = 50;
    
    // Movement speeds
    public static final double TANK_SPEED = 2.0;
    public static final double MISSILE_SPEED = 4.0;
    
    // Timing constants
    public static final long SHOT_COOLDOWN = 500; // Milliseconds between shots
    public static final long EXPLOSION_FRAME_DELAY = 100;
    
    // Game object counts
    public static final int ENEMY_TANK_COUNT = 6;
    public static final int MEDPACK_COUNT = 3;
    public static final int EXPLOSION_FRAME_COUNT = 10;
    
    // AI behavior probabilities (out of 100)
    public static final int ENEMY_MOVE_CHANCE = 3;
    public static final int ENEMY_SHOOT_CHANCE = 2;
    
    // Game progression values
    public static final int INITIAL_LIVES = 3;
    public static final int LEVEL_BONUS_SCORE = 1000;
    public static final int WIN_BONUS_SCORE = 5000;
    
    // Prevent instantiation
    private GameConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}

