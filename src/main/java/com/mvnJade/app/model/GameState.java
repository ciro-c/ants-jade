package com.mvnJade.app.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameState {

    public enum Status { STOPPED, RUNNING, FINISHED }

    private static GameState instance;
    private Status currentStatus = Status.STOPPED;

    private final Map<String, Entity> entities = new ConcurrentHashMap<>();

    private GameState() {}

    public static synchronized GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public Status getStatus() { 
        return currentStatus; 
    }
    
    public void setStatus(Status status) { 
        this.currentStatus = status; 
    }

    public void registerEntity(Entity entity) {
        entities.put(entity.getId(), entity);
    }

    public Map<String, Entity> getEntities() {
        return entities;
    }
}