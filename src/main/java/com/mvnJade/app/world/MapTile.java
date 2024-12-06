package com.mvnJade.app.world;

import com.mvnJade.app.enums.TileType;

import javafx.scene.layout.StackPane;

public class MapTile extends StackPane {

    // == constants ==
    public static final double ITEM_HEIGHT = 50.0d;
    public static final double ITEM_WIDTH = 50.0d;

    private float intensity = 1.0f;
    private TileType tileType = TileType.EMPTY;

    public MapTile(TileType tileType, float intensity) {
        this.tileType = tileType;
        this.intensity = intensity;
        init();
    }

    private void init() {
        setMinSize(ITEM_WIDTH, ITEM_HEIGHT);
        setMaxSize(ITEM_WIDTH, ITEM_HEIGHT);
        updateStyle();
    }

    private void updateStyle() {
        setStyle(tileType.color);
        // setStyle(tileType.color+"-fx-opacity:"+intensity);
    }

    public void updateStyle(TileType tileType, float intensity) {
        this.tileType = tileType;
        this.intensity = intensity;
        updateStyle();
    }
}