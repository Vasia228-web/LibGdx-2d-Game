package com.github.vasia228web.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.github.vasia228web.asset.MapAsset;
import com.github.vasia228web.component.Move;
import com.github.vasia228web.component.Physic;
import com.github.vasia228web.component.Transform;
import com.github.vasia228web.event.MapChanger;
import com.github.vasia228web.input.Controller;
import com.github.vasia228web.input.KeyboardController;
import com.github.vasia228web.tiled.TiledService;

public class MapTransitionService implements MapChanger {

    private final Engine engine;
    private final World physicsWorld;
    private final TiledService tiledService;
    private final CameraSystem cameraSystem;
    private final KeyboardController keyboardController;
    private boolean hasRequest = false;
    private String nextMapName;
    private float targetX;
    private float targetY;

    public MapTransitionService(
        Engine engine,
        World physicsWorld,
        TiledService tiledService,
        CameraSystem cameraSystem,
        KeyboardController keyboardController
    ) {
        this.engine = engine;
        this.physicsWorld = physicsWorld;
        this.tiledService = tiledService;
        this.cameraSystem = cameraSystem;
        this.keyboardController = keyboardController;
    }

    @Override
    public void changeMap(String mapName, float targetX, float targetY) {
        this.hasRequest = true;
        this.nextMapName = mapName;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void update() {
        if (!hasRequest) return;

        hasRequest = false;
        doChangeMap(nextMapName, targetX, targetY);
    }

    private void doChangeMap(String mapName, float targetX, float targetY) {
        Array<Body> bodies = new Array<>();
        physicsWorld.getBodies(bodies);

        for (Body body : bodies) {
            physicsWorld.destroyBody(body);
        }

        engine.removeAllEntities();

        MapAsset nextMapAsset = MapAsset.valueOf(mapName.toUpperCase());
        TiledMap nextMap = tiledService.loadMap(nextMapAsset);
        tiledService.setMap(nextMap);

        teleportPlayer(targetX, targetY, nextMap);

        keyboardController.forceClearCommands();
        resetPlayerInput();

    }

    private void resetPlayerInput() {
        Family playerFamily = Family.all(Controller.class).get();

        for (Entity player : engine.getEntitiesFor(playerFamily)) {
            Controller controller = Controller.MAPPER.get(player);

            if (controller != null) {
                controller.getPressedCommands().clear();
                controller.getReleasedCommands().clear();
            }

            Move move = Move.MAPPER.get(player);

            if (move != null) {
                move.getDirection().setZero();
            }

            Physic physic = Physic.MAPPER.get(player);

            if (physic != null && physic.body != null) {
                physic.body.setLinearVelocity(0f, 0f);
                physic.body.setAngularVelocity(0f);
            }
        }
    }

    private void teleportPlayer(float targetX, float targetY, TiledMap map) {
        Family playerFamily = Family.all(Controller.class).get();
        ImmutableArray<Entity> players = engine.getEntitiesFor(playerFamily);

        if (players.size() == 0) return;

        Entity player = players.first();

        Transform transform = Transform.MAPPER.get(player);
        Physic physic = Physic.MAPPER.get(player);

        float finalX = targetX + 0.5f;
        float finalY = targetY + 0.5f;

        if (transform != null) {
            transform.position.set(finalX, finalY);
        }

        if (physic != null && physic.body != null) {
            physic.body.setTransform(finalX, finalY, 0f);
        }


        cameraSystem.setMap(map);
        cameraSystem.teleportCamera(finalX, finalY);
    }
}


