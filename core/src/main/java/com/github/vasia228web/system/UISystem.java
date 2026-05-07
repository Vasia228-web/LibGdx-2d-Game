package com.github.vasia228web.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.vasia228web.component.Interaction;
import com.github.vasia228web.input.Controller;

public class UISystem extends IteratingSystem {

    private final Batch batch;
    private final BitmapFont font;
    private final Viewport uiViewport;

    public UISystem(Batch batch, Viewport uiViewport) {

        super(Family.all(Controller.class, Interaction.class).get());

        this.batch = batch;
        this.uiViewport = uiViewport;

        this.font = new BitmapFont();
        this.font.getData().setScale(0.5f);
    }

    @Override
    public void update(float deltaTime) {

        batch.setProjectionMatrix(uiViewport.getCamera().combined);
        batch.begin();
        super.update(deltaTime);

        batch.end();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        font.draw(batch, "Press E to Interact", 100, 30);
    }
}
