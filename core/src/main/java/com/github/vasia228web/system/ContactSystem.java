package com.github.vasia228web.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import com.github.vasia228web.component.Interaction;
import com.github.vasia228web.component.NPC;
import com.github.vasia228web.component.Triggers;
import com.github.vasia228web.input.Controller;


public class ContactSystem implements ContactListener {

    @Override
    public void beginContact(Contact contact) {

        if (!contact.getFixtureA().isSensor() && !contact.getFixtureB().isSensor()) {
            return;
        }

        Object dataA = contact.getFixtureA().getBody().getUserData();
        Object dataB = contact.getFixtureB().getBody().getUserData();

        if (!(dataA instanceof Entity) || !(dataB instanceof Entity)) {
            return;
        }

        Entity entityA = (Entity) dataA;
        Entity entityB = (Entity) dataB;

        boolean isA_Player = entityA.getComponent(Controller.class) != null;
        boolean isB_Player = entityB.getComponent(Controller.class) != null;

        boolean isA_Trigger = entityA.getComponent(Triggers.class) != null;
        boolean isB_Trigger = entityB.getComponent(Triggers.class) != null;

        boolean isA_Npc = entityA.getComponent(NPC.class) != null;
        boolean isB_Npc = entityB.getComponent(NPC.class) != null;

        boolean isA_Interactable = isA_Trigger || isA_Npc;
        boolean isB_Interactable = isB_Trigger || isB_Npc;

        if ((isA_Player && isB_Interactable) || (isB_Player && isA_Interactable)) {

            Entity player = isA_Player ? entityA : entityB;
            Entity target = isA_Interactable ? entityA : entityB;

            player.add(new Interaction(target));
        }
    }

    @Override
    public void endContact(Contact contact) {

        if (!contact.getFixtureA().isSensor() && !contact.getFixtureB().isSensor()) {
            return;
        }

        Object dataA = contact.getFixtureA().getBody().getUserData();
        Object dataB = contact.getFixtureB().getBody().getUserData();

        if (!(dataA instanceof Entity) || !(dataB instanceof Entity)) {
            return;
        }

        Entity entityA = (Entity) dataA;
        Entity entityB = (Entity) dataB;

        boolean isA_Player = entityA.getComponent(Controller.class) != null;
        boolean isB_Player = entityB.getComponent(Controller.class) != null;

        boolean isA_Trigger = entityA.getComponent(Triggers.class) != null;
        boolean isB_Trigger = entityB.getComponent(Triggers.class) != null;

        boolean isA_Npc = entityA.getComponent(NPC.class) != null;
        boolean isB_Npc = entityB.getComponent(NPC.class) != null;

        boolean isA_Interactable = isA_Trigger || isA_Npc;
        boolean isB_Interactable = isB_Trigger || isB_Npc;

        if ((isA_Player && isB_Interactable) || (isB_Player && isA_Interactable)) {

            Entity player = isA_Player ? entityA : entityB;
            Entity target = isA_Interactable ? entityA : entityB;

            Interaction interaction = Interaction.MAPPER.get(player);

            if (interaction != null && interaction.targetEntity == target) {
                player.remove(Interaction.class);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

}
