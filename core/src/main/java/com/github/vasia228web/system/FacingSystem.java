package com.github.vasia228web.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.github.vasia228web.component.Facing;
import com.github.vasia228web.component.Facing.FacingDirection;
import com.github.vasia228web.component.Move;

public class FacingSystem extends IteratingSystem {
    public FacingSystem() {
        super(Family.all(Facing.class, Move.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Move move = Move.MAPPER.get(entity);
        Vector2 moveDirection = move.getDirection();
        if(moveDirection.isZero()){
            return;
        }

        Facing facing = Facing.MAPPER.get(entity);

        if(Math.abs(moveDirection.x) > Math.abs(moveDirection.y)){
            if(moveDirection.x > 0f){
                facing.setDirection(FacingDirection.RIGHT);
            } else {
                facing.setDirection(FacingDirection.LEFT);
            }
        } else {
            if(moveDirection.y > 0f){
                facing.setDirection(FacingDirection.UP);
            } else {
                facing.setDirection(FacingDirection.DOWN);
            }
        }
    }
}
