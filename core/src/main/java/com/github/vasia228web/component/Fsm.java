package com.github.vasia228web.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.github.vasia228web.ai.AnimationState;

public class Fsm implements Component {
    public static final ComponentMapper<Fsm> MAPPER = ComponentMapper.getFor(Fsm.class);

    private final DefaultStateMachine<Entity, AnimationState> animationFsm;

    public Fsm(Entity owner) {
        this.animationFsm = new DefaultStateMachine<>(owner, AnimationState.IDLE);
    }

    public DefaultStateMachine<Entity, AnimationState> getAnimationFsm(){
        return this.animationFsm;
    }

}
