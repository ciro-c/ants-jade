package com.mvnJade.app.model;

public abstract class Entity {
    private String id; // O nome do agente no JADE (ex: "Jogador1", "Bola")
    private Vector2D position;

    public Entity(String id, float startX, float startY) {
        this.id = id;
        this.position = new Vector2D(startX, startY);
    }

    public String getId() { return id; }
    public Vector2D getPosition() { return position; }
    
    // Método utilitário para o agente JADE atualizar sua própria posição
    public void updatePosition(float x, float y) {
        this.position.setX(x);
        this.position.setY(y);
    }
}