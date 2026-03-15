package com.github.vasia228web.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.github.vasia228web.GdxGame;
import com.github.vasia228web.asset.AssetService;
import com.github.vasia228web.asset.AtlasAsset;

public class LoadingScreen extends ScreenAdapter {

    private final GdxGame game;
    private final AssetService assetService;

    public LoadingScreen(GdxGame game, AssetService assetService  ) {
        this.game = game;
        this.assetService = assetService;
    }

    @Override
    public void show() {
        for(AtlasAsset atlas : AtlasAsset.values()) {
            assetService.queue(atlas);
        }
    }

    @Override
    public void render(float delta) {
        if(this.assetService.update()){
            Gdx.app.debug("LoadingScreen", "Asset update successful");
            createScreens();
            this.game.removeScreen(this);
            this.dispose();
            this.game.setScreen(GameScreen.class);
        }
    }

    private void createScreens() {
        this.game.addScreen(new GameScreen(this.game));
    }
}
