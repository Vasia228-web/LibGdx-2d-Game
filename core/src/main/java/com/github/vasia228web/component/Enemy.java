package com.github.vasia228web.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class Enemy implements Component {

    public static final ComponentMapper<Enemy> MAPPER = ComponentMapper.getFor(Enemy.class);

    public String enemyId;

    public Enemy(String enemyId){
        this.enemyId = enemyId;
    }
}
