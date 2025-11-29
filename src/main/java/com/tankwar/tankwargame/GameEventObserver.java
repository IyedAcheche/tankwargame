package com.tankwar.tankwargame;

import java.util.*;

public interface GameEventObserver {
    void onTankDestroyed(Tank tank);
    void onMedPackCollected(MedPack medPack, Tank tank);
    void onMissileHit(Missile missile, GameObject target);
    void onGameOver(boolean playerWon);
}

class GameEventSubject {
    private List<GameEventObserver> observers = new ArrayList<>();
    
    public void addObserver(GameEventObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(GameEventObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyTankDestroyed(Tank tank) {
        observers.forEach(observer -> observer.onTankDestroyed(tank));
    }
    
    public void notifyMedPackCollected(MedPack medPack, Tank tank) {
        observers.forEach(observer -> observer.onMedPackCollected(medPack, tank));
    }
    
    public void notifyMissileHit(Missile missile, GameObject target) {
        observers.forEach(observer -> observer.onMissileHit(missile, target));
    }
    
    public void notifyGameOver(boolean playerWon) {
        observers.forEach(observer -> observer.onGameOver(playerWon));
    }
}

class GameEventHandler implements GameEventObserver {
    private List<Explosion> explosions;
    
    public GameEventHandler(List<Explosion> explosions) {
        this.explosions = explosions;
    }
    
    @Override
    public void onTankDestroyed(Tank tank) {
        System.out.println("Tank destroyed at (" + tank.getX() + ", " + tank.getY() + ")");
        explosions.add(new Explosion(tank.getX(), tank.getY()));
    }
    
    @Override
    public void onMedPackCollected(MedPack medPack, Tank tank) {
        System.out.println("MedPack collected by " + (tank.isPlayer() ? "player" : "enemy"));
    }
    
    @Override
    public void onMissileHit(Missile missile, GameObject target) {
        System.out.println("Missile hit target");
    }
    
    @Override
    public void onGameOver(boolean playerWon) {
        System.out.println("Game Over! " + (playerWon ? "Player Won!" : "Player Lost!"));
    }
}

