package com.github.vasia228web.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;

public class AssetService implements Disposable {
    private final AssetManager assetManager;

    public AssetService(FileHandleResolver fileHandlerResolver){
        this.assetManager = new AssetManager(fileHandlerResolver);
        this.assetManager.setLoader(TiledMap.class, new TmxMapLoader(fileHandlerResolver));
    }

    public <T> T load(Asset<T> asset){
        this.assetManager.load(asset.getDescriptor());
        this.assetManager.finishLoading();
        return this.assetManager.get(asset.getDescriptor());
    }
    public <T> T get(Asset<T> asset){
        return this.assetManager.get(asset.getDescriptor());
    }
    public <T> void queue(Asset<T>asset){
        this.assetManager.load(asset.getDescriptor());
    }

    public <T>void unload(Asset<T> asset) {
        this.assetManager.unload(asset.getDescriptor().fileName);
    }
    public boolean update(){

        return this.assetManager.update();
    }
    public void debugDiagnostic(){
        Gdx.app.debug("AssetService", this.assetManager.getDiagnostics());
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
