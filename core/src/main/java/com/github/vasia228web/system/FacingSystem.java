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

        Facing facing =Facing.MAPPER.get(entity);
        if(moveDirection.y > 0f){
            facing.setDirection(FacingDirection.UP);
        }else if(moveDirection.y < 0f){
            facing.setDirection(FacingDirection.DOWN);
        }else if(moveDirection.x > 0f){
            facing.setDirection(FacingDirection.RIGHT);
        }else if(moveDirection.x < 0f){
            facing.setDirection(FacingDirection.LEFT);
        }
    }
}
