package com.github.vasia228web.input;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;


public class GameControllerState implements ControllerState {
    private final Engine engine;

    public GameControllerState(Engine engine) {
        this.engine = engine;
    }

    @Override
    public void keyDown(Command command) {
        for (Entity entity : engine.getEntitiesFor(Family.all(Controller.class).get())) {
            Controller.MAPPER.get(entity).getPressedCommands().add(command);
        }
    }

    @Override
    public void keyUp(Command command) {
        for (Entity entity : engine.getEntitiesFor(Family.all(Controller.class).get())) {
            Controller.MAPPER.get(entity).getReleasedCommands().add(command);
        }
    }
}
