package com.github.vasia228web.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.github.vasia228web.component.Interaction;
import com.github.vasia228web.component.NPC;
import com.github.vasia228web.component.Triggers;
import com.github.vasia228web.dialogue.DialogueSystem;
import com.github.vasia228web.event.MapChanger;
import com.github.vasia228web.input.Controller;

public class InteractionSystem extends IteratingSystem {

    private final DialogueSystem dialogueSystem;
    private final MapChanger mapChanger;

    public InteractionSystem(MapChanger mapChanger, DialogueSystem dialogueSystem) {
        super(Family.all(Controller.class, Interaction.class).get());
        this.dialogueSystem = dialogueSystem;
        this.mapChanger = mapChanger;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (!Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            return;
        }

        if (dialogueSystem.isActive()) {
            dialogueSystem.closeDialog();
            return;
        }

        Interaction interaction = Interaction.MAPPER.get(entity);

        if (interaction == null || interaction.targetEntity == null) {
            return;
        }

        Entity target = interaction.targetEntity;

        NPC npc = NPC.MAPPER.get(target);
        Triggers trigger = Triggers.MAPPER.get(target);

        if (trigger != null) {
            mapChanger.changeMap(trigger.mapName, trigger.targetX, trigger.targetY);
            return;
        }

        if (npc != null) {
            dialogueSystem.startDialog(npc.npcId);
            return;
        }
    }
}
