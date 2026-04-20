package com.github.vasia228web.screen;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.vasia228web.GdxGame;
import com.github.vasia228web.asset.MapAsset;
import com.github.vasia228web.audio.AudioService;
import com.github.vasia228web.input.GameControllerState;
import com.github.vasia228web.input.KeyboardController;
import com.github.vasia228web.system.*;
import com.github.vasia228web.tiled.TiledAshleyConfigurator;
import com.github.vasia228web.tiled.TiledService;

import java.util.function.Consumer;


public class GameScreen extends ScreenAdapter {
    private final Engine engine;
    private final TiledService tiledService;
    private final TiledAshleyConfigurator tiledAshleyConfigurator;
    private final KeyboardController keyboardController ;
    private final GdxGame game;
    private final World physicsWorld;
    private final AudioService audioService;
    private final Stage stage;
    private final Viewport uiViewport;

    public GameScreen(GdxGame game){
        this.game = game;
        this.engine = new Engine();
        this.physicsWorld = new World(Vector2.Zero, true);
        this.tiledService = new TiledService(game.getAssetService(),this.physicsWorld);
        this.physicsWorld.setAutoClearForces(false);
        this.tiledAshleyConfigurator = new TiledAshleyConfigurator(this.engine, game.getAssetService(),physicsWorld);
        this.keyboardController = new KeyboardController(GameControllerState.class, engine);
        this.audioService = game.getAudioService();
        this.uiViewport = new FitViewport(320f, 180f);
        this.stage = new Stage(uiViewport, game.getBatch());

        this.engine.addSystem(new ControllerSystem());
        this.engine.addSystem(new PhysicMoveSystem());
        this.engine.addSystem(new FsmSystem());
        this.engine.addSystem(new FacingSystem());
        this.engine.addSystem(new PhysicSystem(physicsWorld, 1/60f));
        this.engine.addSystem(new DestroySystem(physicsWorld));
        this.engine.addSystem(new AnimationSystem(game.getAssetService()));
        this.engine.addSystem(new CameraSystem(game.getCamera()));
        this.engine.addSystem(new RenderSystem(game.getBatch(), game.getViewport(),game.getCamera()));
        this.engine.addSystem(new PhysicDebugRenderSystem(physicsWorld, game.getCamera()));

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.uiViewport.update(width, height);
    }

    @Override
    public void show() {
        game.setInputProcessor(keyboardController, stage);
        keyboardController.setActiveState(GameControllerState.class);

        Consumer<TiledMap> renderConsumer = this.engine.getSystem(RenderSystem.class)::setMap;
        Consumer<TiledMap> cameraConsumer = this.engine.getSystem(CameraSystem.class)::setMap;
        Consumer<TiledMap> audioConsumer = audioService::setMap;

        this.tiledService.setMapChangeConsumer(renderConsumer.andThen(cameraConsumer).andThen(audioConsumer));
        this.tiledService.setLoadObjectsConsumer(this.tiledAshleyConfigurator::onLoadObject);
        this.tiledService.setLoadTileConsumer(tiledAshleyConfigurator::onLoadTile);

        TiledMap tiledMap = this.tiledService.loadMap(MapAsset.MAIN);
        this.tiledService.setMap(tiledMap);

    }

    @Override
    public void hide() {
        this.engine.removeAllEntities();
        this.stage.clear();
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 1/30f);
        this.engine.update(delta);

        uiViewport.apply();
        stage.getBatch().setColor(Color.WHITE);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        for(EntitySystem system : this.engine.getSystems()){
            if(system instanceof Disposable disposableSystem){
                disposableSystem.dispose();
            }
        }
        this.physicsWorld.dispose();
        this.stage.dispose();
    }
}
