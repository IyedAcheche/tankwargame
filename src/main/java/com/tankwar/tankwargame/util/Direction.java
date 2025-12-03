package com.tankwar.tankwargame.util;

/**
 * Direction enum for movement and orientation.
 * Contains movement vectors and image file names for each direction.
 * @author Iyed Acheche
 */
public enum Direction {
    UP(0, -1, "tankU.gif"),
    DOWN(0, 1, "tankD.gif"),
    LEFT(-1, 0, "tankL.gif"),
    RIGHT(1, 0, "tankR.gif");
    
    private final int dx;
    private final int dy;
    private final String tankImage;
    
    Direction(int dx, int dy, String tankImage) {
        this.dx = dx;
        this.dy = dy;
        this.tankImage = tankImage;
    }
    
    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public String getTankImage() { return tankImage; }
    
    public String getMissileImage() {
        switch(this) {
            case UP: return "missileU.gif";
            case DOWN: return "MissileD.gif";
            case LEFT: return "missileL.gif";
            case RIGHT: return "missileR.gif";
            default: return "missileU.gif";
        }
    }
    
    public Direction getClockwise() {
        switch(this) {
            case UP: return RIGHT;
            case RIGHT: return DOWN;
            case DOWN: return LEFT;
            case LEFT: return UP;
            default: return this;
        }
    }
    
    public Direction getCounterClockwise() {
        switch(this) {
            case UP: return LEFT;
            case LEFT: return DOWN;
            case DOWN: return RIGHT;
            case RIGHT: return UP;
            default: return this;
        }
    }
    
    public Direction getOpposite() {
        switch(this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: return this;
        }
    }
}

