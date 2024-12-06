package com.mvnJade.app.world;

import java.util.*;

import javafx.geometry.Pos;
import javafx.scene.layout.TilePane;

public class WorldGrid  extends TilePane {
  private MapTile[][] mapTile;
  private int size = 3; 

  WorldGrid(MapTile[][] mapTile, Integer size) {
    this.mapTile = mapTile;
    if (size != null) {
      this.size = size;      
    }
    init();
  }

  private void init() {
    setPrefColumns(this.size);
    setPrefRows(this.size);
    setTileAlignment(Pos.CENTER);
    List<MapTile> toAdd = new ArrayList<>();
    for (int i = 0; i < mapTile.length; i++) {
      for (int j = 0; j < mapTile[i].length; j++) {
        MapTile tile = this.mapTile[i][j];
        toAdd.add(tile);
      }
    }
    getChildren().clear();     
    getChildren().setAll(toAdd);
  }

}
