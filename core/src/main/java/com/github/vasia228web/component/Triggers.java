package com.github.vasia228web.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;


public class Triggers implements Component {
    public static final ComponentMapper<Triggers> MAPPER = ComponentMapper.getFor(Triggers.class);

    public String mapName;
    public float targetX;
    public float targetY;


}
