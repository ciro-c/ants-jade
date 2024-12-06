package com.mvnJade.app.agents;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.mvnJade.app.dto.MapTileDTO;
import com.mvnJade.app.world.World;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class Ant extends Agent {
  protected Map<String, Object> spaces;

  private World world;

  int x = 0, y = 0, lastX = 0, lastY = 0;
  boolean hasFood = false;
  double reward = 0;
  double momentumProbability = 0.8;
  double randomProbaility = 0.1;
  int maxPheromone = 200;

  int testMapSize = 30;

  protected void setup() {
    // System.out.println("Hello ant:" + getName());
    world = World.getInstance();
    addBehaviour(new TickerBehaviour(this, 10) {
      protected void onTick() {
        // System.out.println("Agent " + myAgent.getLocalName() + ": tick=" + getTickCount());
        // System.out.println("X:" + x + ", Y:" + y);
        if (!world.getRunning()) {
          return;
        }
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
    // System.out.println("Movendo");

    // world.getPosition(x - 1, y + 1);
    if (x == 0 && y == 0) {
      x = 1;
      return ;
    } else if (x == testMapSize-1 && y == testMapSize-1) {
      x = 0;
      y = 0;
      return ;
    } else if (y == testMapSize-1 && x != testMapSize-1) {
      x++;
      return ;
      
    } else if (x == testMapSize-1) {
      y++;
      x=0;
      return ;
    } 
    
    x++;
    return;

    // if (hasFood) {
      
    // } else {

    // }

    // world.getPosition(x - 1, y + 1);

        // Mason example                
        // Int2D location = af.buggrid.getObjectLocation(this);
                
        // if (hasFoodItem)  // follow home pheromone
        //     {
        //     double max = AntsForage.IMPOSSIBLY_BAD_PHEROMONE;
        //     int max_x = x;
        //     int max_y = y;
        //     int count = 2;
        //     for(int dx = -1; dx < 2; dx++)
        //         for(int dy = -1; dy < 2; dy++)
        //             {
        //             int _x = dx+x;
        //             int _y = dy+y;
        //             // Pula se for bloco
        //             if ((dx == 0 && dy == 0) || _x < 0 || _y < 0 || _x >= world.getMapSize() || _y >= world.getMapSize() || world.getPosition(_x, _y).getIsBlock()) continue;
        //             double m = af.toHomeGrid.field[_x][_y];
        //             // if (m > max) {
        //             //     count = 2;
        //             // }
        //             // no else, yes m > max is repeated
        //             if (m > max || (m == max && state.random.nextBoolean(1.0 / count++)))  {
        //               max = m;
        //                 max_x = _x;
        //                 max_y = _y;
        //                 }
        //             }
        //     if (max == 0 && last != null)  // nowhere to go!  Maybe go straight
        //         {
        //         if (state.random.nextBoolean(af.momentumProbability))
        //             {
        //             int xm = x + (x - last.x);
        //             int ym = y + (y - last.y);
        //             if (xm >= 0 && xm < AntsForage.GRID_WIDTH && ym >= 0 && ym < AntsForage.GRID_HEIGHT && af.obstacles.field[xm][ym] == 0)
        //                 { max_x = xm; max_y = ym; }
        //             }
        //         }
        //     else if (state.random.nextBoolean(af.randomActionProbability))  // Maybe go randomly
        //         {
        //         int xd = (state.random.nextInt(3) - 1);
        //         int yd = (state.random.nextInt(3) - 1);
        //         int xm = x + xd;
        //         int ym = y + yd;
        //         if (!(xd == 0 && yd == 0) && xm >= 0 && xm < AntsForage.GRID_WIDTH && ym >= 0 && ym < AntsForage.GRID_HEIGHT && af.obstacles.field[xm][ym] == 0)
        //             { max_x = xm; max_y = ym; }
        //         }
        //     af.buggrid.setObjectLocation(this, new Int2D(max_x, max_y));
        //     if (af.sites.field[max_x][max_y] == AntsForage.HOME)  // reward me next time!  And change my status
        //         { reward = af.reward ; hasFoodItem = ! hasFoodItem; }
        //     }
        // else
        //     {
        //     double max = AntsForage.IMPOSSIBLY_BAD_PHEROMONE;
        //     int max_x = x;
        //     int max_y = y;
        //     int count = 2;
        //     for(int dx = -1; dx < 2; dx++)
        //         for(int dy = -1; dy < 2; dy++)
        //             {
        //             int _x = dx+x;
        //             int _y = dy+y;
        //             if ((dx == 0 && dy == 0) ||
        //                 _x < 0 || _y < 0 ||
        //                 _x >= AntsForage.GRID_WIDTH || _y >= AntsForage.GRID_HEIGHT || 
        //                 af.obstacles.field[_x][_y] == 1) continue;  // nothing to see here
        //             double m = af.toFoodGrid.field[_x][_y];
        //             if (m > max)
        //                 {
        //                 count = 2;
        //                 }
        //             // no else, yes m > max is repeated
        //             if (m > max || (m == max && state.random.nextBoolean(1.0 / count++)))  // this little magic makes all "==" situations equally likely
        //                 {
        //                 max = m;
        //                 max_x = _x;
        //                 max_y = _y;
        //                 }
        //             }
        //     if (max == 0 && last != null)  // nowhere to go!  Maybe go straight
        //         {
        //         if (state.random.nextBoolean(af.momentumProbability))
        //             {    this.map[0][0].;

        //             int xm = x + (x - last.x);
        //             int ym = y + (y - last.y);
        //             if (xm >= 0 && xm < AntsForage.GRID_WIDTH && ym >= 0 && ym < AntsForage.GRID_HEIGHT && af.obstacles.field[xm][ym] == 0)
        //                 { max_x = xm; max_y = ym; }
        //             }
        //         }
        //     else if (state.random.nextBoolean(af.randomActionProbability))  // Maybe go randomly
        //         {
        //         int xd = (state.random.nextInt(3) - 1);
        //         int yd = (state.random.nextInt(3) - 1);
        //         int xm = x + xd;
        //         int ym = y + yd;
        //         if (!(xd == 0 && yd == 0) && xm >= 0 && xm < AntsForage.GRID_WIDTH && ym >= 0 && ym < AntsForage.GRID_HEIGHT && af.obstacles.field[xm][ym] == 0)
        //             { max_x = xm; max_y = ym; }
        //         }
        //     af.buggrid.setObjectLocation(this, new Int2D(max_x, max_y));
        //     if (af.sites.field[max_x][max_y] == AntsForage.FOOD)  // reward me next time!  And change my status
        //         { reward = af.reward; hasFoodItem = ! hasFoodItem; }
        //     }
        // last = location;


  }

  private void depositPherommones() {
    if (hasFood) {
      int pheromoneFoundFood = world.getPosition(x, y).getPheromoneFoundFood();
      if (pheromoneFoundFood >= maxPheromone) {
        return;
      }
    } else {
      int pheromoneExploring = world.getPosition(x, y).getPheromoneExploring();
      if (pheromoneExploring >= maxPheromone) {
        return;
      }
    }
    world.putPheromones(x, y, hasFood);
  }

}
