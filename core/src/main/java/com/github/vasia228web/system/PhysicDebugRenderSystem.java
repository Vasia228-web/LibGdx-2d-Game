package com.github.vasia228web.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class PhysicDebugRenderSystem extends EntitySystem implements Disposable {

    private final World physicWorld;
    private final Box2DDebugRenderer box2dDebugRenderer;
    private final Camera camera;

    private boolean disposed = false;

    public PhysicDebugRenderSystem(World physicWorld, Camera camera) {
        this.physicWorld = physicWorld;
        this.camera = camera;
        this.box2dDebugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void update(float deltaTime) {
        if (disposed) {
            return;
        }

        this.box2dDebugRenderer.render(physicWorld, camera.combined);
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }

        disposed = true;
        box2dDebugRenderer.dispose();
    }
}
