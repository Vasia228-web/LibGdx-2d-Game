package com.github.vasia228web.tiled;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.vasia228web.asset.AssetService;
import com.github.vasia228web.asset.MapAsset;

import java.util.function.Consumer;


public class TiledService {

    private final AssetService assetService;

    private TiledMap currentMap;

    private Consumer<TiledMap> mapChangedConsumer;
    private Consumer<TiledMapTileMapObject> loadObjectsConsumer;
    private LoadTileConsumer loadTileConsumer;

    public TiledService(AssetService assetService) {
        this.assetService = assetService;
        this.mapChangedConsumer = null;
        this.loadObjectsConsumer = null;
        this.currentMap = null;
        this.loadTileConsumer = null;
    }

    public TiledMap loadMap(MapAsset mapAsset) {
        TiledMap tiledMap = this.assetService.load(mapAsset);
        tiledMap.getProperties().put("mapAsset", mapAsset);
        return tiledMap;
    }


    public void setMap(TiledMap map) {
        if(this.currentMap != null) {
            this.assetService.unload(this.currentMap.getProperties().get("mapAsset", MapAsset.class));
        }

        this.currentMap = map;
        loadMapObject(map);
        if(this.mapChangedConsumer != null) {
            this.mapChangedConsumer.accept(map);
        }
    }

    private void loadMapObject(TiledMap tiledMap) {
        for(MapLayer layer : tiledMap.getLayers()){
            if("Objects".equals(layer.getName())){
                loadObjectLayer(layer);
            }else if(layer instanceof TiledMapTileLayer tileLayer){
                loadTileLayer(tileLayer);
            }
        }
    }

    private void loadTileLayer(TiledMapTileLayer tileLayer) {
        if(loadTileConsumer == null) return;
        for(int y = 0; y < tileLayer.getHeight(); y++){
            for(int x = 0; x < tileLayer.getWidth(); x++){
                TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                if(cell == null)continue;

                loadTileConsumer.accept(cell.getTile(),x,y);
            }
        }
    }


    private void loadObjectLayer(MapLayer objectLayer) {
        if(loadObjectsConsumer == null) return;
        for(MapObject mapObject : objectLayer.getObjects()){
            if(mapObject instanceof TiledMapTileMapObject tileMapObject){
                loadObjectsConsumer.accept(tileMapObject);
            }
            else{
                throw new GdxRuntimeException("Unsupported MapObject type: " + mapObject.getClass().getSimpleName());
            }
        }
    }

    public void setMapChangeConsumer(Consumer<TiledMap> mapChangedConsumer) {
        this.mapChangedConsumer = mapChangedConsumer;
    }

    public void setLoadTileConsumer(LoadTileConsumer loadTileConsumer) {
        this.loadTileConsumer = loadTileConsumer;
    }
    @FunctionalInterface
    public interface LoadTileConsumer {
        void accept(TiledMapTile tile, float x, float y);
    }


    public void setLoadObjectsConsumer(Consumer<TiledMapTileMapObject> loadObjectsConsumer) {
        this.loadObjectsConsumer = loadObjectsConsumer;
    }
}
