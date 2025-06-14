package com.mvnJade.app.agents;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.mvnJade.app.MersenneTwisterFast;
import com.mvnJade.app.dto.MapTileDTO;
import com.mvnJade.app.world.World;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class Ant extends Agent {
  protected Map<String, Object> spaces;

  private World world;
  private int internalStep = 0;
  private MersenneTwisterFast random;

  int x = 0, y = 0, lastX = 0, lastY = 0;
  boolean hasFood = false;
  float reward = 0;
  double momentumProbability = 0.8;
  double randomProbability = 0.1;
  float maxPheromone = 1000.0f;
  final float impossibleBadPheromone = World.IMPOSSIBLY_BAD_PHEROMONE;
  float rewardGain;
  float updateCutDown;
  float diagonalCutDown;

  int testMapSize = 30;
  
  public Ant() {
    this.rewardGain = 1.0f;
  }

  protected void setup() {
    System.out.println("Hello ant:" + getName());
    random = new MersenneTwisterFast();
    world = World.getInstance();
    updateCutDown = world.getUpdateCutDown();
    diagonalCutDown = world.getDiagonalCutDown();
    rewardGain = world.getReward();
    
    addBehaviour(new TickerBehaviour(this, 10) {
      protected void onTick() {
        if (!world.getRunning()) {
          return;
        }

        if (internalStep <= world.getStepsPerClock()) {
          internalStep++;
          return;
        }
        internalStep = 0;
        MapTileDTO currentTile = world.getPosition(x, y);
        if (currentTile == null || currentTile.getIsBlock()) {
          System.out.println("Bye");
          myAgent.doDelete();
          return;
        }

        depositPheromones();
        moveAnt();
      }
    });
  }

  private void depositPheromones() {
    MapTileDTO currentTile = world.getPosition(x, y);
    
    if (hasFood) { // deposit food pheromone
      float max = currentTile.getPheromoneFoundFood();
      for (int dx = -1; dx < 2; dx++) {
        for (int dy = -1; dy < 2; dy++) {
          int _x = dx + x;
          int _y = dy + y;
          MapTileDTO neighborTile = world.getPosition(_x, _y);
          if (neighborTile == null) continue; // nothing to see here
          
          float m = neighborTile.getPheromoneFoundFood() * 
              (dx * dy != 0 ? // diagonal corners
              diagonalCutDown : updateCutDown) + reward;
          if (m > max) max = m;
        }
      }
      currentTile.setPheromoneFoundFood(max);
    } else {
      float max = currentTile.getPheromoneExploring();
      for (int dx = -1; dx < 2; dx++) {
        for (int dy = -1; dy < 2; dy++) {
          int _x = dx + x;
          int _y = dy + y;
          MapTileDTO neighborTile = world.getPosition(_x, _y);
          if (neighborTile == null) continue; // nothing to see here
          
          float m = neighborTile.getPheromoneExploring() * 
              (dx * dy != 0 ? // diagonal corners
              diagonalCutDown : updateCutDown) + reward;
          if (m > max) max = m;
        }
      }
      currentTile.setPheromoneExploring(max);
    }
    reward = 0.0f;
  }

  private void moveAnt() {
    float max = impossibleBadPheromone;
    int max_x = x;
    int max_y = y;
    int count = 2;
    MapTileDTO currentTile = world.getPosition(x, y);
    currentTile.removeAnt();
    
    if (hasFood) { // follow home pheromone
      for (int dx = -1; dx < 2; dx++) {
        for (int dy = -1; dy < 2; dy++) {
          int _x = dx + x;
          int _y = dy + y;
          
          MapTileDTO nextTile = world.getPosition(_x, _y);
          if ((dx == 0 && dy == 0) || nextTile == null || nextTile.getIsBlock()) {
            continue;
          }
          
          float exploringPheromone = nextTile.getPheromoneExploring();
          if (exploringPheromone > max) {
            count = 2;
          }

          if (exploringPheromone > max || (exploringPheromone == max && random.nextBoolean(1.0 / count++))) {
            max = exploringPheromone;
            max_x = _x;
            max_y = _y;
          }
        }
      }

      if (max == 0 && lastX != x && lastY != y) { // nowhere to go! Maybe go straight
        if (random.nextBoolean(momentumProbability)) {
          int xm = x + (x - lastX);
          int ym = y + (y - lastY);
          MapTileDTO nextPos = world.getPosition(xm, ym);
          if (xm >= 0 && ym >= 0 && nextPos != null && !nextPos.getIsBlock()) {
            max_x = xm;
            max_y = ym;
          }
        }
      } else if (random.nextBoolean(randomProbability)) { // Maybe go randomly
        int xd = (random.nextInt(3) - 1);
        int yd = (random.nextInt(3) - 1);
        int xm = x + xd;
        int ym = y + yd;
        MapTileDTO newPosition = world.getPosition(xm, ym);
        if (!(xd == 0 && yd == 0) && xm >= 0 && ym >= 0 && newPosition != null && !newPosition.getIsBlock()) {
          max_x = xm;
          max_y = ym;
        }
      }

      lastX = x;
      lastY = y;
      x = max_x;
      y = max_y;
      
      MapTileDTO newPosition = world.getPosition(max_x, max_y);
      newPosition.addAnt();
      if (newPosition != null && newPosition.getIsHome()) { // reward me next time! And change my status
        reward = rewardGain;
        hasFood = !hasFood;
      }
    } else { // looking for food, follow food pheromone
      for (int dx = -1; dx < 2; dx++) {
        for (int dy = -1; dy < 2; dy++) {
          int _x = dx + x;
          int _y = dy + y;

          MapTileDTO nextTile = world.getPosition(_x, _y);
          if ((dx == 0 && dy == 0) || nextTile == null || nextTile.getIsBlock()) {
            continue;
          }
          
          float foodPheromone = nextTile.getPheromoneFoundFood();
          if (foodPheromone > max) {
            count = 2;
          }

          if (foodPheromone > max || (foodPheromone == max && random.nextBoolean(1.0 / count++))) {
            max = foodPheromone;
            max_x = _x;
            max_y = _y;
          }
        }
      }

      if (max == 0 && lastX != x && lastY != y) { // nowhere to go! Maybe go straight
        if (random.nextBoolean(momentumProbability)) {
          int xm = x + (x - lastX);
          int ym = y + (y - lastY);
          MapTileDTO nextPos = world.getPosition(xm, ym);
          if (xm >= 0 && ym >= 0 && nextPos != null && !nextPos.getIsBlock()) {
            max_x = xm;
            max_y = ym;
          }
        }
      } else if (random.nextBoolean(randomProbability)) { // Maybe go randomly
        int xd = (random.nextInt(3) - 1);
        int yd = (random.nextInt(3) - 1);
        int xm = x + xd;
        int ym = y + yd;
        MapTileDTO newPosition = world.getPosition(xm, ym);
        if (!(xd == 0 && yd == 0) && xm >= 0 && ym >= 0 && newPosition != null && !newPosition.getIsBlock()) {
          max_x = xm;
          max_y = ym;
        }
      }
      
      lastX = x;
      lastY = y;
      x = max_x;
      y = max_y;
      
      MapTileDTO newPosition = world.getPosition(max_x, max_y);
      newPosition.addAnt();
      if (newPosition != null && newPosition.getIsFood()) { // reward me next time! And change my status
        reward = rewardGain;
        hasFood = !hasFood;
      }
    }
  }
}
