package com.tankwar.tankwargame.ai;

import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.entities.environment.DestructibleWall;
import com.tankwar.tankwargame.entities.environment.Wall;
import com.tankwar.tankwargame.entities.tanks.Tank;
import com.tankwar.tankwargame.util.Direction;
import com.tankwar.tankwargame.util.GameConstants;
import java.util.*;

/**
 * Improved Enemy AI with coordinated attacks.
 * 
 * Key behaviors:
 * 1. PATROLLING - Wanders when player is far, gradually drifts toward player
 * 2. APPROACHING - Moves directly toward player, navigates around obstacles
 * 3. FLANKING - Circles around to attack from the side (coordinated)
 * 4. ATTACKING - In range, focuses on aiming and shooting
 * 
 * Coordination: When multiple tanks are near the player, they split roles:
 * - DIRECT: Attack head-on
 * - FLANK_LEFT: Circle counterclockwise
 * - FLANK_RIGHT: Circle clockwise
 * 
 * @author Iyed Acheche
 */
public class EnemyAI {
    
    public enum AIState {
        PATROLLING,      // Wandering when player is far
        APPROACHING,     // Moving toward player
        FLANKING,        // Circling around to attack from side
        ATTACKING        // In range, focused on shooting
    }
    
    public enum CombatRole {
        DIRECT,          // Attack head-on
        FLANK_LEFT,      // Circle left of player
        FLANK_RIGHT      // Circle right of player
    }
    
    private AIState currentState = AIState.PATROLLING;
    private CombatRole combatRole = CombatRole.DIRECT;
    private Direction currentDirection;
    
    // Movement tracking
    private double lastX, lastY;
    private int stuckTicks = 0;
    private int directionTicks = 0;  // How long we've been moving in current direction
    private boolean followWallClockwise;  // Alternates navigation preference when stuck
    
    // Unique ID for coordination
    private static int nextId = 0;
    private final int tankId;
    
    // Timing
    private boolean shouldShoot = false;
    private long lastUpdateTime = 0;
    
    // Constants - Chase radius ~1/3 of map
    private static final double ENGAGEMENT_RANGE = 280.0;  // Start chasing within this range
    private static final double ATTACK_RANGE = 120.0;
    private static final double COORDINATION_RANGE = 250.0;
    private static final int STUCK_THRESHOLD = 15;
    private static final int MAX_CHASERS = 3;  // Maximum tanks that can chase at once
    
    // Shared state for coordination (which tanks are currently chasing)
    private static final Set<Integer> activeChasers = new HashSet<>();
    
    public EnemyAI() {
        this.tankId = nextId++;
        this.currentDirection = Direction.values()[(int)(Math.random() * 4)];
        // Alternate wall-following direction for variety
        this.followWallClockwise = (tankId % 2 == 0);
    }
    
    /**
     * Main AI update method.
     * @param self This tank
     * @param player The player tank
     * @param obstacles All obstacles (walls, tanks, etc.)
     * @param allEnemies List of all enemy tanks for coordination
     */
    public void updateAI(Tank self, Tank player, List<GameObject> obstacles, List<Tank> allEnemies) {
        if (!self.isActive() || player == null || !player.isActive()) return;
        
        // Frame rate limiting
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime < 16) return;
        lastUpdateTime = currentTime;
        
        shouldShoot = false;
        double distToPlayer = distance(self, player);
        
        // Track movement for stuck detection
        double movement = Math.abs(self.getX() - lastX) + Math.abs(self.getY() - lastY);
        stuckTicks = (movement < 1) ? stuckTicks + 1 : 0;
        lastX = self.getX();
        lastY = self.getY();
        
        // === COORDINATION: Assign roles when multiple tanks near player ===
        assignCombatRole(self, player, allEnemies);
        
        // === STATE SELECTION with chase limit ===
        AIState previousState = currentState;
        boolean wasChasing = isChasing(previousState);
        
        if (distToPlayer > ENGAGEMENT_RANGE) {
            // Too far - patrol
            currentState = AIState.PATROLLING;
        } else {
            // In range - check if we can chase
            boolean canChase = wasChasing || canBecomeChaser();
            
            if (canChase) {
                if (distToPlayer > ATTACK_RANGE) {
                    currentState = (combatRole != CombatRole.DIRECT) ? AIState.FLANKING : AIState.APPROACHING;
                } else {
                    currentState = AIState.ATTACKING;
                }
            } else {
                // Max chasers reached - patrol instead
                currentState = AIState.PATROLLING;
            }
        }
        
        // Update chaser registry
        updateChaserStatus(wasChasing, isChasing(currentState));
        
        // === EXECUTE BEHAVIOR ===
        switch (currentState) {
            case PATROLLING:
                patrol(self, player, obstacles);
                break;
            case APPROACHING:
                approach(self, player, obstacles);
                break;
            case FLANKING:
                flank(self, player, obstacles);
                break;
            case ATTACKING:
                attack(self, player, obstacles);
                break;
        }
        
        // Always check for shooting opportunity
        checkShoot(self, player, obstacles);
    }
    
    /**
     * Check if a state counts as "chasing" (actively pursuing player).
     */
    private boolean isChasing(AIState state) {
        return state == AIState.APPROACHING || state == AIState.FLANKING || state == AIState.ATTACKING;
    }
    
    /**
     * Check if this tank can become a new chaser (under the limit).
     */
    private boolean canBecomeChaser() {
        synchronized (activeChasers) {
            return activeChasers.size() < MAX_CHASERS;
        }
    }
    
    /**
     * Update the shared chaser registry when state changes.
     */
    private void updateChaserStatus(boolean wasChasing, boolean nowChasing) {
        synchronized (activeChasers) {
            if (!wasChasing && nowChasing) {
                // Became a chaser
                activeChasers.add(tankId);
            } else if (wasChasing && !nowChasing) {
                // Stopped chasing
                activeChasers.remove(tankId);
            }
        }
    }
    
    /**
     * Coordinate attack roles among nearby tanks.
     * Tanks split into DIRECT, FLANK_LEFT, and FLANK_RIGHT based on their ID.
     */
    private void assignCombatRole(Tank self, Tank player, List<Tank> allEnemies) {
        if (allEnemies == null || allEnemies.isEmpty()) {
            combatRole = CombatRole.DIRECT;
            return;
        }
        
        // Count how many tanks are near the player
        int nearbyCount = 0;
        for (Tank enemy : allEnemies) {
            if (enemy.isActive() && distance(enemy, player) < COORDINATION_RANGE) {
                nearbyCount++;
            }
        }
        
        boolean selfNearPlayer = distance(self, player) < COORDINATION_RANGE;
        
        // If alone or far from player, just attack directly
        if (!selfNearPlayer || nearbyCount <= 1) {
            combatRole = CombatRole.DIRECT;
            return;
        }
        
        // Assign roles based on tank ID for consistency
        // This ensures tanks always get the same role, creating stable pincer movements
        switch (tankId % 3) {
            case 0:
                combatRole = CombatRole.DIRECT;
                break;
            case 1:
                combatRole = CombatRole.FLANK_LEFT;
                break;
            case 2:
                combatRole = CombatRole.FLANK_RIGHT;
                break;
        }
    }
    
    /**
     * Patrol: Wander around the map, occasionally drifting toward player.
     * Creates natural movement while waiting for chase slot.
     */
    private void patrol(Tank self, Tank player, List<GameObject> obstacles) {
        directionTicks++;
        
        // Occasionally (every ~2 seconds) consider turning toward player
        if (directionTicks > 60 && Math.random() < 0.1) {
            Direction toPlayer = directionTo(self, player);
            if (canMove(self, toPlayer, obstacles)) {
                currentDirection = toPlayer;
                directionTicks = 0;
            }
        }
        
        // Change direction randomly sometimes for natural wandering
        if (directionTicks > 40 && Math.random() < 0.05) {
            Direction[] dirs = Direction.values();
            Direction newDir = dirs[(int)(Math.random() * dirs.length)];
            if (canMove(self, newDir, obstacles)) {
                currentDirection = newDir;
                directionTicks = 0;
            }
        }
        
        // If blocked or stuck, pick a new direction
        if (stuckTicks > STUCK_THRESHOLD / 2 || !canMove(self, currentDirection, obstacles)) {
            // Try perpendiculars first
            Direction perp1 = currentDirection.getClockwise();
            Direction perp2 = currentDirection.getCounterClockwise();
            
            if (canMove(self, perp1, obstacles)) {
                currentDirection = perp1;
            } else if (canMove(self, perp2, obstacles)) {
                currentDirection = perp2;
            } else if (canMove(self, currentDirection.getOpposite(), obstacles)) {
                currentDirection = currentDirection.getOpposite();
            }
            stuckTicks = 0;
            directionTicks = 0;
        }
        
        // Move in current direction
        if (canMove(self, currentDirection, obstacles)) {
            moveInDirection(self, currentDirection, obstacles);
        }
    }
    
    /**
     * Approach: Move directly toward player, navigate around obstacles.
     * Uses simple but effective strategy: always try to make progress toward player,
     * cycling through directions when blocked.
     */
    private void approach(Tank self, Tank player, List<GameObject> obstacles) {
        Direction toPlayer = directionTo(self, player);
        Direction secondaryDir = getSecondaryDirection(self, player);
        
        // Build priority list: direct path, secondary axis, perpendiculars, then backwards
        Direction[] priorities;
        if (followWallClockwise) {
            priorities = new Direction[] {
                toPlayer,                          // Direct to player
                secondaryDir,                      // Secondary axis toward player  
                toPlayer.getClockwise(),           // Perpendicular CW
                toPlayer.getCounterClockwise(),    // Perpendicular CCW
                toPlayer.getOpposite()             // Backwards (last resort)
            };
        } else {
            priorities = new Direction[] {
                toPlayer,
                secondaryDir,
                toPlayer.getCounterClockwise(),
                toPlayer.getClockwise(),
                toPlayer.getOpposite()
            };
        }
        
        // If stuck for too long, swap preference to try different route
        if (stuckTicks > STUCK_THRESHOLD) {
            followWallClockwise = !followWallClockwise;
            stuckTicks = 0;
        }
        
        // Try each direction in priority order
        boolean moved = false;
        for (Direction dir : priorities) {
            if (dir != null && canMove(self, dir, obstacles)) {
                moveInDirection(self, dir, obstacles);
                currentDirection = dir;
                moved = true;
                break;
            }
        }
        
        // If completely stuck, try any direction
        if (!moved) {
            for (Direction dir : Direction.values()) {
                if (canMove(self, dir, obstacles)) {
                    moveInDirection(self, dir, obstacles);
                    currentDirection = dir;
                    break;
                }
            }
        }
        
        // Face player for potential shots
        self.setDirection(toPlayer);
    }
    
    /**
     * Get the secondary direction toward the player (the lesser axis).
     * If player is mostly to the right and slightly up, returns UP.
     */
    private Direction getSecondaryDirection(GameObject from, GameObject to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        
        // Return the direction along the SMALLER axis
        if (Math.abs(dx) <= Math.abs(dy)) {
            return dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            return dy > 0 ? Direction.DOWN : Direction.UP;
        }
    }
    
    /**
     * Flank: Circle around to attack from the side.
     * Creates pincer movements when coordinated with other tanks.
     */
    private void flank(Tank self, Tank player, List<GameObject> obstacles) {
        Direction toPlayer = directionTo(self, player);
        Direction flankDir = (combatRole == CombatRole.FLANK_LEFT) ? 
            toPlayer.getCounterClockwise() : toPlayer.getClockwise();
        Direction altFlank = (combatRole == CombatRole.FLANK_LEFT) ? 
            toPlayer.getClockwise() : toPlayer.getCounterClockwise();
        
        // Priority: flank direction, then toward player, then alt flank, then away
        Direction[] priorities = {
            flankDir,           // Preferred flank direction
            toPlayer,           // Direct approach
            altFlank,           // Other perpendicular
            toPlayer.getOpposite()  // Retreat to reposition
        };
        
        // If stuck, swap flank preference
        if (stuckTicks > STUCK_THRESHOLD) {
            followWallClockwise = !followWallClockwise;
            stuckTicks = 0;
        }
        
        boolean moved = false;
        for (Direction dir : priorities) {
            if (canMove(self, dir, obstacles)) {
                moveInDirection(self, dir, obstacles);
                currentDirection = dir;
                moved = true;
                break;
            }
        }
        
        // If completely stuck, try any direction
        if (!moved) {
            for (Direction dir : Direction.values()) {
                if (canMove(self, dir, obstacles)) {
                    moveInDirection(self, dir, obstacles);
                    currentDirection = dir;
                    break;
                }
            }
        }
        
        // Always face player
        self.setDirection(toPlayer);
    }
    
    /**
     * Attack: Stay relatively still, focus on aiming and shooting.
     * Makes small adjustments to line up shots.
     */
    private void attack(Tank self, Tank player, List<GameObject> obstacles) {
        Direction toPlayer = directionTo(self, player);
        self.setDirection(toPlayer);
        
        // Calculate offset from player
        double dx = player.getX() - self.getX();
        double dy = player.getY() - self.getY();
        
        // Strafe to align for shot (line up horizontally or vertically)
        if (toPlayer == Direction.UP || toPlayer == Direction.DOWN) {
            // Vertical alignment - strafe horizontally to line up
            if (Math.abs(dx) > 15) {
                Direction strafeDir = dx > 0 ? Direction.RIGHT : Direction.LEFT;
                if (canMove(self, strafeDir, obstacles)) {
                    moveInDirection(self, strafeDir, obstacles);
                }
            }
        } else {
            // Horizontal alignment - strafe vertically to line up
            if (Math.abs(dy) > 15) {
                Direction strafeDir = dy > 0 ? Direction.DOWN : Direction.UP;
                if (canMove(self, strafeDir, obstacles)) {
                    moveInDirection(self, strafeDir, obstacles);
                }
            }
        }
    }
    
    /**
     * Check if we should shoot at the player.
     * Shoots when aligned and has line of sight.
     */
    private void checkShoot(Tank self, Tank player, List<GameObject> obstacles) {
        Direction facing = self.getDirection();
        double dx = player.getX() - self.getX();
        double dy = player.getY() - self.getY();
        
        boolean aligned = false;
        double tolerance = 35;  // Alignment tolerance in pixels
        
        switch (facing) {
            case UP:    aligned = dy < 0 && Math.abs(dx) < tolerance; break;
            case DOWN:  aligned = dy > 0 && Math.abs(dx) < tolerance; break;
            case LEFT:  aligned = dx < 0 && Math.abs(dy) < tolerance; break;
            case RIGHT: aligned = dx > 0 && Math.abs(dy) < tolerance; break;
        }
        
        if (aligned && hasLineOfSight(self, player, obstacles)) {
            shouldShoot = true;
        }
    }
    
    // === UTILITY METHODS ===
    
    /**
     * Get the cardinal direction from one object to another.
     */
    private Direction directionTo(GameObject from, GameObject to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            return dy > 0 ? Direction.DOWN : Direction.UP;
        }
    }
    
    /**
     * Move the tank in the specified direction.
     */
    private void moveInDirection(Tank self, Direction dir, List<GameObject> obstacles) {
        self.move(dir, obstacles);
    }
    
    /**
     * Check if the tank can move in the specified direction without colliding.
     */
    private boolean canMove(Tank tank, Direction dir, List<GameObject> obstacles) {
        double speed = GameConstants.ENEMY_TANK_SPEED;
        double newX = tank.getX() + dir.getDx() * speed;
        double newY = tank.getY() + dir.getDy() * speed;
        
        // Boundary check
        if (newX < 0 || newY < 0 || 
            newX + tank.getWidth() > GameConstants.GAME_WIDTH || 
            newY + tank.getHeight() > GameConstants.GAME_HEIGHT) {
            return false;
        }
        
        // Obstacle collision check with small buffer for smoother navigation
        final int BUFFER = 2;
        for (GameObject obs : obstacles) {
            if (obs == tank || !obs.isActive()) continue;
            
            if (newX + BUFFER < obs.getX() + obs.getWidth() - BUFFER && 
                newX + tank.getWidth() - BUFFER > obs.getX() + BUFFER &&
                newY + BUFFER < obs.getY() + obs.getHeight() - BUFFER && 
                newY + tank.getHeight() - BUFFER > obs.getY() + BUFFER) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if there's a clear line of sight between two tanks.
     * Used for shooting decisions.
     */
    private boolean hasLineOfSight(Tank from, Tank to, List<GameObject> obstacles) {
        double fx = from.getX() + from.getWidth() / 2;
        double fy = from.getY() + from.getHeight() / 2;
        double tx = to.getX() + to.getWidth() / 2;
        double ty = to.getY() + to.getHeight() / 2;
        
        double dx = tx - fx, dy = ty - fy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 1) return true;
        
        dx /= dist; 
        dy /= dist;
        
        // Ray march along the line checking for wall collisions
        for (double t = 15; t < dist - 15; t += 15) {
            double cx = fx + dx * t;
            double cy = fy + dy * t;
            
            for (GameObject obs : obstacles) {
                if ((obs instanceof Wall || obs instanceof DestructibleWall) && obs.isActive()) {
                    if (cx >= obs.getX() && cx <= obs.getX() + obs.getWidth() &&
                        cy >= obs.getY() && cy <= obs.getY() + obs.getHeight()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Calculate distance between two game objects.
     */
    private double distance(GameObject a, GameObject b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    // === PUBLIC GETTERS ===
    
    public AIState getCurrentState() { return currentState; }
    public CombatRole getCombatRole() { return combatRole; }
    public boolean wantsToShoot() { return shouldShoot; }
    
    /**
     * Reset the AI ID counter and chaser registry. Call when starting a new game.
     */
    public static void resetIdCounter() { 
        nextId = 0; 
        synchronized (activeChasers) {
            activeChasers.clear();
        }
    }
}
