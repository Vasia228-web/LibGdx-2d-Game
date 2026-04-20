package com.github.vasia228web.audio;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.github.vasia228web.asset.AssetService;
import com.github.vasia228web.asset.MusicAsset;
import com.github.vasia228web.asset.SoundAsset;

public class AudioService {

    private final AssetService assetService;
    private Music currentMusic;
    private MusicAsset currentMusicAsset;
    private float musicVolume;
    private float soundVolume;

    public AudioService(AssetService assetService) {
        this.assetService = assetService;
        this.currentMusic =  null;
        this.currentMusicAsset = null;
        this.musicVolume = 0.5f;
        this.soundVolume = 0.33f;
    }

    public void setMusicVolume(float musicVolume) {
        this.musicVolume = MathUtils.clamp(musicVolume, 0.0f, 1.0f);
        if(currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
    }

    public float getMusicVolume() {
        return musicVolume;
    }


    public void setSoundVolume(float soundVolume) {
        this.soundVolume = MathUtils.clamp(soundVolume, 0.0f, 1.0f);

    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void playMusic(MusicAsset musicAsset) {
        if(this.currentMusicAsset == musicAsset) return;

        if(this.currentMusic != null) {
            this.currentMusic.stop();
            this.assetService.unload(this.currentMusicAsset);
        }

        this.currentMusic = this.assetService.load(musicAsset);
        this.currentMusic.setLooping(true);
        this.currentMusic.setVolume(musicVolume);
        this.currentMusic.play();
        this.currentMusicAsset = musicAsset;

    }


    public void playSound(SoundAsset soundAsset) {
        this.assetService.get(soundAsset).play(soundVolume);
    }

    public void setMap(TiledMap tiledMap) {

        String musicAssetStr = tiledMap.getProperties().get("music","", String.class);
        if(musicAssetStr.isEmpty()) return;

        MusicAsset musicAsset = MusicAsset.valueOf(musicAssetStr);
        playMusic(musicAsset);

    }

}
