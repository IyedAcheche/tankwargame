package com.tankwar.tankwargame.entities.environment;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.util.GameConstants;
import javafx.scene.image.Image;

/**
 * Destructible wall that can be destroyed by missile hits.
 * 
 * OOP Concepts: Inheritance, Encapsulation, Polymorphism, State Management
 * Design Patterns: Template Method, State Pattern
 * 
 * @author Iyed Acheche
 */
public class DestructibleWall extends GameObject {
    private int health;
    private final int maxHealth;
    private boolean damaged = false;
    
    public DestructibleWall(double x, double y, int health) {
        super(x, y, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
        this.maxHealth = health;
        this.health = health;
        loadWallImage();
    }
    
    public DestructibleWall(double x, double y) {
        this(x, y, GameConstants.DESTRUCTIBLE_WALL_HEALTH);
    }
    
    private void loadWallImage() {
        try {
            image = new Image(getClass().getResourceAsStream("/com/tankwar/tankwargame/images/destructible_wall.gif"));
        } catch (Exception e) {
            System.err.println("Could not load destructible wall image: destructible_wall.gif");
        }
    }
    
    @Override
    public void update() {
        boolean shouldBeDamaged = health < maxHealth * 0.5;
        if (shouldBeDamaged != damaged) {
            damaged = shouldBeDamaged;
            loadWallImage();
        }
    }
    
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            active = false;
            onDestroy();
        }
    }
    
    private void onDestroy() {
        // Wall destruction handled
    }
    
    public void repair() {
        health = maxHealth;
        damaged = false;
        active = true;
        loadWallImage();
    }
    
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isDamaged() { return damaged; }
    public boolean isDestroyed() { return health <= 0; }
    
    public static DestructibleWall createWeakWall(double x, double y) {
        return new DestructibleWall(x, y, GameConstants.WEAK_WALL_HEALTH);
    }
    
    public static DestructibleWall createStrongWall(double x, double y) {
        return new DestructibleWall(x, y, GameConstants.STRONG_WALL_HEALTH);
    }
}


