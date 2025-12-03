package com.tankwar.tankwargame.entities.tanks;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.entities.projectiles.Missile;
import com.tankwar.tankwargame.util.Direction;
import com.tankwar.tankwargame.util.GameConstants;
import javafx.scene.image.Image;
import java.util.List;

/**
 * Tank class represents both player and enemy tanks in the game.
 * Serves as base class for PlayerTank and EnemyTank.
 * 
 * OOP Concepts: Inheritance, Encapsulation, Polymorphism, Abstraction
 * Design Patterns: Template Method, State Management
 * 
 * @author Iyed Acheche
 */
public class Tank extends GameObject {
    protected Direction direction;
    protected int health;
    protected int maxHealth;
    protected long lastShotTime = 0;
    protected long shotCooldown;
    protected double speed = GameConstants.TANK_SPEED;
    protected boolean isPlayer;
    
    public Tank(double x, double y, Direction direction, boolean isPlayer) {
        super(x, y, GameConstants.TANK_SIZE, GameConstants.TANK_SIZE);
        this.direction = direction;
        this.isPlayer = isPlayer;
        this.maxHealth = isPlayer ? GameConstants.PLAYER_MAX_HEALTH : GameConstants.ENEMY_MAX_HEALTH;
        this.health = maxHealth;
        this.shotCooldown = isPlayer ? GameConstants.SHOT_COOLDOWN : GameConstants.ENEMY_SHOT_COOLDOWN;
        loadImage();
    }
    
    private void loadImage() {
        try {
            image = new Image(getClass().getResourceAsStream("/com/tankwar/tankwargame/images/" + direction.getTankImage()));
        } catch (Exception e) {
            System.err.println("Could not load tank image: " + direction.getTankImage());
        }
    }
    
    @Override
    public void update() {
        // Base tank doesn't need update logic
    }
    
    public void move(Direction newDirection, List<GameObject> obstacles) {
        if (newDirection != direction) {
            direction = newDirection;
            loadImage();
        }
        
        double newX = x + direction.getDx() * speed;
        double newY = y + direction.getDy() * speed;
        
        if (canMoveTo(newX, newY, obstacles)) {
            x = newX;
            y = newY;
        }
    }
    
    private boolean canMoveTo(double newX, double newY, List<GameObject> obstacles) {
        if (newX < 0 || newY < 0 || newX + width > GameConstants.GAME_WIDTH || newY + height > GameConstants.GAME_HEIGHT) {
            return false;
        }
        
        final int COLLISION_BUFFER = 2;
        
        for (GameObject obstacle : obstacles) {
            if (obstacle != this && obstacle.isActive()) {
                if (newX + COLLISION_BUFFER < obstacle.getX() + obstacle.getWidth() - COLLISION_BUFFER &&
                    newX + width - COLLISION_BUFFER > obstacle.getX() + COLLISION_BUFFER &&
                    newY + COLLISION_BUFFER < obstacle.getY() + obstacle.getHeight() - COLLISION_BUFFER &&
                    newY + height - COLLISION_BUFFER > obstacle.getY() + COLLISION_BUFFER) {
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

