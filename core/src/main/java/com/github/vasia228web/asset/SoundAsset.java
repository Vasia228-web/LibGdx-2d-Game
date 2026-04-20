package com.github.vasia228web.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;

public enum SoundAsset implements Asset<Sound> {

    SWORD_HIT("sword_hit.wav"),;

    private final AssetDescriptor<Sound> descriptor;

    SoundAsset(String musicFile) {
        this.descriptor = new AssetDescriptor<>("audio/"+ musicFile, Sound.class);
    }


    @Override
    public AssetDescriptor<Sound> getDescriptor() {
        return descriptor;
    }

}
