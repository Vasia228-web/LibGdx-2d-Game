package com.github.vasia228web.screen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.vasia228web.GdxGame;
import com.github.vasia228web.asset.AssetService;
import com.github.vasia228web.asset.MapAsset;
import com.github.vasia228web.input.GameControllerState;
import com.github.vasia228web.input.KeyboardController;
import com.github.vasia228web.system.ControllerSystem;
import com.github.vasia228web.system.MoveSystem;
import com.github.vasia228web.system.RenderSystem;
import com.github.vasia228web.tiled.TiledAshleyConfigurator;
import com.github.vasia228web.tiled.TiledService;

import java.util.function.Consumer;


public class GameScreen extends ScreenAdapter {
    private final Engine engine;
    private final TiledService tiledService;
    private final TiledAshleyConfigurator tiledAshleyConfigurator;
    private final KeyboardController keyboardController ;
    private final GdxGame game;

    public GameScreen(GdxGame game){
        this.game = game;
        this.tiledService = new TiledService(game.getAssetService());
        this.engine = new Engine();
        this.tiledAshleyConfigurator = new TiledAshleyConfigurator(this.engine, game.getAssetService());
        this.keyboardController = new KeyboardController(GameControllerState.class, engine);

        this.engine.addSystem(new ControllerSystem());
        this.engine.addSystem(new MoveSystem());
        this.engine.addSystem(new RenderSystem(game.getBatch(), game.getViewport(),game.getCamera()));
    }

    @Override
    public void show() {
        game.setInputProcessor(keyboardController);
        keyboardController.setActiveState(GameControllerState.class);

        Consumer<TiledMap> renderConsumer = this.engine.getSystem(RenderSystem.class)::setMap;
        this.tiledService.setMapChangeConsumer(renderConsumer);
        this.tiledService.setLoadObjectsConsumer(this.tiledAshleyConfigurator::onLoadObject);

        TiledMap tiledMap = this.tiledService.loadMap(MapAsset.MAIN);
        this.tiledService.setMap(tiledMap);

    }

    @Override
    public void hide() {
        this.engine.removeAllEntities();
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 1/30f);
        this.engine.update(delta);
    }

    @Override
    public void dispose() {
        for(EntitySystem system : this.engine.getSystems()){
            if(system instanceof Disposable disposableSystem){
                disposableSystem.dispose();
            }
        }
    }
}
