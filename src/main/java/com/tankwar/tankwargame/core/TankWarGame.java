package com.tankwar.tankwargame.core;

import com.tankwar.tankwargame.util.GameConstants;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.*;

/**
 * Main application class for the Tank War Game.
 * Handles JavaFX GUI setup and game loop.
 * 
 * OOP Concepts: Encapsulation, Composition, Separation of Concerns
 * Design Patterns: MVC Pattern, Template Method
 * 
 * @author Iyed Acheche
 */
public class TankWarGame extends Application {
    
    private Canvas canvas;
    private GraphicsContext gc;
    private GameEngine gameEngine;
    private AnimationTimer gameLoop;
    private Set<KeyCode> pressedKeys = new HashSet<>();
    
    private MenuState currentState = MenuState.MENU;
    private StackPane mainContainer;
    private VBox menuPane;
    private VBox pausePane;
    private VBox winPane;
    private VBox lossPane;
    private Scene scene;
    private boolean gameOverScreenShown = false;
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tank War Game");
        
        canvas = new Canvas(GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        
        mainContainer = new StackPane();
        
        createMenu();
        createPauseOverlay();
        
        scene = new Scene(mainContainer, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
        setupKeyHandlers(scene);
        
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        showMenu();
        startGameLoop();
    }
    
    private void createMenu() {
        menuPane = new VBox(30);
        menuPane.setAlignment(Pos.CENTER);
        menuPane.setStyle("-fx-background-color: #2b2b2b;");
        
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/com/tankwar/tankwargame/images/game_logo.png"));
            ImageView logoView = new ImageView(logoImage);
            logoView.setFitWidth(300);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);
            menuPane.getChildren().add(logoView);
        } catch (Exception e) {
            Label welcomeLabel = new Label("Welcome to Tank City");
            welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
            welcomeLabel.setTextFill(Color.YELLOW);
            menuPane.getChildren().add(welcomeLabel);
        }
        
        Button startButton = new Button("START");
        startButton.setPrefSize(150, 50);
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        startButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: black; -fx-border-width: 2px; -fx-background-radius: 5px; -fx-border-radius: 5px;");
        startButton.setOnAction(e -> startGame());
        
        Button exitButton = new Button("EXIT");
        exitButton.setPrefSize(150, 50);
        exitButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        exitButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2px; -fx-background-radius: 5px; -fx-border-radius: 5px;");
        exitButton.setOnAction(e -> System.exit(0));
        
        menuPane.getChildren().addAll(startButton, exitButton);
    }
    
    private void createPauseOverlay() {
        pausePane = new VBox(30);
        pausePane.setAlignment(Pos.CENTER);
        pausePane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        
        Label pauseLabel = new Label("GAME PAUSED");
        pauseLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        pauseLabel.setTextFill(Color.YELLOW);
        
        Button continueButton = new Button("CONTINUE");
        continueButton.setPrefSize(150, 50);
        continueButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        continueButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: black; -fx-border-width: 2px; -fx-background-radius: 5px; -fx-border-radius: 5px;");
        continueButton.setOnAction(e -> resumeGame());
        
        Button exitToMenuButton = new Button("EXIT TO MENU");
        exitToMenuButton.setPrefSize(150, 50);
        exitToMenuButton.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        exitToMenuButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2px; -fx-background-radius: 5px; -fx-border-radius: 5px;");
        exitToMenuButton.setOnAction(e -> returnToMenu());
        
        pausePane.getChildren().addAll(pauseLabel, continueButton, exitToMenuButton);
    }
    
    private void createWinScreen(String winMessage) {
        winPane = new VBox(40);
        winPane.setAlignment(Pos.CENTER);
        winPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        
        Label winLabel = new Label("YOU WIN!");
        winLabel.setFont(Font.font("Arial", FontWeight.BOLD, 64));
        winLabel.setTextFill(Color.GOLD);
        winLabel.setStyle("-fx-effect: dropshadow(gaussian, black, 10, 0.5, 2, 2);");
        
        Label victoryMessage = new Label(winMessage);
        victoryMessage.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        victoryMessage.setTextFill(Color.WHITE);
        
        Label scoreLabel = new Label("Final Score: " + GameState.getInstance().getScore());
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        scoreLabel.setTextFill(Color.YELLOW);
        
        Button restartButton = new Button("RESTART");
        restartButton.setPrefSize(120, 40);
        restartButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        restartButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: black; -fx-border-width: 2px; -fx-background-radius: 5px; -fx-border-radius: 5px;");
        restartButton.setOnAction(e -> restartGame());
        
        Button exitToMenuButton = new Button("EXIT");
        exitToMenuButton.setPrefSize(120, 40);
        exitToMenuButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        exitToMenuButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2px; -fx-background-radius: 5px; -fx-border-radius: 5px;");
        exitToMenuButton.setOnAction(e -> returnToMenu());
        
        winPane.getChildren().addAll(winLabel, victoryMessage, scoreLabel, restartButton, exitToMenuButton);
    }
    
    private void createLossScreen(String lossMessage) {
        lossPane = new VBox(40);
        lossPane.setAlignment(Pos.CENTER);
        lossPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        
        Label lossLabel = new Label("GAME OVER");
        lossLabel.setFont(Font.font("Arial", FontWeight.BOLD, 64));
        lossLabel.setTextFill(Color.rgb(200, 50, 50));
        lossLabel.setStyle("-fx-effect: dropshadow(gaussian, black, 10, 0.5, 2, 2);");
        
        Label lossMessage_label = new Label(lossMessage);
        lossMessage_label.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        lossMessage_label.setTextFill(Color.rgb(255, 200, 100));
        
        Label scoreLabel = new Label("Final Score: " + GameState.getInstance().getScore());
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        scoreLabel.setTextFill(Color.WHITE);
        
        Button restartButton = new Button("RESTART");
        restartButton.setPrefSize(120, 40);
        restartButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        restartButton.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: black; -fx-border-width: 2px; -fx-background-radius: 5px; -fx-border-radius: 5px;");
        restartButton.setOnAction(e -> restartGame());
        
        Button exitToMenuButton = new Button("EXIT");
        exitToMenuButton.setPrefSize(120, 40);
        exitToMenuButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        exitToMenuButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2px; -fx-background-radius: 5px; -fx-border-radius: 5px;");
        exitToMenuButton.setOnAction(e -> returnToMenu());
        
        lossPane.getChildren().addAll(lossLabel, lossMessage_label, scoreLabel, restartButton, exitToMenuButton);
    }
    
    private void showLossScreen() {
        currentState = MenuState.GAME_OVER;
        
        String lossMessage;
        if (gameEngine.appleDestroyed()) {
            lossMessage = "The Golden Apple was reached!";
        } else if (gameEngine.getPlayerHealth() <= 0) {
            lossMessage = "Your tank was destroyed!";
        } else {
            lossMessage = "You were defeated!";
        }
        
        createLossScreen(lossMessage);
        
        GaussianBlur blur = new GaussianBlur(15);
        canvas.setEffect(blur);
        
        mainContainer.getChildren().add(lossPane);
        gameOverScreenShown = true;
    }
    
    private void showMenu() {
        currentState = MenuState.MENU;
        mainContainer.getChildren().clear();
        mainContainer.getChildren().add(menuPane);
    }
    
    private void startGame() {
        currentState = MenuState.PLAYING;
        
        if (gameEngine == null) {
            gameEngine = new GameEngine(GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT, GameConstants.TILE_SIZE);
        } else {
            gameEngine = new GameEngine(GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT, GameConstants.TILE_SIZE);
            GameState.getInstance().reset();
        }
        
        mainContainer.getChildren().clear();
        mainContainer.getChildren().add(canvas);
        canvas.requestFocus();
    }
    
    private void returnToMenu() {
        currentState = MenuState.MENU;
        gameOverScreenShown = false;
        mainContainer.getChildren().clear();
        mainContainer.getChildren().add(menuPane);
        canvas.setEffect(null);
    }
    
    private void restartGame() {
        gameEngine = new GameEngine(GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT, GameConstants.TILE_SIZE);
        GameState.getInstance().reset();
        
        currentState = MenuState.PLAYING;
        gameOverScreenShown = false;
        
        canvas.setEffect(null);
        mainContainer.getChildren().clear();
        mainContainer.getChildren().add(canvas);
        canvas.requestFocus();
    }
    
    private void showWinScreen() {
        currentState = MenuState.GAME_OVER;
        
        String winMessage;
        if (gameEngine.playerCollectedApple()) {
            winMessage = "Golden Apple Collected! +100 points";
        } else {
            winMessage = "All enemies defeated!";
        }
        
        createWinScreen(winMessage);
        
        GaussianBlur blur = new GaussianBlur(15);
        canvas.setEffect(blur);
        
        mainContainer.getChildren().add(winPane);
    }
    
    private void pauseGame() {
        if (currentState == MenuState.PLAYING && gameEngine != null && !gameEngine.isGameOver()) {
            currentState = MenuState.PAUSED;
            
            GaussianBlur blur = new GaussianBlur(10);
            canvas.setEffect(blur);
            
            mainContainer.getChildren().add(pausePane);
        }
    }
    
    private void resumeGame() {
        if (currentState == MenuState.PAUSED) {
            currentState = MenuState.PLAYING;
            canvas.setEffect(null);
            mainContainer.getChildren().remove(pausePane);
            canvas.requestFocus();
        }
    }
    
    private void setupKeyHandlers(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P) {
                if (currentState == MenuState.PLAYING) {
                    pauseGame();
                } else if (currentState == MenuState.PAUSED) {
                    resumeGame();
                }
            }
            
            if (e.getCode() == KeyCode.ESCAPE) {
                if (currentState == MenuState.PLAYING) {
                    pauseGame();
                } else if (currentState == MenuState.PAUSED) {
                    resumeGame();
                }
            }
            
            if (currentState == MenuState.PLAYING) {
                pressedKeys.add(e.getCode());
            }
            
            if (e.getCode() == KeyCode.R && gameEngine != null && gameEngine.isGameOver()) {
                restartGame();
            }
        });
        
        scene.setOnKeyReleased(e -> {
            if (currentState == MenuState.PLAYING) {
                pressedKeys.remove(e.getCode());
            }
        });
    }
    
    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        gameLoop.start();
    }
    
    private void update() {
        if (currentState == MenuState.PLAYING && gameEngine != null) {
            gameEngine.update(pressedKeys);
        }
    }
    
    private void render() {
        if ((currentState == MenuState.PLAYING || currentState == MenuState.PAUSED || currentState == MenuState.GAME_OVER) && gameEngine != null) {
            gc.clearRect(0, 0, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
            
            if (currentState == MenuState.PLAYING || (currentState == MenuState.GAME_OVER && !gameEngine.playerWon())) {
                renderTopBar();
            }
            
            gc.save();
            gc.translate(0, GameConstants.TOP_BAR_HEIGHT);
            
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, GameConstants.GAME_WIDTH, GameConstants.GAME_HEIGHT);
            
            gameEngine.render(gc);
            
            gc.restore();
            
            if (currentState == MenuState.PLAYING || (currentState == MenuState.GAME_OVER && !gameEngine.playerWon())) {
                renderGameOverIfNeeded();
            }
        }
    }
    
    private void renderTopBar() {
        GameState state = GameState.getInstance();
        
        int barHeight = GameConstants.TOP_BAR_HEIGHT;
        
        gc.setFill(Color.rgb(20, 20, 30));
        gc.fillRect(0, 0, GameConstants.WINDOW_WIDTH, barHeight);
        
        gc.setStroke(Color.rgb(60, 60, 80));
        gc.setLineWidth(2);
        gc.strokeLine(0, barHeight, GameConstants.WINDOW_WIDTH, barHeight);
        
        gc.setStroke(Color.rgb(255, 200, 50, 0.6));
        gc.setLineWidth(1);
        gc.strokeLine(0, barHeight - 1, GameConstants.WINDOW_WIDTH, barHeight - 1);
        
        int healthSectionX = 15;
        int centerY = barHeight / 2;
        
        gc.setFill(Color.rgb(220, 50, 50));
        gc.fillOval(healthSectionX, centerY - 8, 16, 16);
        
        gc.setFill(Color.rgb(180, 180, 190));
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        gc.fillText("HEALTH", healthSectionX + 22, centerY - 5);
        
        int healthBarX = healthSectionX + 22;
        int healthBarY = centerY + 2;
        int healthBarWidth = 120;
        int healthBarHeight = 14;
        
        int currentHealth = gameEngine.getPlayerHealth();
        int maxHealth = gameEngine.getPlayerMaxHealth();
        double healthPercent = (double) currentHealth / maxHealth;
        
        gc.setFill(Color.rgb(40, 40, 50));
        gc.fillRoundRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight, 6, 6);
        
        if (healthPercent > 0.6) {
            gc.setFill(Color.rgb(80, 200, 80));
        } else if (healthPercent > 0.3) {
            gc.setFill(Color.rgb(220, 160, 40));
        } else {
            gc.setFill(Color.rgb(200, 60, 60));
        }
        if (healthPercent > 0) {
            gc.fillRoundRect(healthBarX + 2, healthBarY + 2, (healthBarWidth - 4) * healthPercent, healthBarHeight - 4, 4, 4);
        }
        
        gc.setStroke(Color.rgb(80, 80, 100));
        gc.setLineWidth(1);
        gc.strokeRoundRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight, 6, 6);
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.fillText(currentHealth + "/" + maxHealth, healthBarX + healthBarWidth + 8, healthBarY + 11);
        
        int enemiesSectionX = 280;
        
        gc.setFill(Color.rgb(180, 50, 50));
        gc.fillOval(enemiesSectionX, centerY - 7, 14, 14);
        gc.setFill(Color.rgb(20, 20, 30));
        gc.fillOval(enemiesSectionX + 2, centerY - 3, 4, 4);
        gc.fillOval(enemiesSectionX + 8, centerY - 3, 4, 4);
        
        gc.setFill(Color.rgb(180, 180, 190));
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        gc.fillText("ENEMIES", enemiesSectionX + 20, centerY - 5);
        
        gc.setFill(Color.rgb(220, 80, 80));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gc.fillText(String.valueOf(gameEngine.getEnemyCount()), enemiesSectionX + 20, centerY + 15);
        
        int scoreSectionX = 420;
        
        gc.setFill(Color.rgb(255, 200, 50));
        double[] starX = {scoreSectionX + 7, scoreSectionX + 9, scoreSectionX + 14, scoreSectionX + 10, scoreSectionX + 12, scoreSectionX + 7, scoreSectionX + 2, scoreSectionX + 4, scoreSectionX, scoreSectionX + 5};
        double[] starY = {centerY - 8, centerY - 2, centerY - 2, centerY + 2, centerY + 8, centerY + 4, centerY + 8, centerY + 2, centerY - 2, centerY - 2};
        gc.fillPolygon(starX, starY, 10);
        
        gc.setFill(Color.rgb(180, 180, 190));
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        gc.fillText("SCORE", scoreSectionX + 20, centerY - 5);
        
        gc.setFill(Color.rgb(255, 215, 80));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gc.fillText(String.valueOf(state.getScore()), scoreSectionX + 20, centerY + 15);
        
        int objectiveSectionX = 560;
        
        gc.setFill(Color.rgb(255, 200, 50));
        gc.fillOval(objectiveSectionX, centerY - 6, 12, 12);
        gc.setFill(Color.rgb(100, 70, 40));
        gc.fillRect(objectiveSectionX + 5, centerY - 10, 2, 5);
        
        gc.setFill(Color.rgb(255, 200, 50));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.fillText("PROTECT THE APPLE!", objectiveSectionX + 18, centerY + 3);
        
        gc.setFill(Color.rgb(120, 120, 140));
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        gc.fillText("[P] Pause", GameConstants.WINDOW_WIDTH - 60, centerY + 3);
    }
    
    private void renderGameOverIfNeeded() {
        if (!gameEngine.isGameOver()) return;
        
        if (gameOverScreenShown) return;
        
        if (gameEngine.playerWon()) {
            if (currentState != MenuState.GAME_OVER) {
                showWinScreen();
                gameOverScreenShown = true;
            }
        } else {
            if (currentState != MenuState.GAME_OVER) {
                showLossScreen();
            }
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

