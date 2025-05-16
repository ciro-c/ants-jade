package com.mvnJade.app.dto;

public class MapTileDTO {
  float pheromoneExploring;
  float pheromoneFoundFood;
  boolean isHome;
  boolean isBlock;
  boolean isFood;

  public MapTileDTO() {
    this.pheromoneExploring = 0.0f;
    this.pheromoneFoundFood = 0.0f;
    this.isHome = false;
    this.isBlock = false;
    this.isFood = false;
  }

  public MapTileDTO(float pheromoneExploring, float pheromoneFoundFood) {
    this.pheromoneExploring = pheromoneExploring;
    this.pheromoneFoundFood = pheromoneFoundFood;
    this.isHome = false;
    this.isBlock = false;
    this.isFood = false;
  }

  public MapTileDTO(boolean isHome, boolean isBlock, boolean isFood) {
    this.pheromoneExploring = 0.0f;
    this.pheromoneFoundFood = 0.0f;
    this.isHome = isHome;
    this.isBlock = isBlock;
    this.isFood = isFood;
  }

  public float getPheromoneExploring() {
    return this.pheromoneExploring;
  }

  public float getPheromoneFoundFood() {
    return this.pheromoneFoundFood;
  }

  public boolean getIsHome() {
    return this.isHome;
  }

  public boolean getIsBlock() {
    return this.isBlock;
  }

  public boolean getIsFood() {
    return this.isFood;
  }

  public void setPheromoneExploring(float pheromoneExploring) {
    this.pheromoneExploring = pheromoneExploring;
  }

  public void setPheromoneFoundFood(float pheromoneFoundFood) {
    this.pheromoneFoundFood = pheromoneFoundFood;
  }

  public void setIsHome(boolean isHome) {
    this.isHome = isHome;
  }

  public void setIsBlock(boolean isBlock) {
    this.isBlock = isBlock;
  }

  public void setIsFood(boolean isFood) {
    this.isFood = isFood;
  }

}
