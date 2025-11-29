package com.tankwar.tankwargame;

/**
 * Global game state manager (Singleton pattern).
 * @author Iyed Acheche
 */
public class GameState {
    // Singleton instance
    private static GameState instance;
    
    // Game state fields
    private int score = 0;
    private int lives = 3;
    private int level = 1;
    private boolean gameRunning = true;
    private boolean playerWon = false;
    
    // Private constructor for Singleton
    private GameState() {}
    
    // Get singleton instance
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }
    
    // Add to player score
    public void addScore(int points) {
        score += points;
    }
    
    // Lose a life and check game over
    public void loseLife() {
        lives--;
        if (lives <= 0) {
            gameRunning = false;
        }
    }
    
    // Advance to next level
    public void nextLevel() {
        level++;
        score += 1000;
    }
    
    // End game with win/lose status
    public void gameOver(boolean won) {
        gameRunning = false;
        playerWon = won;
        if (won) {
            score += 5000;
        }
    }
    
    // Reset for new game
    public void reset() {
        score = 0;
        lives = 3;
        level = 1;
        gameRunning = true;
        playerWon = false;
    }
    
    // Getters for accessing state
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public int getLevel() { return level; }
    public boolean isGameRunning() { return gameRunning; }
    public boolean isPlayerWon() { return playerWon; }
}

