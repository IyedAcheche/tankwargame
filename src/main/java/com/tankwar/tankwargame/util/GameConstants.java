package com.tankwar.tankwargame.util;

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
    public static final int GOLDEN_APPLE_SIZE = 35;
    
    // Health and damage values
    public static final int PLAYER_MAX_HEALTH = 100;
    public static final int ENEMY_MAX_HEALTH = 50;
    public static final int MISSILE_DAMAGE = 25;           // Player missile damage
    public static final int ENEMY_MISSILE_DAMAGE = 15;     // Enemy missile damage (lower)
    public static final int MEDPACK_HEAL_AMOUNT = 50;
    
    // Wall health values
    public static final int DESTRUCTIBLE_WALL_HEALTH = 50;
    public static final int WEAK_WALL_HEALTH = 25;
    public static final int STRONG_WALL_HEALTH = 75;
    
    // Movement speeds
    public static final double TANK_SPEED = 3.0;
    public static final double ENEMY_TANK_SPEED = 1.5; // Slower than player for easier gameplay
    public static final double MISSILE_SPEED = 4.0;
    
    // Timing constants
    public static final long SHOT_COOLDOWN = 400;        // Player shot cooldown (ms)
    public static final long ENEMY_SHOT_COOLDOWN = 800;  // Enemy shot cooldown - slower firing
    public static final long EXPLOSION_FRAME_DELAY = 100;
    
    // Game object counts
    public static final int ENEMY_TANK_COUNT = 6;
    public static final int MEDPACK_COUNT = 3;
    public static final int EXPLOSION_FRAME_COUNT = 10;
    
    // AI behavior probabilities (out of 100)
    public static final int ENEMY_MOVE_CHANCE = 5;
    public static final int ENEMY_SHOOT_CHANCE = 1;
    
    // AI behavior constants
    public static final double PLAYER_CHASE_RADIUS = 150.0;
    public static final double APPLE_DANGER_RADIUS = 60.0;
    public static final double AI_DECISION_INTERVAL = 50;
    
    // Explosion sizes
    public static final int EXPLOSION_SIZE_SMALL = 20;
    public static final int EXPLOSION_SIZE_MEDIUM = 35;
    public static final int EXPLOSION_SIZE_LARGE = 50;
    
    // Game progression values
    public static final int INITIAL_LIVES = 3;
    public static final int LEVEL_BONUS_SCORE = 1000;
    public static final int WIN_BONUS_SCORE = 5000;
    
    // UI Constants
    public static final int TOP_BAR_HEIGHT = 45;
    public static final int WINDOW_WIDTH = GAME_WIDTH;
    public static final int WINDOW_HEIGHT = GAME_HEIGHT + TOP_BAR_HEIGHT;
    
    // Prevent instantiation
    private GameConstants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}


