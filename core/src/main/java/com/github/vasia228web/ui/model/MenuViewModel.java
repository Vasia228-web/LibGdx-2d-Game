package com.github.vasia228web.ui.model;

import com.badlogic.gdx.Gdx;
import com.github.vasia228web.GdxGame;
import com.github.vasia228web.audio.AudioService;
import com.github.vasia228web.screen.GameScreen;

public class MenuViewModel extends ViewModel {
    private final AudioService audioService;

    public MenuViewModel(GdxGame game) {
        super(game);
        this.audioService = game.getAudioService();
    }

    public float getMusicVolume() {
        return audioService.getMusicVolume();
    }

    public float getSoundVolume() {
        return audioService.getSoundVolume();
    }

    public void setMusicVolume(float volume) {
        audioService.setMusicVolume(volume);
    }

    public void setSoundVolume(float volume) {
        audioService.setSoundVolume(volume);
    }

    public void startGame(){
        game.setScreen(GameScreen.class);
    }

    public void quitGame(){
        Gdx.app.exit();
    }

}
