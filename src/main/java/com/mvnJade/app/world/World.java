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

  public float PHEROMONE_EVAPORATION_RATE = 0.9f;

  MapTileDTO map[][];
  MapTile mapTiles[][];
  int mapSize;
  int stepsPerClock = 1;
  int internalStep = 0;
  float pheromoneDeposit = 500.0f;
  TilePane worldGrid = new TilePane();
  Stage stage = new Stage();


  private World(int mapSize ) {
    this.mapSize = mapSize;
    this.map = new MapTileDTO[mapSize][mapSize];
    this.mapTiles = new MapTile[mapSize][mapSize];
    for (int i = 0; i < mapSize; i++) {
      for (int j = 0; j < mapSize; j++) {
        this.map[i][j] = new MapTileDTO();
        this.mapTiles[i][j] = new MapTile(TileType.EMPTY, 1.0f);
      }
    }

    MapTileDTO homeSpot = new MapTileDTO(true,false,false);
    MapTileDTO foodSpot = new MapTileDTO(false,false,true);
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

  public void putPheromones(Integer x, Integer y, boolean hasFood) {
    if (x < 0 || y < 0) {
      return;
    }

    if (x >= mapSize || y >= mapSize) {
      return;
    }
    if (hasFood) {
      float feromoneLevel = this.map[x][y].getPheromoneFoundFood(); 
      this.map[x][y].setPheromoneFoundFood(feromoneLevel+pheromoneDeposit);
    } else {
      float feromoneLevel = this.map[x][y].getPheromoneExploring(); 
      this.map[x][y].setPheromoneExploring(feromoneLevel+pheromoneDeposit);
    }
  }

  public boolean getRunning() {
    return this.running;
  }

  public void play() {
    exec.scheduleAtFixedRate(new Runnable(){
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
    ArrayList<Rectangle> toAdd = new ArrayList();
    for (int i = 0; i < mapSize; i++) {
      for (int j = 0; j < mapSize; j++) {
        float pheromoneExploring = map[i][j].getPheromoneExploring();
        float pheromoneFoundFood = map[i][j].getPheromoneFoundFood();
        if (map[i][j].getIsBlock()) {
          continue;
        }
        pheromoneExploring = this.PHEROMONE_EVAPORATION_RATE * pheromoneExploring;
        pheromoneFoundFood = this.PHEROMONE_EVAPORATION_RATE * pheromoneFoundFood;
        map[i][j].setPheromoneExploring(pheromoneExploring);
        map[i][j].setPheromoneFoundFood(pheromoneFoundFood);
        if (map[i][j].getIsFood() || map[i][j].getIsHome()) {
          continue;
        }
        if (pheromoneFoundFood > pheromoneExploring) {
          toAdd.add(new Rectangle(50, 50, Color.GREEN));
          mapTiles[i][j].updateStyle(TileType.PHEROMONE_FOUND_FOOD, pheromoneFoundFood/1000.0f);
        } else if (pheromoneExploring > 0) {
          toAdd.add(new Rectangle(50, 50, Color.BLUE));
          mapTiles[i][j].updateStyle(TileType.PHEROMONE_EXPLORING, pheromoneExploring/1000.0f);
        } else {
          toAdd.add(new Rectangle(50, 50, Color.RED));
          mapTiles[i][j].updateStyle(TileType.EMPTY, 1.0f);
        }
      }
    }
  }

  public int getStepsPerClock() {
    return stepsPerClock;
  }

}
