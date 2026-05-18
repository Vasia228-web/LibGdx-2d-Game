package com.github.vasia228web.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class Health implements Component {

    public static final ComponentMapper<Health> MAPPER =
        ComponentMapper.getFor(Health.class);

    private int current;
    private final int max;

    public Health(int max) {
        this.max = max;
        this.current = max;
    }

    public void damage(int amount) {
        if(amount <= 0) {
            return;
        }

        current -= amount;

        if (current < 0) {
            current = 0;
        }
    }

    public void heal(int amount) {
        if(amount <= 0) {
            return;
        }
        current += amount;
        if (current > max) {
            current = max;
        }
    }

    public boolean isDead() {
        return current <= 0;
    }

    public int getCurrent() {
        return current;
    }

    public int getMax() {
        return max;
    }
}
