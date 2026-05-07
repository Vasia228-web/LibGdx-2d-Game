package com.github.vasia228web.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;


public class Interaction implements Component {
    public static final ComponentMapper<Interaction> MAPPER = ComponentMapper.getFor(Interaction.class);

    public Entity targetEntity;

    public Interaction(Entity targetEntity) {
        this.targetEntity = targetEntity;

    }
}
