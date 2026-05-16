package com.github.vasia228web.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class EnemyAI implements Component {

    public static final ComponentMapper<EnemyAI> MAPPER = ComponentMapper.getFor(EnemyAI.class);

    public State state;

    public float spawnX;
    public float spawnY;

    public float patrolRadius;
    public float aggroRange;
    public float attackRange;

    public int patrolDirection;

    public EnemyAI(
        float spawnX,
        float spawnY,
        float patrolRadius,
        float aggroRange,
        float attackRange
    ) {
        this.state = State.PATROL;

        this.spawnX = spawnX;
        this.spawnY = spawnY;

        this.patrolRadius = patrolRadius;
        this.aggroRange = aggroRange;
        this.attackRange = attackRange;

        this.patrolDirection = 1;
    }

    public enum State {
        PATROL,
        CHASE,
        ATTACK
    }

}
