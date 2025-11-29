package com.tankwar.tankwargame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import java.util.*;
import java.util.stream.Collectors;

public class GameEngine {
    private final int gameWidth, gameHeight, tileSize;
    private Tank playerTank;
    private List<Tank> enemyTanks;
    private List<GameObject> walls;
    private List<Missile> missiles;
    private List<MedPack> medPacks;
    private List<Explosion> explosions;
    private List<GameObject> allObjects;
    private Random random;
    
    public GameEngine(int gameWidth, int gameHeight, int tileSize) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.tileSize = tileSize;
        this.random = new Random();
        
        initializeGame();
    }
    
    private void initializeGame() {
        walls = new ArrayList<>();
        missiles = new ArrayList<>();
        medPacks = new ArrayList<>();
        explosions = new ArrayList<>();
        enemyTanks = new ArrayList<>();
        allObjects = new ArrayList<>();
        
        createWalls();
        createPlayerTank();
        createEnemyTanks();
        createMedPacks();
        
        updateAllObjects();
    }
    
    private void createWalls() {
        for (int row = 0; row < gameHeight / tileSize; row++) {
            for (int col = 0; col < gameWidth / tileSize; col++) {
                if ((row == 0 || row == gameHeight / tileSize - 1 || 
                     col == 0 || col == gameWidth / tileSize - 1) ||
                    (row == 5 && col >= 3 && col <= 7) ||
                    (row == 10 && col >= 10 && col <= 14) ||
                    (col == 8 && row >= 2 && row <= 6) ||
                    (col == 12 && row >= 8 && row <= 12)) {
                    walls.add(new Wall(col * tileSize, row * tileSize));
                }
            }
        }
    }
    
    private void createPlayerTank() {
        playerTank = new Tank(80, gameHeight - 120, Direction.UP, true);
    }
    
    private void createEnemyTanks() {
        int[] startX = {80, 200, 320, 440, 560, 680};
        for (int i = 0; i < GameConstants.ENEMY_TANK_COUNT; i++) {
            Tank enemy = new Tank(startX[i], 80, Direction.DOWN, false);
            enemyTanks.add(enemy);
        }
    }
    
    private void createMedPacks() {
        for (int i = 0; i < GameConstants.MEDPACK_COUNT; i++) {
            double x, y;
            do {
                x = random.nextInt(gameWidth / tileSize) * tileSize;
                y = random.nextInt(gameHeight / tileSize) * tileSize;
            } while (isPositionOccupied(x, y));
            
            medPacks.add(new MedPack(x, y));
        }
    }
    
    private boolean isPositionOccupied(double x, double y) {
        for (GameObject wall : walls) {
            if (wall.getX() == x && wall.getY() == y) return true;
        }
        if (playerTank.getX() == x && playerTank.getY() == y) return true;
        for (Tank enemy : enemyTanks) {
            if (enemy.getX() == x && enemy.getY() == y) return true;
        }
        return false;
    }
    
    public void update(Set<KeyCode> pressedKeys) {
        handlePlayerInput(pressedKeys);
        updateEnemyTanks();
        updateMissiles();
        updateExplosions();
        checkCollisions();
        checkMedPackCollections();
        updateAllObjects();
    }
    
    private void handlePlayerInput(Set<KeyCode> pressedKeys) {
        if (!playerTank.isActive()) return;
        
        if (pressedKeys.contains(KeyCode.UP)) {
            playerTank.move(Direction.UP, allObjects);
        }
        if (pressedKeys.contains(KeyCode.DOWN)) {
            playerTank.move(Direction.DOWN, allObjects);
        }
        if (pressedKeys.contains(KeyCode.LEFT)) {
            playerTank.move(Direction.LEFT, allObjects);
        }
        if (pressedKeys.contains(KeyCode.RIGHT)) {
            playerTank.move(Direction.RIGHT, allObjects);
        }
        if (pressedKeys.contains(KeyCode.SPACE)) {
            Missile missile = playerTank.fire();
            if (missile != null) {
                missiles.add(missile);
            }
        }
    }
    
    private void updateEnemyTanks() {
        for (Tank enemy : enemyTanks) {
            if (!enemy.isActive()) continue;
            
            if (random.nextInt(100) < GameConstants.ENEMY_MOVE_CHANCE) {
                Direction[] directions = Direction.values();
                Direction newDir = directions[random.nextInt(directions.length)];
                enemy.move(newDir, allObjects);
            }
            
            if (random.nextInt(100) < GameConstants.ENEMY_SHOOT_CHANCE) {
                Missile missile = enemy.fire();
                if (missile != null) {
                    missiles.add(missile);
                }
            }
        }
    }
    
    private void updateMissiles() {
        missiles.forEach(GameObject::update);
        missiles.removeIf(missile -> !missile.isActive());
    }
    
    private void updateExplosions() {
        explosions.forEach(GameObject::update);
        explosions.removeIf(explosion -> !explosion.isActive());
    }
    
    private void checkCollisions() {
        for (Missile missile : new ArrayList<>(missiles)) {
            if (!missile.isActive()) continue;
            
            for (GameObject wall : walls) {
                if (missile.intersects(wall)) {
                    missile.setActive(false);
                    break;
                }
            }
            
            if (missile.hitTarget(playerTank) && playerTank.isActive()) {
                if (!playerTank.isActive()) {
                    explosions.add(new Explosion(playerTank.getX(), playerTank.getY()));
                }
            }
            
            for (Tank enemy : enemyTanks) {
                if (missile.hitTarget(enemy) && enemy.isActive()) {
                    if (!enemy.isActive()) {
                        explosions.add(new Explosion(enemy.getX(), enemy.getY()));
                    }
                }
            }
        }
    }
    
    private void checkMedPackCollections() {
        for (MedPack medPack : medPacks) {
            if (medPack.isActive()) {
                medPack.collectBy(playerTank);
                
                for (Tank enemy : enemyTanks) {
                    if (medPack.isActive()) {
                        medPack.collectBy(enemy);
                    }
                }
            }
        }
        medPacks.removeIf(medPack -> !medPack.isActive());
    }
    
    private void updateAllObjects() {
        allObjects.clear();
        allObjects.addAll(walls);
        allObjects.add(playerTank);
        allObjects.addAll(enemyTanks.stream()
                .filter(GameObject::isActive)
                .collect(Collectors.toList()));
    }
    
    public void render(GraphicsContext gc) {
        walls.forEach(wall -> wall.render(gc));
        medPacks.forEach(medPack -> medPack.render(gc));
        
        if (playerTank.isActive()) {
            playerTank.render(gc);
        }
        
        enemyTanks.stream()
                .filter(GameObject::isActive)
                .forEach(tank -> tank.render(gc));
        
        missiles.forEach(missile -> missile.render(gc));
        explosions.forEach(explosion -> explosion.render(gc));
    }
    
    public int getPlayerHealth() {
        return playerTank != null ? playerTank.getHealth() : 0;
    }
    
    public int getEnemyCount() {
        return (int) enemyTanks.stream().filter(GameObject::isActive).count();
    }
    
    public boolean isGameOver() {
        return !playerTank.isActive() || getEnemyCount() == 0;
    }
    
    public boolean playerWon() {
        return playerTank.isActive() && getEnemyCount() == 0;
    }
}

