package com.mvnJade.app.world;

import com.mvnJade.app.dto.MapTileDTO;

public final class World {
  private static volatile World instance;

  public static int MIN_MAP_SIZE = 3;
  public static int DEFAULT_MAP_SIZE = 3;

  MapTileDTO map[][];
  int mapSize;

  private World(int mapSize ) {
    this.mapSize = mapSize;
    this.map = new MapTileDTO[mapSize][mapSize];
    MapTileDTO homeSpot = new MapTileDTO(true,false,false);
    MapTileDTO foodSpot = new MapTileDTO(false,false,true);
    this.map[0][0] = homeSpot;
    this.map[2][2] = foodSpot; 
  }

  public static World getInstance() {
    return getInstance(DEFAULT_MAP_SIZE);
  }

  public static World getInstance(int mapSize) {
    World result = instance;
    if (result != null) {
      return result;
    }
    synchronized (World.class) {
      if (instance == null) {
        instance = new World(mapSize);
      }
      return instance;
    }
  }

  public int getMapSize() {
    return mapSize;
  }

  public MapTileDTO getPosition(Integer x, Integer y) {
    if (x < 0 || y < 0) {
      return null;
    }

    if (x >= mapSize || y >= mapSize) {
      return null;
    }

    return this.map[x][y];
  }

  public void putPheromones(Integer x, Integer y, boolean hasFood) {
    if (x < 0 || y < 0) {
      return;
    }

    if (x >= mapSize || y >= mapSize) {
      return;
    }
    if (hasFood) {
      int feromoneLevel = this.map[x][y].getPheromoneFoundFood(); 
      this.map[x][y].setPheromoneFoundFood(feromoneLevel+1);
    } else {
      int feromoneLevel = this.map[x][y].getPheromoneExploring(); 
      this.map[x][y].setPheromoneExploring(feromoneLevel+1);
    }
  }



}
