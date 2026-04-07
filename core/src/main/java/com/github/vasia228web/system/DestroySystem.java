package com.github.vasia228web.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.github.vasia228web.component.DestroyTag;
import com.github.vasia228web.component.Physic;

public class DestroySystem extends EntitySystem {
    private final World world;
    private ImmutableArray<Entity> entities;

    public DestroySystem(World world) {
        this.world = world;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(DestroyTag.class).get());
    }

    @Override
    public void update(float deltaTime) {
        if(world.isLocked()) return;

        Engine engine = getEngine();

        for(int i = 0; i < entities.size(); i++){
            Entity entity = entities.get(i);

            Physic physic = Physic.MAPPER.get(entity);
            if(physic != null){
                Body body = physic.getBody();
                if(body != null) {
                    world.destroyBody(body);
                    physic.setBody(null);
                }
            }
            engine.removeSystem(this);
        }
    }
}
