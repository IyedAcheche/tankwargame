package com.tankwar.tankwargame.entities.projectiles;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.entities.environment.DestructibleWall;
import com.tankwar.tankwargame.entities.tanks.Tank;
import com.tankwar.tankwargame.util.Direction;
import com.tankwar.tankwargame.util.GameConstants;
import javafx.scene.image.Image;

/**
 * Missile projectile class.
 * Handles movement, collision detection, and damage.
 * @author Iyed Acheche
 */
public class Missile extends GameObject {
    private Direction direction;
    private double speed = GameConstants.MISSILE_SPEED;
    private Tank owner;
    private int damage;
    
    public Missile(double x, double y, Direction direction, Tank owner) {
        super(x, y, GameConstants.MISSILE_SIZE, GameConstants.MISSILE_SIZE);
        this.direction = direction;
        this.owner = owner;
        this.damage = owner.isPlayer() ? GameConstants.MISSILE_DAMAGE : GameConstants.ENEMY_MISSILE_DAMAGE;
        loadImage();
    }
    
    private void loadImage() {
        try {
            image = new Image(getClass().getResourceAsStream("/com/tankwar/tankwargame/images/" + direction.getMissileImage()));
        } catch (Exception e) {
            System.err.println("Could not load missile image: " + direction.getMissileImage());
        }
    }
    
    @Override
    public void update() {
        x += direction.getDx() * speed;
        y += direction.getDy() * speed;
        
        if (x < 0 || y < 0 || x > GameConstants.GAME_WIDTH || y > GameConstants.GAME_HEIGHT) {
            active = false;
        }
    }
    
    public boolean hitTarget(GameObject target) {
        if (target == owner || !target.isActive()) {
            return false;
        }
        
        // Prevent friendly fire
        if (target instanceof Tank && owner instanceof Tank) {
            Tank targetTank = (Tank) target;
            Tank ownerTank = (Tank) owner;
            
            if (!targetTank.isPlayer() && !ownerTank.isPlayer()) {
                return false;
            }
        }
        
        if (intersects(target)) {
            if (target instanceof Tank) { 
                ((Tank) target).takeDamage(damage);
            } else if (target instanceof DestructibleWall) {
                ((DestructibleWall) target).takeDamage(damage);
            }
            
            active = false;
            return true;
        }
        return false;
    }
    
    public boolean hitWall(GameObject wall) {
        if (intersects(wall)) {
            if (wall instanceof DestructibleWall) {
                ((DestructibleWall) wall).takeDamage(damage);
            }
            active = false;
            return true;
        }
        return false;
    }
    
    public Tank getOwner() { return owner; }
    public int getDamage() { return damage; }
}


