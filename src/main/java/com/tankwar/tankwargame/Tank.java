package com.tankwar.tankwargame;

import javafx.scene.image.Image;
import java.util.List;

/**
 * Tank class represents both player and enemy tanks in the game.
 * Demonstrates key OOP concepts and serves as base class for specialized tanks.
 * 
 * OOP Concepts Applied:
 * - INHERITANCE: Extends GameObject to inherit common properties
 * - ENCAPSULATION: Protected fields with controlled access
 * - POLYMORPHISM: Can be extended by PlayerTank and EnemyTank
 * - ABSTRACTION: Provides tank-specific behavior while hiding implementation
 * 
 * Design Patterns:
 * - Template Method: Provides common tank behavior, subclasses can override
 * - State Management: Tracks tank health, direction, and firing cooldown
 * 
 * @author Iyed Acheche
 */
public class Tank extends GameObject {
    // ENCAPSULATION: Protected fields accessible to subclasses but not external classes
    protected Direction direction;        // Current facing direction
    protected int health;                // Current health points
    protected int maxHealth;             // Maximum health capacity
    protected long lastShotTime = 0;     // Timestamp of last missile fired
    protected final long shotCooldown = GameConstants.SHOT_COOLDOWN;  // Firing rate limit
    protected double speed = GameConstants.TANK_SPEED;  // Movement speed
    protected boolean isPlayer;          // Flag to distinguish player from enemy
    
    /**
     * Constructor initializes tank with position, direction, and type.
     * Demonstrates CONSTRUCTOR OVERLOADING and POLYMORPHISM.
     */
    public Tank(double x, double y, Direction direction, boolean isPlayer) {
        super(x, y, GameConstants.TANK_SIZE, GameConstants.TANK_SIZE);  // Call to parent constructor
        this.direction = direction;
        this.isPlayer = isPlayer;
        
        // CONDITIONAL LOGIC: Different health based on tank type
        this.maxHealth = isPlayer ? GameConstants.PLAYER_MAX_HEALTH : GameConstants.ENEMY_MAX_HEALTH;
        this.health = maxHealth;
        
        loadImage();  // Load appropriate sprite image
    }
    
    /**
     * Loads the tank image based on current direction.
     * Demonstrates RESOURCE MANAGEMENT and ERROR HANDLING.
     */
    private void loadImage() {
        try {
            // Dynamic image loading based on direction
            image = new Image(getClass().getResourceAsStream("/com/tankwar/tankwargame/images/" + direction.getTankImage()));
        } catch (Exception e) {
            System.err.println("Could not load tank image: " + direction.getTankImage());
        }
    }
    
    /**
     * POLYMORPHISM: Override abstract method from GameObject.
     * Base implementation is empty - subclasses can provide specific behavior.
     */
    @Override
    public void update() {
        // Base tank doesn't need update logic
        // Subclasses like PlayerTank or EnemyTank can override for specific behavior
    }
    
    /**
     * Handles tank movement in specified direction with collision detection.
     * Demonstrates ALGORITHM implementation and COLLISION DETECTION.
     */
    public void move(Direction newDirection, List<GameObject> obstacles) {
        // Change direction if different (tank can turn without moving)
        if (newDirection != direction) {
            direction = newDirection;
            loadImage();  // Update sprite for new direction
        }
        
        // Calculate new position based on direction and speed
        double newX = x + direction.getDx() * speed;
        double newY = y + direction.getDy() * speed;
        
        // Only move if the new position is valid (no collisions)
        if (canMoveTo(newX, newY, obstacles)) {
            x = newX;
            y = newY;
        }
    }
    
    private boolean canMoveTo(double newX, double newY, List<GameObject> obstacles) {
        if (newX < 0 || newY < 0 || newX + width > GameConstants.GAME_WIDTH || newY + height > GameConstants.GAME_HEIGHT) {
            return false;
        }
        
        for (GameObject obstacle : obstacles) {
            if (obstacle != this && obstacle.isActive()) {
                if (newX < obstacle.x + obstacle.width &&
                    newX + width > obstacle.x &&
                    newY < obstacle.y + obstacle.height &&
                    newY + height > obstacle.y) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public Missile fire() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime < shotCooldown) {
            return null;
        }
        
        lastShotTime = currentTime;
        
        double missileX = x + width / 2 - GameConstants.MISSILE_SIZE / 2;
        double missileY = y + height / 2 - GameConstants.MISSILE_SIZE / 2;
        
        return new Missile(missileX, missileY, direction, this);
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            active = false;
        }
    }
    
    public void heal(int amount) {
        health = Math.min(health + amount, maxHealth);
    }
    
    public Direction getDirection() { return direction; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isPlayer() { return isPlayer; }
    public void setDirection(Direction direction) { 
        this.direction = direction;
        loadImage();
    }
}

