package com.github.vasia228web.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.github.vasia228web.component.Interaction;
import com.github.vasia228web.component.Triggers;
import com.github.vasia228web.input.Controller;


public class ContactSystem implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Object dataA = contact.getFixtureA().getBody().getUserData();
        Object dataB = contact.getFixtureB().getBody().getUserData();

        if (!(dataA instanceof Entity) || !(dataB instanceof Entity)) {
            return;
        }

        Entity entityA = (Entity) dataA;
        Entity entityB = (Entity) dataB;

        boolean isA_Player = entityA.getComponent(Controller.class) != null;
        boolean isA_Trigger = entityA.getComponent(Triggers.class) != null;

        boolean isB_Player = entityB.getComponent(Controller.class) != null;
        boolean isB_Trigger = entityB.getComponent(Triggers.class) != null;

        if ((isA_Trigger && isB_Player) || (isA_Player && isB_Trigger)) {

            Entity player = isA_Player ? entityA : entityB;
            Entity trigger = isA_Trigger ? entityA : entityB;

            player.add(new Interaction(trigger));

        }
    }

    @Override
    public void endContact(Contact contact) {

        Object dataA = contact.getFixtureA().getBody().getUserData();
        Object dataB = contact.getFixtureB().getBody().getUserData();

        if (!(dataA instanceof Entity) || !(dataB instanceof Entity)) {
            return;
        }

        Entity entityA = (Entity) dataA;
        Entity entityB = (Entity) dataB;

        boolean isA_Player = entityA.getComponent(Controller.class) != null;
        boolean isA_Trigger = entityA.getComponent(Triggers.class) != null;

        boolean isB_Player = entityB.getComponent(Controller.class) != null;
        boolean isB_Trigger = entityB.getComponent(Triggers.class) != null;

        if ((isA_Trigger && isB_Player) || (isA_Player && isB_Trigger)) {
            Entity player = isA_Player ? entityA : entityB;
            player.remove(Interaction.class);
        }

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

}
