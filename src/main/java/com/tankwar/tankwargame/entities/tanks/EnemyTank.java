package com.tankwar.tankwargame.entities.tanks;

import com.tankwar.tankwargame.ai.EnemyAI;
import com.tankwar.tankwargame.entities.base.GameObject;
import com.tankwar.tankwargame.util.Direction;
import com.tankwar.tankwargame.util.GameConstants;
import java.util.*;

/**
 * AI-controlled enemy tank class.
 * Uses intelligent AI for pathfinding and coordinated combat behavior.
 * @author Iyed Acheche
 */
public class EnemyTank extends Tank {
    private EnemyAI aiController;
    
    public EnemyTank(double x, double y, Direction direction) {
        super(x, y, direction, false);
        this.speed = GameConstants.ENEMY_TANK_SPEED;
        this.aiController = new EnemyAI();
    }
    
    /**
     * Enhanced AI update with intelligent decision making and coordination.
     * @param playerTank The player tank to target
     * @param obstacles All obstacles including walls and other tanks
     * @param allEnemies All enemy tanks for coordination
     */
    public void updateWithIntelligentAI(Tank playerTank, List<GameObject> obstacles, 
                                        List<Tank> allEnemies) {
        if (aiController != null) {
            aiController.updateAI(this, playerTank, obstacles, allEnemies);
        }
    }
    
    /**
     * Returns true if the AI wants to shoot this frame.
     */
    public boolean wantsToShoot() {
        return aiController != null && aiController.wantsToShoot();
    }
    
    /**
     * Get the current AI state for debugging/display.
     */
    public EnemyAI.AIState getAIState() {
        return aiController != null ? aiController.getCurrentState() : null;
    }
    
    /**
     * Get the current combat role for debugging/display.
     */
    public EnemyAI.CombatRole getCombatRole() {
        return aiController != null ? aiController.getCombatRole() : null;
    }
}

