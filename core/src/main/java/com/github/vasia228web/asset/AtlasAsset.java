package com.github.vasia228web.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public enum AtlasAsset implements  Asset<TextureAtlas>{
    OBJECTS("objects.atlas");

    private final AssetDescriptor<TextureAtlas> descriptor;

    AtlasAsset(String atlasName){
        this.descriptor = new AssetDescriptor<>("graphic/"+ atlasName, TextureAtlas.class);
    }

    @Override
    public AssetDescriptor<TextureAtlas> getDescriptor() {
        return descriptor;
    }
}
