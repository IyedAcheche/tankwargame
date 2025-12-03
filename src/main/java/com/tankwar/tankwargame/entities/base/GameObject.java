package com.tankwar.tankwargame.entities.base;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Abstract base class for all game objects.
 * Uses inheritance, encapsulation, and polymorphism.
 * @author Iyed Acheche
 */
public abstract class GameObject {
    protected double x, y;
    protected int width, height;
    protected Image image;
    protected boolean active = true;
    
    public GameObject(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public abstract void update();
    
    public void render(GraphicsContext gc) {
        if (active && image != null) {
            gc.drawImage(image, x, y, width, height);
        }
    }
    
    public boolean intersects(GameObject other) {
        return x < other.x + other.width &&
               x + width > other.x &&
               y < other.y + other.height &&
               y + height > other.y;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public void setPosition(double x, double y) { this.x = x; this.y = y; }
}

