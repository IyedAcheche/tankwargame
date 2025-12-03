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


## How to Run

```bash
./mvnw clean javafx:run
```

Or alternatively:
```bash  
./mvnw clean compile exec:java
```

