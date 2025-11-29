package com.tankwar.tankwargame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.*;

/**
 * Main application class for the Tank War Game.
 * This class serves as the entry point and handles the JavaFX GUI setup and game loop.
 * 
 * OOP Concepts Applied:
 * - ENCAPSULATION: Private fields with controlled access to game state
 * - COMPOSITION: Uses GameEngine to separate game logic from GUI concerns
 * - SEPARATION OF CONCERNS: GUI logic is separated from game mechanics
 * 
 * Design Patterns:
 * - MVC Pattern: This acts as the View/Controller, GameEngine as Model
 * - Template Method: JavaFX Application provides template for start() method
 * 
 * @author Iyed Acheche
 */
public class TankWarGame extends Application {
    
    // Private fields demonstrating ENCAPSULATION - hiding implementation details
    private Canvas canvas;              // JavaFX Canvas for rendering
    private GraphicsContext gc;         // Graphics context for drawing operations
    private GameEngine gameEngine;      // COMPOSITION - uses GameEngine for game logic
    private AnimationTimer gameLoop;    // JavaFX animation timer for game loop
    private Set<KeyCode> pressedKeys = new HashSet<>();  // Tracks currently pressed keys
    
    /**
     * JavaFX start method - sets up the GUI components and initializes the game.
     * Demonstrates TEMPLATE METHOD pattern from JavaFX Application class.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tank War Game");
        
        // Initialize canvas for drawing the game
        canvas = new Canvas(GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        
        // COMPOSITION: Create game engine to handle all game logic
        gameEngine = new GameEngine(GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT, GameConstants.TILE_SIZE);
        
        // Set up JavaFX scene hierarchy
        VBox root = new VBox();
        root.getChildren().add(canvas);
        
        Scene scene = new Scene(root);
        setupKeyHandlers(scene);  // Delegate key handling setup
        
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        startGameLoop();  // Begin the game loop
    }
    
    /**
     * Sets up keyboard input handling using lambda expressions.
     * Demonstrates EVENT-DRIVEN programming and LAMBDA usage.
     */
    private void setupKeyHandlers(Scene scene) {
        // Lambda expression for key press events
        scene.setOnKeyPressed(e -> {
            pressedKeys.add(e.getCode());
            // Handle game restart - uses SINGLETON pattern via GameState
            if (e.getCode() == KeyCode.R && gameEngine.isGameOver()) {
                gameEngine = new GameEngine(GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT, GameConstants.TILE_SIZE);
                GameState.getInstance().reset();  // SINGLETON pattern usage
            }
        });
        
        // Lambda expression for key release events
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));
        
        // Ensure canvas can receive keyboard focus
        canvas.requestFocus();
        canvas.setFocusTraversable(true);
    }
    
    /**
     * Starts the main game loop using JavaFX AnimationTimer.
     * Demonstrates ANONYMOUS INNER CLASS and TEMPLATE METHOD pattern.
     */
    private void startGameLoop() {
        // Anonymous inner class extending AnimationTimer
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();  // Update game state
                render();  // Render graphics
            }
        };
        gameLoop.start();
    }
    
    /**
     * Updates game logic by delegating to GameEngine.
     * Demonstrates DELEGATION pattern.
     */
    private void update() {
        gameEngine.update(pressedKeys);  // DELEGATION to game engine
    }
    
    /**
     * Renders the game graphics and UI elements.
     * Separates rendering logic from game logic (SEPARATION OF CONCERNS).
     */
    private void render() {
        // Clear canvas with black background
        gc.clearRect(0, 0, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);
        
        // Delegate game object rendering to GameEngine
        gameEngine.render(gc);
        
        // Render UI elements (health, score, etc.)
        renderUI();
    }
    
    /**
     * Renders the user interface elements (health, score, game over screen).
     * Uses SINGLETON pattern to access global game state.
     */
    private void renderUI() {
        // Access SINGLETON GameState instance
        GameState state = GameState.getInstance();
        
        // Set text color and font for UI elements
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(16));
        
        // Display game statistics using data from different sources
        gc.fillText("Health: " + gameEngine.getPlayerHealth(), 10, 25);
        gc.fillText("Enemies: " + gameEngine.getEnemyCount(), 10, 45);
        gc.fillText("Score: " + state.getScore(), 10, 65);
        gc.fillText("Lives: " + state.getLives(), 10, 85);
        gc.fillText("Level: " + state.getLevel(), 10, 105);
        
        // Game over screen with conditional logic
        if (gameEngine.isGameOver()) {
            gc.setFont(Font.font(32));
            gc.setFill(Color.YELLOW);
            String message = gameEngine.playerWon() ? "YOU WIN!" : "GAME OVER!";
            gc.fillText(message, GameConstants.GAME_WIDTH / 2 - 80, GameConstants.GAME_HEIGHT / 2);
            
            gc.setFont(Font.font(16));
            gc.fillText("Press R to restart", GameConstants.GAME_WIDTH / 2 - 60, GameConstants.GAME_HEIGHT / 2 + 40);
        }
    }
    
    /**
     * Main entry point for the application.
     * Uses JavaFX launch mechanism to start the application.
     */
    public static void main(String[] args) {
        launch(args);  // JavaFX application launcher
    }
}

