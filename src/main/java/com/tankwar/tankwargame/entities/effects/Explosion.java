package com.tankwar.tankwargame.entities.effects;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.util.GameConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Explosion effect with different sizes for various impact types.
 * SMALL - missile impact
 * MEDIUM - wall destruction
 * LARGE - tank destruction
 * @author Iyed Acheche
 */
public class Explosion extends GameObject {
    private int frameCount = 0;
    private final int maxFrames = GameConstants.EXPLOSION_FRAME_COUNT;
    private long lastFrameTime = 0;
    private final long frameDelay;
    private final int explosionSize;
    
    public enum Size {
        SMALL(GameConstants.EXPLOSION_SIZE_SMALL, 60),
        MEDIUM(GameConstants.EXPLOSION_SIZE_MEDIUM, 80),
        LARGE(GameConstants.EXPLOSION_SIZE_LARGE, 100);
        
        private final int pixelSize;
        private final long frameDelay;
        
        Size(int pixelSize, long frameDelay) {
            this.pixelSize = pixelSize;
            this.frameDelay = frameDelay;
        }
        
        public int getPixelSize() { return pixelSize; }
        public long getFrameDelay() { return frameDelay; }
    }
    
    public Explosion(double x, double y) {
        this(x, y, Size.MEDIUM);
    }
    
    public Explosion(double x, double y, Size size) {
        super(x, y, size.getPixelSize(), size.getPixelSize());
        this.explosionSize = size.getPixelSize();
        this.frameDelay = size.getFrameDelay();
        this.x = x - explosionSize / 2.0;
        this.y = y - explosionSize / 2.0;
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
    public void render(GraphicsContext gc) {
        if (image != null && active) {
            gc.drawImage(image, x, y, explosionSize, explosionSize);
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


