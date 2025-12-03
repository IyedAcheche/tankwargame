package com.tankwar.tankwargame.core;

import com.tankwar.tankwargame.ai.EnemyAI;
import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.entities.effects.Explosion;
import com.tankwar.tankwargame.entities.environment.*;
import com.tankwar.tankwargame.entities.pickups.MedPack;
import com.tankwar.tankwargame.entities.projectiles.Missile;
import com.tankwar.tankwargame.entities.tanks.*;
import com.tankwar.tankwargame.events.*;
import com.tankwar.tankwargame.factory.GameObjectFactory;
import com.tankwar.tankwargame.map.MapGenerator;
import com.tankwar.tankwargame.util.Direction;
import com.tankwar.tankwargame.util.GameConstants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Game Engine handles core game logic, updates, and rendering.
 * 
 * OOP Concepts: Composition, Encapsulation, Polymorphism, Abstraction
 * Design Patterns: Factory, Observer, Strategy, Delegation
 * 
 * @author Iyed Acheche
 */
public class GameEngine {
    private final int gameWidth, gameHeight, tileSize;
    private final MapGenerator mapGenerator;
    private final GameObjectFactory factory;
    private final GameEventSubject eventSubject;
    private PlayerTank playerTank;
    private List<EnemyTank> enemyTanks;
    private List<GameObject> walls;
    private List<DestructibleWall> destructibleWalls;
    private List<Missile> missiles;
    private List<MedPack> medPacks;
    private List<Explosion> explosions;
    private List<GameObject> allObjects;
    private List<GoldenTile> goldenTiles;
    private GoldenApple goldenApple;
    private Random random;
    private boolean gameOverNotified = false;
    private boolean playerCollectedApple = false;
    
    public GameEngine(int gameWidth, int gameHeight, int tileSize) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.tileSize = tileSize;
        this.random = new Random();
        this.mapGenerator = new MapGenerator(gameWidth, gameHeight, tileSize);
        this.factory = GameObjectFactory.getInstance();
        this.eventSubject = new GameEventSubject();
        
        initializeGame();
    }
    
    private void initializeGame() {
        walls = new ArrayList<>();
        destructibleWalls = new ArrayList<>();
        missiles = new ArrayList<>();
        medPacks = new ArrayList<>();
        explosions = new ArrayList<>();
        enemyTanks = new ArrayList<>();
        allObjects = new ArrayList<>();
        goldenTiles = new ArrayList<>();
        
        // Reset AI ID counter for proper coordination
        EnemyAI.resetIdCounter();
        
        eventSubject.addObserver(new GameEventHandler(explosions));
        mapGenerator.generateMap(walls, destructibleWalls, goldenTiles);
        
        createGoldenApple();
        createPlayerTank();
        createEnemyTanks();
        createMedPacks();
        
        updateAllObjects();
    }
    
    private void createGoldenApple() {
        int centerX = gameWidth / 2;
        int centerY = gameHeight / 2;
        int appleX = (centerX / tileSize) * tileSize;
        int appleY = (centerY / tileSize) * tileSize;
        goldenApple = new GoldenApple(appleX, appleY);
    }
    
    private void createPlayerTank() {
        // Fixed spawn position: bottom center of the map
        // X: center of 800px map = 400, minus half tank (20) = 380, snapped to tile = 360
        // Y: near bottom = 520 (600 - 80, snapped to tile)
        int spawnX = 360;  // Center-bottom, aligned to tile grid
        int spawnY = 520;  // Near bottom of map
        
        playerTank = (PlayerTank) factory.createTank(spawnX, spawnY, Direction.UP, GameObjectFactory.TankType.PLAYER);
    }
    
    private void createEnemyTanks() {
        int[][] enemySpawnPositions = {
            {80, 120},
            {gameWidth - 120, 120},
            {80, gameHeight / 2 - 60},
            {80, gameHeight / 2 + 40},           // Moved up from +80 to +40
            {gameWidth - 120, gameHeight / 2 - 60},
            {gameWidth - 120, gameHeight / 2 + 40}  // Moved up from +80 to +40
        };
        
        for (int i = 0; i < enemySpawnPositions.length && i < GameConstants.ENEMY_TANK_COUNT; i++) {
            int x = enemySpawnPositions[i][0];
            int y = enemySpawnPositions[i][1];
            
            EnemyTank enemy = (EnemyTank) factory.createTank(x, y, Direction.DOWN, GameObjectFactory.TankType.ENEMY);
            enemyTanks.add(enemy);
        }
    }
    
    private boolean isSpawnPositionValid(int x, int y) {
        if (x < GameConstants.TANK_SIZE || y < GameConstants.TANK_SIZE || 
            x > gameWidth - GameConstants.TANK_SIZE * 2 || y > gameHeight - GameConstants.TANK_SIZE * 2) {
            return false;
        }
        
        int safetyMargin = GameConstants.TANK_SIZE + 10;
        for (GameObject wall : walls) {
            if (Math.abs(wall.getX() - x) < safetyMargin && Math.abs(wall.getY() - y) < safetyMargin) {
                return false;
            }
        }
        for (DestructibleWall dWall : destructibleWalls) {
            if (Math.abs(dWall.getX() - x) < safetyMargin && Math.abs(dWall.getY() - y) < safetyMargin) {
                return false;
            }
        }
        
        if (goldenApple != null) {
            if (Math.abs(goldenApple.getX() - x) < safetyMargin && Math.abs(goldenApple.getY() - y) < safetyMargin) {
                return false;
            }
        }
        
        return true;
    }
    
    private void createMedPacks() {
        for (int i = 0; i < GameConstants.MEDPACK_COUNT; i++) {
            double x, y;
            int attempts = 0;
            do {
                x = (random.nextInt((gameWidth / tileSize) - 4) + 2) * tileSize;
                y = (random.nextInt((gameHeight / tileSize) - 4) + 2) * tileSize;
                attempts++;
            } while (isPositionOccupied((int)x, (int)y) && attempts < 50);
            
            if (attempts < 50) {
                medPacks.add(factory.createMedPack(x, y));
            }
        }
    }
    
    private boolean isPositionOccupied(int x, int y) {
        for (GameObject wall : walls) {
            if (Math.abs(wall.getX() - x) < tileSize && Math.abs(wall.getY() - y) < tileSize) return true;
        }
        
        for (DestructibleWall dWall : destructibleWalls) {
            if (Math.abs(dWall.getX() - x) < tileSize && Math.abs(dWall.getY() - y) < tileSize) return true;
        }
        
        if (playerTank != null && Math.abs(playerTank.getX() - x) < tileSize && Math.abs(playerTank.getY() - y) < tileSize) return true;
        
        for (EnemyTank enemy : enemyTanks) {
            if (Math.abs(enemy.getX() - x) < tileSize && Math.abs(enemy.getY() - y) < tileSize) return true;
        }
        
        if (goldenApple != null && Math.abs(goldenApple.getX() - x) < tileSize && Math.abs(goldenApple.getY() - y) < tileSize) return true;
        
        return false;
    }
    
    public void update(Set<KeyCode> pressedKeys) {
        updateMissiles();
        checkCollisions();
        updateDestructibleWalls();
        updateExplosions();
        updateAllObjects();
        handlePlayerInput(pressedKeys);
        updateEnemyTanks();
        updateGoldenApple();
        checkMedPackCollections();
        checkVictoryConditions();
    }
    
    private void handlePlayerInput(Set<KeyCode> pressedKeys) {
        if (!playerTank.isActive()) return;
        playerTank.updateWithBehavior(pressedKeys, allObjects, missiles);
    }
    
    private void updateEnemyTanks() {
        // Create list of all enemy tanks for coordination
        List<Tank> allEnemyTanks = new ArrayList<>();
        for (EnemyTank enemy : enemyTanks) {
            if (enemy.isActive()) {
                allEnemyTanks.add(enemy);
            }
        }
        
        for (EnemyTank enemy : enemyTanks) {
            if (!enemy.isActive()) continue;
            
            // Pass all enemies for coordinated attacks
            enemy.updateWithIntelligentAI(playerTank, allObjects, allEnemyTanks);
            
            if (enemy.wantsToShoot()) {
                Missile missile = enemy.fire();
                if (missile != null) {
                    missiles.add(missile);
                }
            }
        }
    }
    
    private void updateDestructibleWalls() {
        destructibleWalls.forEach(GameObject::update);
        destructibleWalls.removeIf(wall -> !wall.isActive());
    }
    
    private void updateGoldenApple() {
        if (goldenApple != null && goldenApple.isActive()) {
            goldenApple.update();
            
            for (EnemyTank enemy : enemyTanks) {
                if (enemy.isActive() && goldenApple.isInDangerZone(enemy)) {
                    goldenApple.setUnderThreat(true);
                    break;
                }
            }
        }
    }
    
    private void checkVictoryConditions() {
        if (gameOverNotified) return;
        
        // Apple is just a score booster - player collects it for +100 points
        if (goldenApple != null && goldenApple.isActive() && playerTank.isActive()) {
            if (goldenApple.intersects(playerTank)) {
                playerCollectedApple = true;
                goldenApple.setActive(false);
                GameState.getInstance().addScore(100);
                // Don't end game - apple is just a bonus
            }
        }
        
        // Check win/lose conditions (apple no longer affects game ending)
        if (isGameOver() && !gameOverNotified) {
            eventSubject.notifyGameOver(playerWon());
            gameOverNotified = true;
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
                if (missile.hitWall(wall)) {
                    double impactX = missile.getX() + GameConstants.MISSILE_SIZE / 2.0;
                    double impactY = missile.getY() + GameConstants.MISSILE_SIZE / 2.0;
                    explosions.add(factory.createSmallExplosion(impactX, impactY));
                    break;
                }
            }
            
            if (!missile.isActive()) continue;
            
            for (DestructibleWall dWall : new ArrayList<>(destructibleWalls)) {
                boolean wasActive = dWall.isActive();
                if (missile.hitWall(dWall)) {
                    eventSubject.notifyMissileHit(missile, dWall);
                    
                    double impactX = dWall.getX() + dWall.getWidth() / 2.0;
                    double impactY = dWall.getY() + dWall.getHeight() / 2.0;
                    
                    if (!dWall.isActive() && wasActive) {
                        explosions.add(factory.createMediumExplosion(impactX, impactY));
                        if (missile.getOwner() != null && missile.getOwner().isPlayer()) {
                            GameState.getInstance().addScore(10);
                        }
                    } else {
                        explosions.add(factory.createSmallExplosion(impactX, impactY));
                    }
                    break;
                }
            }
            
            if (!missile.isActive()) continue;
            
            boolean playerWasActive = playerTank.isActive();
            if (missile.hitTarget(playerTank) && playerWasActive) {
                eventSubject.notifyMissileHit(missile, playerTank);
                
                double impactX = playerTank.getX() + playerTank.getWidth() / 2.0;
                double impactY = playerTank.getY() + playerTank.getHeight() / 2.0;
                
                if (!playerTank.isActive()) {
                    explosions.add(factory.createLargeExplosion(impactX, impactY));
                    eventSubject.notifyTankDestroyed(playerTank);
                } else {
                    explosions.add(factory.createSmallExplosion(impactX, impactY));
                }
            }
            
            if (!missile.isActive()) continue;
            
            for (EnemyTank enemy : enemyTanks) {
                boolean enemyWasActive = enemy.isActive();
                if (missile.hitTarget(enemy) && enemyWasActive) {
                    eventSubject.notifyMissileHit(missile, enemy);
                    
                    double impactX = enemy.getX() + enemy.getWidth() / 2.0;
                    double impactY = enemy.getY() + enemy.getHeight() / 2.0;
                    
                    if (!enemy.isActive()) {
                        explosions.add(factory.createLargeExplosion(impactX, impactY));
                        eventSubject.notifyTankDestroyed(enemy);
                        if (missile.getOwner() != null && missile.getOwner().isPlayer()) {
                            GameState.getInstance().addScore(25);
                        }
                    } else {
                        explosions.add(factory.createSmallExplosion(impactX, impactY));
                    }
                    break;
                }
            }
        }
    }
    
    private void checkMedPackCollections() {
        for (MedPack medPack : medPacks) {
            if (medPack.isActive()) {
                if (medPack.collectBy(playerTank)) {
                    eventSubject.notifyMedPackCollected(medPack, playerTank);
                }
                
                for (Tank enemy : enemyTanks) {
                    if (medPack.isActive()) {
                        if (medPack.collectBy(enemy)) {
                            eventSubject.notifyMedPackCollected(medPack, enemy);
                        }
                    }
                }
            }
        }
        medPacks.removeIf(medPack -> !medPack.isActive());
    }
    
    private void updateAllObjects() {
        allObjects.clear();
        allObjects.addAll(walls);
        allObjects.addAll(destructibleWalls.stream()
                .filter(GameObject::isActive)
                .collect(Collectors.toList()));
        allObjects.add(playerTank);
        allObjects.addAll(enemyTanks.stream()
                .filter(GameObject::isActive)
                .collect(Collectors.toList()));
        if (goldenApple != null && goldenApple.isActive()) {
            allObjects.add(goldenApple);
        }
    }
    
    public void render(GraphicsContext gc) {
        goldenTiles.forEach(tile -> tile.render(gc));
        
        walls.forEach(wall -> wall.render(gc));
        destructibleWalls.stream()
                .filter(GameObject::isActive)
                .forEach(wall -> wall.render(gc));
        
        if (goldenApple != null && goldenApple.isActive()) {
            goldenApple.render(gc);
        }
        
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
    
    public int getPlayerMaxHealth() {
        return playerTank != null ? playerTank.getMaxHealth() : GameConstants.PLAYER_MAX_HEALTH;
    }
    
    public int getEnemyCount() {
        return (int) enemyTanks.stream().filter(GameObject::isActive).count();
    }
    
    public boolean isGameOver() {
        // Game ends when player dies OR all enemies are defeated
        return !playerTank.isActive() || getEnemyCount() == 0;
    }
    
    public boolean playerWon() {
        // Player wins by defeating all enemies while staying alive
        return playerTank.isActive() && getEnemyCount() == 0;
    }
    
    public boolean appleDestroyed() {
        return false; // Apple no longer affects game state
    }
    
    public boolean playerCollectedApple() {
        return playerCollectedApple;
    }
}

