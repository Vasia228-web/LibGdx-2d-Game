package com.github.vasia228web.asset;

import com.badlogic.gdx.assets.AssetDescriptor;

public interface Asset <T>{
    AssetDescriptor<T> getDescriptor();
}
