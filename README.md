# Tank War Game

**Author:** Iyed Acheche  
**Course:** Object-Oriented Programming  

## Overview

A 2D tank battle game built with JavaFX. Players control a tank to fight enemy tanks in a grid-based arena.

## How to Play

- **Arrow Keys**: Move tank
- **Spacebar**: Fire missiles  
- **R**: Restart game (when game over)

## Game Features

- Player tank vs 6 enemy tanks
- Health system with medical packs
- Collision detection
- Score and lives tracking
- Explosion effects

## OOP Concepts Used

### Inheritance
- `GameObject` base class for all game objects
- `Tank` extended by `PlayerTank` and `EnemyTank`
- All objects inherit position, rendering, collision detection

### Encapsulation  
- Private fields with public getter/setter methods
- Protected fields in base classes for subclass access
- Internal game state hidden from outside classes

### Polymorphism
- `GameObject.update()` implemented differently by each subclass
- `TankBehavior` interface for player vs AI control
- Same methods work with different object types

### Abstraction
- Abstract `GameObject` class defines common interface
- `TankBehavior` interface hides implementation details
- Complex game logic simplified through clear interfaces

## Design Patterns

### Singleton Pattern
- `GameState` - single instance manages score, lives, level
- `GameObjectFactory` - centralized object creation

### Factory Pattern  
- `GameObjectFactory` creates tanks, missiles, walls, etc.
- Easy to add new object types

### Strategy Pattern
- `PlayerBehavior` - keyboard input handling
- `EnemyBehavior` - AI movement and shooting
- Different behaviors without changing Tank class

## Project Structure

```
src/main/java/com/tankwar/tankwargame/
├── TankWarGame.java          # Main JavaFX application
├── GameEngine.java           # Core game logic  
├── GameState.java            # Game state management
├── GameObjectFactory.java    # Object creation
├── GameObject.java           # Base class for all objects
├── Tank.java                # Tank base class
├── PlayerTank.java          # Player tank
├── EnemyTank.java           # Enemy tank
├── TankBehavior.java        # Tank AI interface
├── Missile.java             # Projectiles
├── Wall.java                # Obstacles
├── MedPack.java             # Health pickups
├── Explosion.java           # Visual effects
├── Direction.java           # Movement directions
└── GameConstants.java       # Game settings
```

## How to Run

```bash
./mvnw clean javafx:run
```

Or alternatively:
```bash  
./mvnw clean compile exec:java
```

## Key Learning Points

1. **Inheritance** - Code reuse through class hierarchies
2. **Polymorphism** - Same interface, different behaviors  
3. **Encapsulation** - Data protection and controlled access
4. **Design Patterns** - Structured solutions to common problems
5. **JavaFX** - Modern Java GUI development
6. **Game Programming** - Game loops, collision detection, state management