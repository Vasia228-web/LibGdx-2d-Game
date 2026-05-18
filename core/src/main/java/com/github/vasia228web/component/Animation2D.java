package com.github.vasia228web.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.vasia228web.asset.AtlasAsset;
import com.github.vasia228web.component.Facing.FacingDirection;

public class Animation2D implements Component {

    public static final ComponentMapper<Animation2D> MAPPER =
        ComponentMapper.getFor(Animation2D.class);

    private final AtlasAsset atlasAsset;
    private final String atlasKey;

    private AnimationType type;
    private FacingDirection direction;
    private PlayMode playMode;

    private float speed;
    private float stateTime;

    private Animation<TextureRegion> animation;
    private boolean dirty;

    public Animation2D(
        AtlasAsset atlasAsset,
        String atlasKey,
        AnimationType type,
        PlayMode playMode,
        float speed
    ) {
        this.atlasAsset = atlasAsset;
        this.atlasKey = atlasKey;
        this.type = type;
        this.direction = null;
        this.playMode = playMode;
        this.speed = speed;

        this.stateTime = 0f;
        this.animation = null;
        this.dirty = true;
    }

    public void setAnimation(Animation<TextureRegion> animation, FacingDirection direction) {
        this.animation = animation;
        this.direction = direction;
        this.stateTime = 0f;
        this.dirty = false;
    }

    public AtlasAsset getAtlasAsset() {
        return atlasAsset;
    }

    public String getAtlasKey() {
        return atlasKey;
    }

    public AnimationType getType() {
        return type;
    }

    public FacingDirection getDirection() {
        return direction;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public float getSpeed() {
        return speed;
    }

    public float getStateTime() {
        return stateTime;
    }

    public boolean hasAnimation() {
        return animation != null;
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean isFinished() {
        return animation != null && animation.isAnimationFinished(stateTime);
    }

    public void setType(AnimationType type) {
        if (this.type == type) {
            return;
        }

        this.type = type;
        this.dirty = true;
    }

    public void setDirection(FacingDirection direction) {
        if (this.direction == direction) {
            return;
        }

        this.direction = direction;
        this.dirty = true;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;

        if (animation != null) {
            animation.setPlayMode(playMode);
        }
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void resetStateTime() {
        this.stateTime = 0f;
    }

    public float incAndGetStateTime(float deltaTime) {
        this.stateTime += deltaTime * speed;
        return this.stateTime;
    }

    public enum AnimationType {
        IDLE,
        WALK,
        ATTACK,
        DEATH;

        private final String atlasKey;

        AnimationType() {
            this.atlasKey = name().toLowerCase();
        }

        public String getAtlasKey() {
            return atlasKey;
        }
    }
}
