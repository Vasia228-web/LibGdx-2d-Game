package com.github.vasia228web.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.vasia228web.GdxGame;
import com.github.vasia228web.asset.SkinAsset;
import com.github.vasia228web.ui.model.MenuViewModel;
import com.github.vasia228web.ui.view.MenuView;

public class MenuScreen extends ScreenAdapter {

    private final GdxGame game;
    private final Stage stage;
    private final Skin skin;
    private final Viewport uiViewport;

    public MenuScreen(GdxGame game) {
        this.game = game;
        this.uiViewport = new FitViewport(800f, 450f);
        this.stage = new Stage(uiViewport, game.getBatch());
        this.skin = game.getAssetService().get(SkinAsset.DEFAULT);
    }

    @Override
    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }

    @Override
    public void show() {
        this.game.setInputProcessor(stage);

        this.stage.addActor(new MenuView(stage, skin, new MenuViewModel(game)));
    }

    @Override
    public void hide() {
        this.stage.clear();
    }

    @Override
    public void render(float delta) {
        uiViewport.apply();
        stage.getBatch().setColor(Color.WHITE);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
