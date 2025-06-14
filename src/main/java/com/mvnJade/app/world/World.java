package com.mvnJade.app.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mvnJade.app.dto.MapTileDTO;
import com.mvnJade.app.enums.TileType;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.Scene;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public final class World{
  private static volatile World instance;
  private boolean running = false;
  private ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
  public static int MIN_MAP_SIZE = 3;
  public static int DEFAULT_MAP_SIZE = 3;
  private final int UPDATE_TIMER = 10;

  public static final float IMPOSSIBLY_BAD_PHEROMONE = -1.0f;
  public static final float LIKELY_MAX_PHEROMONE = 3.0f;
  public static final int HOME = 1;
  public static final int FOOD = 2;
  
  public float PHEROMONE_EVAPORATION_RATE = 0.999f;
  public float reward = 1.0f;
  public float updateCutDown = 0.9f;
  public float diagonalCutDown;

  MapTileDTO map[][];
  MapTile mapTiles[][];
  int mapSize;
  int stepsPerClock = 1;
  int internalStep = 0;
  float pheromoneDeposit = 1.0f;
  TilePane worldGrid = new TilePane();
  Stage stage = new Stage();


  private World(int mapSize) {
    this.mapSize = mapSize;
    this.map = new MapTileDTO[mapSize][mapSize];
    this.mapTiles = new MapTile[mapSize][mapSize];
    this.diagonalCutDown = (float)Math.pow(updateCutDown, Math.sqrt(2));
    
    for (int i = 0; i < mapSize; i++) {
      for (int j = 0; j < mapSize; j++) {
        this.map[i][j] = new MapTileDTO();
        this.mapTiles[i][j] = new MapTile(TileType.EMPTY, 1.0f);
      }
    }

    MapTileDTO homeSpot = new MapTileDTO(true, false, false);
    MapTileDTO foodSpot = new MapTileDTO(false, false, true);
    this.map[0][0] = homeSpot;
    this.mapTiles[0][0] = new MapTile(TileType.HOME, 1.0f);
    this.map[20][20] = foodSpot; 
    this.mapTiles[20][20] = new MapTile(TileType.FOOD, 1.0f);
    this.worldGrid = new WorldGrid(this.mapTiles, mapSize);
    stage.setScene(new Scene(worldGrid));
    stage.show();
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

  public float getDiagonalCutDown() {
    return diagonalCutDown;
  }
  
  public float getUpdateCutDown() {
    return updateCutDown;
  }
  
  public float getReward() {
    return reward;
  }

  public void putPheromones(Integer x, Integer y, boolean hasFood) {
    if (x < 0 || y < 0) {
      return;
    }

    if (x >= mapSize || y >= mapSize) {
      return;
    }
    if (hasFood) {
      float feromoneLevel = this.map[x][y].getPheromoneFoundFood(); 
      this.map[x][y].setPheromoneFoundFood(feromoneLevel + pheromoneDeposit);
    } else {
      float feromoneLevel = this.map[x][y].getPheromoneExploring(); 
      this.map[x][y].setPheromoneExploring(feromoneLevel + pheromoneDeposit);
    }
  }

  public boolean getRunning() {
    return this.running;
  }

  public void play() {
    exec.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        updateWorld();
      }
    }, 0, UPDATE_TIMER, TimeUnit.MILLISECONDS);
    this.running = true;
  }

  public void stop() {
    exec.shutdown();
    this.running = false;
  }

  public void updateWorld() {
    if (running == false) {
      return;
    }
    if (internalStep <= stepsPerClock) {
      internalStep++;
      return;
    }
    internalStep = 0;
    
    // Evaporate pheromones - similar to antsforage approach
    for (int i = 0; i < mapSize; i++) {
      for (int j = 0; j < mapSize; j++) {
        float pheromoneExploring = map[i][j].getPheromoneExploring();
        float pheromoneFoundFood = map[i][j].getPheromoneFoundFood();
        int antsNumber = map[i][j].getAnts();
        
        if (map[i][j].getIsBlock()) {
          continue;
        }
        
        // Apply evaporation
        pheromoneExploring *= PHEROMONE_EVAPORATION_RATE;
        pheromoneFoundFood *= PHEROMONE_EVAPORATION_RATE;
        
        map[i][j].setPheromoneExploring(pheromoneExploring);
        map[i][j].setPheromoneFoundFood(pheromoneFoundFood);
        
        // Update tile visualization
        if (map[i][j].getIsFood() || map[i][j].getIsHome()) {
          continue;
        }
        
        if (pheromoneFoundFood > pheromoneExploring) {
          mapTiles[i][j].updateStyle(TileType.PHEROMONE_FOUND_FOOD, 
              Math.min(pheromoneFoundFood / LIKELY_MAX_PHEROMONE, 1.0f));
        } else if (pheromoneExploring > 0) {
          mapTiles[i][j].updateStyle(TileType.PHEROMONE_EXPLORING, 
              Math.min(pheromoneExploring / LIKELY_MAX_PHEROMONE, 1.0f));
        } else if (antsNumber > 0) {
          mapTiles[i][j].updateStyle(TileType.ANT, 1.0f);
        } else {
          mapTiles[i][j].updateStyle(TileType.EMPTY, 1.0f);
        }
      }
    }
  }

  public int getStepsPerClock() {
    return stepsPerClock;
  }
  
  /**
   * Sets the number of steps per clock cycle.
   * Higher values make the simulation run faster.
   * @param steps - number of steps per clock (1-20)
   */
  public void setStepsPerClock(int steps) {
    if (steps > 0) {
      this.stepsPerClock = steps;
      // Reset internal step counter to prevent inconsistent timing
      this.internalStep = 0;
    }
  }
}
