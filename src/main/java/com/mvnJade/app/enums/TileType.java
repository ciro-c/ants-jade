package com.mvnJade.app.enums;

public enum TileType {
  HOME("-fx-background-color: red"),
  BLOCK("-fx-background-color: black"),
  FOOD("-fx-background-color: green"),
  PHEROMONE_EXPLORING("-fx-background-color: yellow"),
  PHEROMONE_FOUND_FOOD("-fx-background-color: blue"),
  EMPTY("-fx-background-color: white");

  public final String color;

  private TileType(String color) {
    this.color = color;
  }
}
