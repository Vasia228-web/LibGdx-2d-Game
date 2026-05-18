package com.github.vasia228web.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.vasia228web.component.Health;
import com.github.vasia228web.component.Interaction;
import com.github.vasia228web.component.NPC;
import com.github.vasia228web.component.Triggers;
import com.github.vasia228web.dialogue.DialogueSystem;
import com.github.vasia228web.input.Controller;
import com.github.vasia228web.ui.dialogue.DialogueWindow;
import com.github.vasia228web.ui.hud.PlayerHeartsHud;

public class UISystem extends IteratingSystem implements Disposable {

    private final Batch batch;
    private final BitmapFont font;
    private final Viewport uiViewport;
    private final DialogueSystem dialogueSystem;
    private final DialogueWindow dialogueWindow;
    private final PlayerHeartsHud playerHeartsHud;

    private ImmutableArray<Entity> players;

    public UISystem(Batch batch, Viewport uiViewport, DialogueSystem dialogueSystem) {
        super(Family.all(Controller.class, Interaction.class).get());

        this.batch = batch;
        this.uiViewport = uiViewport;
        this.dialogueSystem = dialogueSystem;

        this.font = new BitmapFont();
        this.font.getData().setScale(0.5f);

        this.playerHeartsHud = new PlayerHeartsHud(uiViewport);
        this.dialogueWindow = new DialogueWindow(dialogueSystem);
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(uiViewport.getCamera().combined);

        batch.begin();

        playerHeartsHud.draw(batch, getPlayerHealth());

        if (dialogueSystem.isActive()) {
            dialogueWindow.draw(batch);
        } else {
            super.update(deltaTime);
        }

        batch.end();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        this.players = engine.getEntitiesFor(
            Family.all(Controller.class, Health.class).get()
        );
    }



    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Interaction interaction = Interaction.MAPPER.get(entity);

        if (interaction == null || interaction.targetEntity == null) {
            return;
        }

        Entity target = interaction.targetEntity;

        NPC npc = NPC.MAPPER.get(target);
        if (npc != null) {
            font.draw(batch, "Press E to talk", 100, 30);
            return;
        }

        Triggers trigger = Triggers.MAPPER.get(target);
        if (trigger != null) {
            font.draw(batch, "Press E to enter", 100, 30);
            return;
        }

        font.draw(batch, "Press E to interact", 100, 30);
    }

    private Health getPlayerHealth() {
        if (players == null || players.size() == 0) {
            return null;
        }

        Entity player = players.first();
        return Health.MAPPER.get(player);
    }

    @Override
    public void dispose() {
        font.dispose();
        dialogueWindow.dispose();
    }
}
