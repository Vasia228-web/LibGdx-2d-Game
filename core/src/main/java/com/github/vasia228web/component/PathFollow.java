package com.github.vasia228web.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PathFollow implements Component {

    public static final ComponentMapper<PathFollow> MAPPER =
        ComponentMapper.getFor(PathFollow.class);

    public Array<Vector2> path = new Array<>();
    public int currentIndex = 0;

    public float repathTimer = 0f;
    public float repathInterval = 0.4f;

    public float waypointReachDistance = 0.16f;

    public void clear() {
        path.clear();
        currentIndex = 0;
        repathTimer = 0f;
    }

    public boolean hasPath() {
        return path != null && path.size > 0 && currentIndex < path.size;
    }
}
