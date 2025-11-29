package com.tankwar.tankwargame;

import javafx.scene.image.Image;

/**
 * Missile projectile class that extends GameObject.
 * Handles movement, collision detection, and damage.
 * @author Iyed Acheche
 */
public class Missile extends GameObject {
    private Direction direction; // Movement direction
    private double speed = GameConstants.MISSILE_SPEED; // Movement speed
    private Tank owner; // Tank that fired this missile
    private int damage = GameConstants.MISSILE_DAMAGE; // Damage dealt
    
    public Missile(double x, double y, Direction direction, Tank owner) {
        super(x, y, GameConstants.MISSILE_SIZE, GameConstants.MISSILE_SIZE);
        this.direction = direction;
        this.owner = owner;
        loadImage();
    }
    
    // Load missile sprite based on direction
    private void loadImage() {
        try {
            image = new Image(getClass().getResourceAsStream("/com/tankwar/tankwargame/images/" + direction.getMissileImage()));
        } catch (Exception e) {
            System.err.println("Could not load missile image: " + direction.getMissileImage());
        }
    }
    
    // Move missile and check boundaries
    @Override
    public void update() {
        x += direction.getDx() * speed;
        y += direction.getDy() * speed;
        
        // Destroy missile if it goes off screen
        if (x < 0 || y < 0 || x > GameConstants.GAME_WIDTH || y > GameConstants.GAME_HEIGHT) {
            active = false;
        }
    }
    
    // Check if missile hit a target and deal damage
    public boolean hitTarget(GameObject target) {
        if (target == owner || !target.isActive()) {
            return false; // Can't hit owner or inactive targets
        }
        
        if (intersects(target)) {
            if (target instanceof Tank) { // Polymorphism - check if target is Tank
                ((Tank) target).takeDamage(damage);
            }
            active = false; // Destroy missile after hit
            return true;
        }
        return false;
    }
    
    // Getters for encapsulation
    public Tank getOwner() { return owner; }
    public int getDamage() { return damage; }
}

