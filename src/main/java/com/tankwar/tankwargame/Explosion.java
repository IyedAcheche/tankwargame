package com.tankwar.tankwargame;

import javafx.scene.image.Image;

public class Explosion extends GameObject {
    private int frameCount = 0;
    private final int maxFrames = GameConstants.EXPLOSION_FRAME_COUNT;
    private long lastFrameTime = 0;
    private final long frameDelay = GameConstants.EXPLOSION_FRAME_DELAY;
    
    public Explosion(double x, double y) {
        super(x, y, GameConstants.TANK_SIZE, GameConstants.TANK_SIZE);
        loadImage();
    }
    
    private void loadImage() {
        try {
            image = new Image(getClass().getResourceAsStream("/com/tankwar/tankwargame/images/" + frameCount + ".gif"));
        } catch (Exception e) {
            System.err.println("Could not load explosion image: " + frameCount + ".gif");
        }
    }
    
    @Override
    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDelay) {
            frameCount++;
            if (frameCount >= maxFrames) {
                active = false;
            } else {
                loadImage();
            }
            lastFrameTime = currentTime;
        }
    }
}

