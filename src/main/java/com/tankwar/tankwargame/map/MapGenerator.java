package com.tankwar.tankwargame.map;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.entities.environment.*;
import java.util.List;

/**
 * Map Generator class responsible for creating game maps/levels.
 * 
 * OOP Concepts: Single Responsibility, Encapsulation, Composition
 * Design Patterns: Factory Pattern, Builder Pattern
 * 
 * @author Iyed Acheche
 */
public class MapGenerator {
    
    private final int gameWidth;
    private final int gameHeight;
    private final int tileSize;
    private final int rows;
    private final int cols;
    
    public MapGenerator(int gameWidth, int gameHeight, int tileSize) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.tileSize = tileSize;
        this.rows = gameHeight / tileSize;
        this.cols = gameWidth / tileSize;
    }
    
    public void generateMap(List<GameObject> walls, 
                           List<DestructibleWall> destructibleWalls,
                           List<GoldenTile> goldenTiles) {
        createBorderWalls(walls);
        createMazeStructure(walls, destructibleWalls);
        createAppleDefenseRing(destructibleWalls);
        createStrategicDestructibleWalls(destructibleWalls);
        createGoldenSpawnArea(goldenTiles);
    }
    
    private void createBorderWalls(List<GameObject> walls) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (row == 0 || row == rows - 1 || col == 0 || col == cols - 1) {
                    walls.add(new Wall(col * tileSize, row * tileSize));
                }
            }
        }
    }
    
    private void createMazeStructure(List<GameObject> walls, List<DestructibleWall> destructibleWalls) {
        int centerCol = cols / 2;
        int centerRow = rows / 2;
        
        createHorizontalWalls(walls, centerCol);
        createVerticalWalls(walls, centerRow);
        createInnerObstacles(walls);
        createCornerDestructibles(destructibleWalls, centerCol, centerRow);
    }
    
    private void createHorizontalWalls(List<GameObject> walls, int centerCol) {
        for (int x = 4; x < centerCol - 2; x++) {
            walls.add(new Wall(x * tileSize, 3 * tileSize));
        }
        for (int x = centerCol + 3; x < cols - 4; x++) {
            walls.add(new Wall(x * tileSize, 3 * tileSize));
        }
        
        for (int x = 4; x < centerCol - 3; x++) {
            walls.add(new Wall(x * tileSize, (rows - 4) * tileSize));
        }
        for (int x = centerCol + 4; x < cols - 4; x++) {
            walls.add(new Wall(x * tileSize, (rows - 4) * tileSize));
        }
    }
    
    private void createVerticalWalls(List<GameObject> walls, int centerRow) {
        walls.add(new Wall(4 * tileSize, 4 * tileSize));
        walls.add(new Wall(4 * tileSize, 5 * tileSize));
        for (int y = 8; y < centerRow - 1; y++) {
            walls.add(new Wall(4 * tileSize, y * tileSize));
        }
        for (int y = centerRow + 2; y < rows - 5; y++) {
            walls.add(new Wall(4 * tileSize, y * tileSize));
        }
        
        walls.add(new Wall((cols - 5) * tileSize, 4 * tileSize));
        walls.add(new Wall((cols - 5) * tileSize, 5 * tileSize));
        for (int y = 8; y < centerRow - 1; y++) {
            walls.add(new Wall((cols - 5) * tileSize, y * tileSize));
        }
        for (int y = centerRow + 2; y < rows - 5; y++) {
            walls.add(new Wall((cols - 5) * tileSize, y * tileSize));
        }
    }
    
    private void createInnerObstacles(List<GameObject> walls) {
        walls.add(new Wall(7 * tileSize, 5 * tileSize));
        walls.add(new Wall((cols - 8) * tileSize, 5 * tileSize));
        walls.add(new Wall(7 * tileSize, (rows - 6) * tileSize));
        walls.add(new Wall((cols - 8) * tileSize, (rows - 6) * tileSize));
    }
    
    private void createCornerDestructibles(List<DestructibleWall> destructibleWalls, int centerCol, int centerRow) {
        destructibleWalls.add(new DestructibleWall(2 * tileSize, 2 * tileSize));
        destructibleWalls.add(new DestructibleWall(3 * tileSize, 2 * tileSize));
        
        destructibleWalls.add(new DestructibleWall((cols - 3) * tileSize, 2 * tileSize));
        destructibleWalls.add(new DestructibleWall((cols - 4) * tileSize, 2 * tileSize));
        
        destructibleWalls.add(new DestructibleWall(2 * tileSize, (rows - 3) * tileSize));
        destructibleWalls.add(new DestructibleWall(3 * tileSize, (rows - 3) * tileSize));
        destructibleWalls.add(new DestructibleWall(2 * tileSize, (rows - 4) * tileSize));
        destructibleWalls.add(new DestructibleWall(2 * tileSize, (rows - 5) * tileSize));
        
        destructibleWalls.add(new DestructibleWall((cols - 3) * tileSize, (rows - 3) * tileSize));
        destructibleWalls.add(new DestructibleWall((cols - 4) * tileSize, (rows - 3) * tileSize));
        destructibleWalls.add(new DestructibleWall((cols - 3) * tileSize, (rows - 4) * tileSize));
        destructibleWalls.add(new DestructibleWall((cols - 3) * tileSize, (rows - 5) * tileSize));
        
        destructibleWalls.add(new DestructibleWall((centerCol - 3) * tileSize, centerRow * tileSize));
        destructibleWalls.add(new DestructibleWall((centerCol + 3) * tileSize, centerRow * tileSize));
        destructibleWalls.add(new DestructibleWall(centerCol * tileSize, (centerRow - 3) * tileSize));
        destructibleWalls.add(new DestructibleWall(centerCol * tileSize, (centerRow + 3) * tileSize));
    }
    
    private void createAppleDefenseRing(List<DestructibleWall> destructibleWalls) {
        int centerX = gameWidth / 2;
        int centerY = gameHeight / 2;
        
        int appleX = (centerX / tileSize) * tileSize;
        int appleY = (centerY / tileSize) * tileSize;
        
        int[][] offsets = {
            {-1, -1}, {0, -1}, {1, -1},
            {-1, 0},          {1, 0},
            {-1, 1},  {0, 1}, {1, 1}
        };
        
        for (int[] offset : offsets) {
            int x = appleX + offset[0] * tileSize;
            int y = appleY + offset[1] * tileSize;
            if (isValidPosition(x, y)) {
                destructibleWalls.add(new DestructibleWall(x, y));
            }
        }
    }
    
    private void createStrategicDestructibleWalls(List<DestructibleWall> destructibleWalls) {
        int centerCol = cols / 2;
        int centerRow = rows / 2;
        
        destructibleWalls.add(new DestructibleWall(6 * tileSize, 5 * tileSize));
        destructibleWalls.add(new DestructibleWall(6 * tileSize, centerRow * tileSize));
        destructibleWalls.add(new DestructibleWall(6 * tileSize, (rows - 6) * tileSize));
        
        destructibleWalls.add(new DestructibleWall((cols - 7) * tileSize, 5 * tileSize));
        destructibleWalls.add(new DestructibleWall((cols - 7) * tileSize, centerRow * tileSize));
        destructibleWalls.add(new DestructibleWall((cols - 7) * tileSize, (rows - 6) * tileSize));
        
        destructibleWalls.add(new DestructibleWall(8 * tileSize, 2 * tileSize));
        destructibleWalls.add(new DestructibleWall((cols - 9) * tileSize, 2 * tileSize));
        
        destructibleWalls.add(new DestructibleWall(8 * tileSize, (rows - 3) * tileSize));
        destructibleWalls.add(new DestructibleWall((cols - 9) * tileSize, (rows - 3) * tileSize));
        
        destructibleWalls.add(new DestructibleWall((centerCol - 4) * tileSize, (centerRow - 2) * tileSize));
        destructibleWalls.add(new DestructibleWall((centerCol + 4) * tileSize, (centerRow - 2) * tileSize));
        destructibleWalls.add(new DestructibleWall((centerCol - 4) * tileSize, (centerRow + 2) * tileSize));
        destructibleWalls.add(new DestructibleWall((centerCol + 4) * tileSize, (centerRow + 2) * tileSize));
        
        destructibleWalls.add(new DestructibleWall(9 * tileSize, 6 * tileSize));
        destructibleWalls.add(new DestructibleWall((cols - 10) * tileSize, 6 * tileSize));
        destructibleWalls.add(new DestructibleWall(9 * tileSize, (rows - 7) * tileSize));
        destructibleWalls.add(new DestructibleWall((cols - 10) * tileSize, (rows - 7) * tileSize));
    }
    
    private void createGoldenSpawnArea(List<GoldenTile> goldenTiles) {
        int centerX = gameWidth / 2;
        int bottomY = gameHeight - 80;
        
        int tileX = (centerX / tileSize) * tileSize;
        int tileY = (bottomY / tileSize) * tileSize;
        
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int x = tileX + dx * tileSize;
                int y = tileY + dy * tileSize;
                goldenTiles.add(new GoldenTile(x, y));
            }
        }
    }
    
    private boolean isValidPosition(int x, int y) {
        return x > 0 && y > 0 && x < gameWidth - tileSize && y < gameHeight - tileSize;
    }
    
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getTileSize() { return tileSize; }
}


