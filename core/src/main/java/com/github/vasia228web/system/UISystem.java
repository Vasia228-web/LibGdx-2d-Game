package com.github.vasia228web.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.vasia228web.component.Interaction;
import com.github.vasia228web.component.NPC;
import com.github.vasia228web.component.Triggers;
import com.github.vasia228web.dialogue.DialogueSystem;
import com.github.vasia228web.input.Controller;

import java.util.HashMap;
import java.util.Map;

public class UISystem extends IteratingSystem implements Disposable {

    private final Batch batch;
    private final BitmapFont font;
    private final Viewport uiViewport;
    private final DialogueSystem dialogueSystem;
    private final Texture whitePixel;
    private final Map<String, Texture> portraits;

    public UISystem(Batch batch, Viewport uiViewport, DialogueSystem dialogueSystem) {

        super(Family.all(Controller.class, Interaction.class).get());

        this.batch = batch;
        this.uiViewport = uiViewport;
        this.dialogueSystem = dialogueSystem;

        this.font = new BitmapFont();
        this.font.getData().setScale(0.5f);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        this.whitePixel = new Texture(pixmap);
        pixmap.dispose();

        this.portraits = new HashMap<>();
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(uiViewport.getCamera().combined);

        batch.begin();

        if (dialogueSystem.isActive()) {
            drawDialogueWindow();
        } else {
            super.update(deltaTime);
        }

        batch.end();
    }

    private void drawDialogueWindow() {
        String speakerName = dialogueSystem.getSpeakerName();
        String text = dialogueSystem.getText();
        String portraitName = dialogueSystem.getPortraitName();

        float boxX = 8f;
        float boxY = 8f;
        float boxW = 304f;
        float boxH = 58f;

        // Фон діалогового вікна
        batch.setColor(0f, 0f, 0f, 0.85f);
        batch.draw(whitePixel, boxX, boxY, boxW, boxH);

        // Рамка зверху
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(whitePixel, boxX, boxY + boxH - 1f, boxW, 1f);
        batch.draw(whitePixel, boxX, boxY, boxW, 1f);
        batch.draw(whitePixel, boxX, boxY, 1f, boxH);
        batch.draw(whitePixel, boxX + boxW - 1f, boxY, 1f, boxH);

        batch.setColor(Color.WHITE);

        float portraitX = boxX + 6f;
        float portraitY = boxY + 7f;
        float portraitSize = 44f;

        Texture portrait = getPortrait(portraitName);

        if (portrait != null) {
            batch.draw(portrait, portraitX, portraitY, portraitSize, portraitSize);
        }

        float textX = boxX + 58f;
        float nameY = boxY + 48f;
        float textY = boxY + 34f;
        float textWidth = boxW - 68f;

        if (speakerName != null) {
            font.draw(batch, speakerName, textX, nameY);
        }

        if (text != null) {
            font.draw(
                batch,
                text,
                textX,
                textY,
                textWidth,
                Align.left,
                true
            );
        }

        batch.setColor(Color.WHITE);
    }

    private Texture getPortrait(String portraitName) {
        if (portraitName == null || portraitName.isBlank()) {
            return null;
        }

        if (portraits.containsKey(portraitName)) {
            return portraits.get(portraitName);
        }

        Texture texture = new Texture(
            Gdx.files.internal("portraits/" + portraitName + ".png")
        );

        portraits.put(portraitName, texture);

        return texture;

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

    @Override
    public void dispose() {
        font.dispose();
        whitePixel.dispose();

        for (Texture portrait : portraits.values()) {
            portrait.dispose();
        }

        portraits.clear();
    }
}

