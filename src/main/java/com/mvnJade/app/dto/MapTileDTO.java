package com.mvnJade.app.dto;

public class MapTileDTO {
  int pheromoneExploring;
  int pheromoneFoundFood;
  boolean isHome;
  boolean isBlock;
  boolean isFood;

  public MapTileDTO() {
    this.pheromoneExploring = 0;
    this.pheromoneFoundFood = 0;
    this.isHome = false;
    this.isBlock = false;
    this.isFood = false;
  }

  public MapTileDTO(int pheromoneExploring, int pheromoneFoundFood) {
    this.pheromoneExploring = pheromoneExploring;
    this.pheromoneFoundFood = pheromoneFoundFood;
    this.isHome = false;
    this.isBlock = false;
    this.isFood = false;
  }

  public MapTileDTO(boolean isHome, boolean isBlock, boolean isFood) {
    this.pheromoneExploring = 0;
    this.pheromoneFoundFood = 0;
    this.isHome = isHome;
    this.isBlock = isBlock;
    this.isFood = isFood;
  }

  public int getPheromoneExploring() {
    return this.pheromoneExploring;
  }

  public int getPheromoneFoundFood() {
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

  public void setPheromoneExploring(int pheromoneExploring) {
    this.pheromoneExploring = pheromoneExploring;
  }

  public void setPheromoneFoundFood(int pheromoneFoundFood) {
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
