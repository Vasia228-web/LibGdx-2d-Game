package com.github.vasia228web.tiled;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.vasia228web.GdxGame;
import com.github.vasia228web.asset.AssetService;
import com.github.vasia228web.asset.MapAsset;

import java.util.function.Consumer;


public class TiledService {

    private final AssetService assetService;
    private final World physicWorld;

    private TiledMap currentMap;

    private Consumer<TiledMap> mapChangedConsumer;
    private Consumer<TiledMapTileMapObject> loadObjectsConsumer;
    private LoadTileConsumer loadTileConsumer;

    public TiledService(AssetService assetService,  World physicWorld) {
        this.assetService = assetService;
        this.mapChangedConsumer = null;
        this.loadObjectsConsumer = null;
        this.currentMap = null;
        this.loadTileConsumer = null;
        this.physicWorld = physicWorld;
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
        spawnMapBoundary(tiledMap);

    }

    private void spawnMapBoundary(TiledMap tiledMap) {
        Integer width = tiledMap.getProperties().get("width", 0, Integer.class);
        Integer tileW = tiledMap.getProperties().get("tilewidth", 0, Integer.class);
        Integer height = tiledMap.getProperties().get("height", 0, Integer.class);
        Integer tileH = tiledMap.getProperties().get("tileheight", 0, Integer.class);

        float mapW = width * tileW * GdxGame.UNIT_SCALE;
        float mapH = height * tileH * GdxGame.UNIT_SCALE;
        float halfW = mapW * 0.5f;
        float halfH = mapH * 0.5f;
        float boxThickness = 0.5f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.setZero();
        bodyDef.fixedRotation = true;
        Body body = physicWorld.createBody(bodyDef);

        // left edge
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(boxThickness,halfH, new Vector2(-boxThickness,halfH), 0f);
        body.createFixture(shape,0f).setFriction(0f);
        shape.dispose();

        // right edge
        shape = new PolygonShape();
        shape.setAsBox(boxThickness,halfH, new Vector2(mapW +boxThickness,halfH), 0f);
        body.createFixture(shape,0f).setFriction(0f);
        shape.dispose();

        // bottom edge
        shape = new PolygonShape();
        shape.setAsBox(halfW, boxThickness, new Vector2(halfW,-boxThickness), 0f);
        body.createFixture(shape,0f).setFriction(0f);
        shape.dispose();

        // top edge
        shape = new PolygonShape();
        shape.setAsBox(halfW,boxThickness, new Vector2(halfW,mapH+boxThickness), 0f);
        body.createFixture(shape,0f).setFriction(0f);
        shape.dispose();

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
