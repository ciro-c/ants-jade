package com.mvnJade.app.world;

import com.mvnJade.app.dto.MapTileDTO;

public final class World {
  private static volatile World instance;
  private boolean isSimulationRunning = false;
  public static int MIN_MAP_SIZE = 3;
  public static int DEFAULT_MAP_SIZE = 30;

  public static final float IMPOSSIBLY_BAD_PHEROMONE = -1.0f;
  public static final float LIKELY_MAX_PHEROMONE = 3.0f;
  public static final int HOME = 1;
  public static final int FOOD = 2;

  public float PHEROMONE_EVAPORATION_RATE = 0.999f;
  public float reward = 1.0f;
  public float updateCutDown = 0.9f;
  public float diagonalCutDown;

  MapTileDTO map[][];
  int mapSize;
  int stepsPerClock = 1;
  int internalStep = 0;
  float pheromoneDeposit = 1.0f;

  private World(int mapSize) {
    this.mapSize = mapSize;
    this.map = new MapTileDTO[mapSize][mapSize];
    this.diagonalCutDown = (float) Math.pow(updateCutDown, Math.sqrt(2));

    for (int i = 0; i < mapSize; i++) {
      for (int j = 0; j < mapSize; j++) {
        this.map[i][j] = new MapTileDTO();
      }
    }

    MapTileDTO homeSpot = new MapTileDTO(true, false, false);
    MapTileDTO foodSpot = new MapTileDTO(false, false, true);
    this.map[0][0] = homeSpot;
    int foodPosition = mapSize - (mapSize * 3 / 4); // Posiciona a ~50% do mapa
    System.out.println(foodPosition);
    this.map[foodPosition][foodPosition] = foodSpot;
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

  public boolean getSimulationRunning() {
    return this.isSimulationRunning;
  }

  public void setRunning(boolean running) {
    this.isSimulationRunning = running;
  }

  public void updateWorld() {
    if (isSimulationRunning == false) {
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
      }
    }
  }

  public int getStepsPerClock() {
    return stepsPerClock;
  }

  public void putPheromones(Integer x, Integer y, boolean hasFood) {
    if (x < 0 || y < 0 || x >= mapSize || y >= mapSize) {
      return; // Garante que não vamos acessar uma posição fora do mapa
    }

    if (hasFood) {
      float feromoneLevel = this.map[x][y].getPheromoneFoundFood();
      // Adiciona o valor de depósito ao nível existente
      this.map[x][y].setPheromoneFoundFood(feromoneLevel + pheromoneDeposit);
    } else {
      float feromoneLevel = this.map[x][y].getPheromoneExploring();
      // Adiciona o valor de depósito ao nível existente
      this.map[x][y].setPheromoneExploring(feromoneLevel + pheromoneDeposit);
    }
  }

  /**
   * Sets the number of steps per clock cycle.
   * Higher values make the simulation run faster.
   * 
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
