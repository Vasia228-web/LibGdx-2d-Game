package com.github.vasia228web.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.maps.tiled.TiledMap;


public enum MapAsset implements Asset<TiledMap> {
    MAIN("main.tmx");

    private final AssetDescriptor<TiledMap>descriptor;

    MapAsset(String mapName) {
        // No loader params here: in gdx 1.14.0 AtlasTmxMapLoader no longer has
        // AtlasTiledMapLoaderParameters, and TmxMapLoader expects its own params type.
        this.descriptor = new AssetDescriptor<>("maps/" + mapName, TiledMap.class);
    }


    @Override
    public AssetDescriptor<TiledMap> getDescriptor() {
        return this.descriptor;
    }
}
