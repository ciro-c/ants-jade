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
  double reward = 0;
  double momentumProbability = 0.8;
  double randomProbaility = 0.1;
  float maxPheromone = 1000.0f;
  final float impossibleBadPheromone = -1.0f;
  final double rewardGain = 1.0;

  int testMapSize = 30;

  protected void setup() {
    System.out.println("Hello ant:" + getName());
    random = new MersenneTwisterFast();
    world = World.getInstance();
    addBehaviour(new TickerBehaviour(this, 10) {
      protected void onTick() {
        // System.out.println("Agent " + myAgent.getLocalName() + ": tick=" +
        // getTickCount());
        // System.out.println("X:" + x + ", Y:" + y);
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

        depositPherommones();

        if (hasFood) {
          if (currentTile.getIsHome()) {
            hasFood = false;
            // System.out.println("Depositando comida");
            return;
          }
          moveAnt();
          return;
        }

        if (currentTile.getIsFood()) {
          hasFood = true;
          // System.out.println("Pegando comida");

          return;
        }

        moveAnt();
      }
    });
  }

  private void moveAnt() {
    float max = impossibleBadPheromone;
    int max_x = x;
    int max_y = y;
    int maxCount = 2;

    if (hasFood) {
      for (int dx = -1; dx < 2; dx++) {
        for (int dy = -1; dy < 2; dy++) {
          int _y = y + dy;
          int _x = x + dx;

          MapTileDTO nextTile = world.getPosition(_x, _y);
          if ((dx == 0 && dy == 0) || nextTile == null || nextTile.getIsBlock()) {
            continue;
          }
          float exploringPheromone = nextTile.getPheromoneExploring();
          if (exploringPheromone > max) {
            maxCount = 2;
          }

          if (exploringPheromone > max || (exploringPheromone == max && random.nextBoolean(1 / maxCount++))) {
            max = exploringPheromone;
            max_x = _x;
            max_y = _y;
          }
        }
      }

      if (max == 0 && lastX != x && lastY != y) {
        if (random.nextBoolean(momentumProbability)) {
          int xm = x + (x - lastX);
          int ym = y + (y - lastY);

          if (xm >= 0 && ym >= 0 && world.getPosition(xm, ym) != null) {
            max_x = xm;
            max_y = ym;
          }
        }
      } else if (random.nextBoolean(randomProbaility)) {
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
      if (world.getPosition(max_x, max_y).getIsHome()) {
        reward = rewardGain;
        hasFood = false;
      }
      return;
    }

    for (int dx = -1; dx < 2; dx++) {
      for (int dy = -1; dy < 2; dy++) {
        int _y = y + dy;
        int _x = x + dx;

        MapTileDTO nextTile = world.getPosition(_x, _y);
        if ((dx == 0 && dy == 0) || nextTile == null || nextTile.getIsBlock()) {
          continue;
        }
        float foodPheromone = nextTile.getPheromoneFoundFood();
        if (foodPheromone > max) {
          maxCount = 2;
        }

        if (foodPheromone > max || (foodPheromone == max && random.nextBoolean(1 / maxCount++))) {
          max = foodPheromone;
          max_x = _x;
          max_y = _y;
        }
      }
    }

    if (max == 0 && lastX != x && lastY != y) {
      if (random.nextBoolean(momentumProbability)) {
        int xm = x + (x - lastX);
        int ym = y + (y - lastY);

        if (xm >= 0 && ym >= 0 && world.getPosition(xm, ym) != null) {
          max_x = xm;
          max_y = ym;
        }
      }
    } else if (random.nextBoolean(randomProbaility)) {
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
    // System.out.println("X:" + x + ", Y:" + y);
    // System.out.println("Max X:" + max_x + ", Max Y:" + max_y);
    x = max_x;
    y = max_y;
    if (world.getPosition(max_x, max_y).getIsFood()) {
      reward = rewardGain;
      hasFood = true;
    }
  }

  private void depositPherommones() {
    if (hasFood) {
      float pheromoneFoundFood = world.getPosition(x, y).getPheromoneFoundFood();
      if (pheromoneFoundFood >= maxPheromone) {
        return;
      }
    } else {
      float pheromoneExploring = world.getPosition(x, y).getPheromoneExploring();
      if (pheromoneExploring >= maxPheromone) {
        return;
      }
    }
    world.putPheromones(x, y, hasFood);
  }

}
