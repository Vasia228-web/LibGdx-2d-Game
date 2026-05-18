package com.github.vasia228web.ui.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.github.vasia228web.dialogue.DialogueSystem;

import java.util.HashMap;
import java.util.Map;

public class DialogueWindow implements Disposable {

    private final DialogueSystem dialogueSystem;

    private final BitmapFont font;
    private final Texture whitePixel;
    private final Map<String, Texture> portraits;

    public DialogueWindow(DialogueSystem dialogueSystem) {
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

    public void draw(Batch batch) {
        String speakerName = dialogueSystem.getSpeakerName();
        String text = dialogueSystem.getText();
        String portraitName = dialogueSystem.getPortraitName();

        float boxX = 8f;
        float boxY = 8f;
        float boxW = 304f;
        float boxH = 58f;

        batch.setColor(0f, 0f, 0f, 0.85f);
        batch.draw(whitePixel, boxX, boxY, boxW, boxH);

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
    public void dispose() {
        font.dispose();
        whitePixel.dispose();

        for (Texture portrait : portraits.values()) {
            portrait.dispose();
        }

        portraits.clear();
    }
}
