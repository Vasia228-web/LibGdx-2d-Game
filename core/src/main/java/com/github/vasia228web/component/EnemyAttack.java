package com.github.vasia228web.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class EnemyAttack implements Component {

    public static final ComponentMapper<EnemyAttack> MAPPER =
        ComponentMapper.getFor(EnemyAttack.class);

    public int damage;

    public float cooldown;
    public float cooldownTimer;

    public float damageTime;

    public boolean attacking;
    public boolean damageDone;

    public float hitboxWidth;
    public float hitboxHeight;
    public float hitboxOffset;

    public EnemyAttack(
        int damage,
        float cooldown,
        float hitboxWidth,
        float hitboxHeight,
        float hitboxOffset
    ) {
        this.damage = damage;

        this.cooldown = cooldown;
        this.cooldownTimer = 0f;

        this.damageTime = 0.25f;

        this.attacking = false;
        this.damageDone = false;

        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
        this.hitboxOffset = hitboxOffset;
    }
}
