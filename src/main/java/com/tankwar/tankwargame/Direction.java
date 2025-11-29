package com.tankwar.tankwargame;

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
    
    private final int dx; // X movement vector
    private final int dy; // Y movement vector  
    private final String tankImage; // Tank sprite filename
    
    Direction(int dx, int dy, String tankImage) {
        this.dx = dx;
        this.dy = dy;
        this.tankImage = tankImage;
    }
    
    // Getters for movement vectors and images
    public int getDx() { return dx; }
    public int getDy() { return dy; }
    public String getTankImage() { return tankImage; }
    
    // Get missile image based on direction
    public String getMissileImage() {
        switch(this) {
            case UP: return "missileU.gif";
            case DOWN: return "MissileD.gif";
            case LEFT: return "missileL.gif";
            case RIGHT: return "missileR.gif";
            default: return "missileU.gif";
        }
    }
}

