package com.github.vasia228web.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Physic implements Component {
    public static final ComponentMapper<Physic> MAPPER = ComponentMapper.getFor(Physic.class);

    public Body body;
    private final Vector2 prevPosition;

    public Physic(Body body, Vector2 prevPosition) {
        this.body = body;
        this.prevPosition = prevPosition;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Vector2 getPrevPosition() {
        return prevPosition;
    }
}
