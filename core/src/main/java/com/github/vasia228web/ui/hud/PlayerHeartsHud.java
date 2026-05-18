package com.github.vasia228web.ui.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.vasia228web.component.Health;

public class PlayerHeartsHud implements Disposable {

    private final Viewport uiViewport;

    private final Texture fullHeart;
    private final Texture halfHeart;
    private final Texture emptyHeart;

    private final float heartSize = 12f;
    private final float spacing = 2f;
    private final float padding = 8f;

    public PlayerHeartsHud(Viewport uiViewport) {
        this.uiViewport = uiViewport;

        this.fullHeart = new Texture(Gdx.files.internal("hud/heart_full.png"));
        this.halfHeart = new Texture(Gdx.files.internal("hud/heart_half.png"));
        this.emptyHeart = new Texture(Gdx.files.internal("hud/heart_blank.png"));
    }

    public void draw(Batch batch, Health health) {
        if (health == null) {
            return;
        }

        int currentHp = health.getCurrent();
        int maxHp = health.getMax();

        int maxHearts = (int) Math.ceil(maxHp / 2f);

        float startX = padding;
        float startY = uiViewport.getWorldHeight() - padding - heartSize;

        for (int i = 0; i < maxHearts; i++) {
            int heartHpStart = i * 2;
            int hpInThisHeart = currentHp - heartHpStart;

            Texture heartTexture;

            if (hpInThisHeart >= 2) {
                heartTexture = fullHeart;
            } else if (hpInThisHeart == 1) {
                heartTexture = halfHeart;
            } else {
                heartTexture = emptyHeart;
            }

            float x = startX + i * (heartSize + spacing);

            batch.draw(
                heartTexture,
                x,
                startY,
                heartSize,
                heartSize
            );
        }
    }

    @Override
    public void dispose() {
        fullHeart.dispose();
        halfHeart.dispose();
        emptyHeart.dispose();
    }
}
