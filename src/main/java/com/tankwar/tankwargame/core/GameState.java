package com.tankwar.tankwargame.core;

/**
 * Global game state manager (Singleton pattern).
 * @author Iyed Acheche
 */
public class GameState {
    private static GameState instance;
    
    private int score = 0;
    private int lives = 3;
    private int level = 1;
    private boolean gameRunning = true;
    private boolean playerWon = false;
    
    private GameState() {}
    
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }
    
    public void addScore(int points) {
        score += points;
    }
    
    public void loseLife() {
        lives--;
        if (lives <= 0) {
            gameRunning = false;
        }
    }
    
    public void nextLevel() {
        level++;
        score += 1000;
    }
    
    public void gameOver(boolean won) {
        gameRunning = false;
        playerWon = won;
        if (won) {
            score += 5000;
        }
    }
    
    public void reset() {
        score = 0;
        lives = 3;
        level = 1;
        gameRunning = true;
        playerWon = false;
    }
    
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public int getLevel() { return level; }
    public boolean isGameRunning() { return gameRunning; }
    public boolean isPlayerWon() { return playerWon; }
}


