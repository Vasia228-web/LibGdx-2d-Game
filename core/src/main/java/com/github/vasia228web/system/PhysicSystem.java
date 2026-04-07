package com.github.vasia228web.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.github.vasia228web.component.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.github.vasia228web.component.Physic;

public class PhysicSystem extends IteratingSystem{

    private final World world;
    private final float interval;
    private float accumulator;



    public PhysicSystem(World world, float interval) {
        super(Family.all(Physic.class, Transform.class).get());
        this.world = world;
        this.interval = interval;
        this.accumulator = 0;
    }

    @Override
    public void update(float deltaTime) {
        this.accumulator += deltaTime;

        while (this.accumulator >= this.interval) {
            this.accumulator -= this.interval;
            super.update(interval);
            this.world.step(interval, 6, 2);
        }


        float alpha = this.accumulator / this.interval;
        for(int i =0; i < getEntities().size(); i++){
            this.interpolateEntity(getEntities().get(i), alpha);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Physic physic = Physic.MAPPER.get(entity);
        Body body = physic.getBody();

        if (body == null) return;

        physic.getPrevPosition().set(physic.getBody().getPosition());
    }

    private void interpolateEntity(Entity entity, float alpha) {
        Transform transform = Transform.MAPPER.get(entity);
        Physic physic = Physic.MAPPER.get(entity);
        Body body = physic.getBody();

        if (body == null) return;

        transform.getPosition().set(
            MathUtils.lerp(physic.getPrevPosition().x, physic.getBody().getPosition().x,alpha),
            MathUtils.lerp(physic.getPrevPosition().y, physic.getBody().getPosition().y,alpha)
        );
    }

}
