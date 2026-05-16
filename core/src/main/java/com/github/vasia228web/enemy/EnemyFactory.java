package com.github.vasia228web.enemy;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.vasia228web.GdxGame;
import com.github.vasia228web.asset.AssetService;
import com.github.vasia228web.component.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.github.vasia228web.component.Animation2D;
import com.github.vasia228web.component.Animation2D.AnimationType;
import com.github.vasia228web.component.Facing;
import com.github.vasia228web.component.Facing.FacingDirection;

public class EnemyFactory {

    private final Engine engine;
    private final AssetService assetService;
    private final EnemyRepository enemyRepository;
    private final World physicsWorld;

    public EnemyFactory(
        Engine engine,
        AssetService assetService,
        EnemyRepository enemyRepository,
        World physicWorld
    ) {
        this.engine = engine;
        this.assetService = assetService;
        this.enemyRepository = enemyRepository;
        this.physicsWorld = physicWorld;
    }

    public Entity createEnemy(String enemyId, float x, float y) {
        Gdx.app.log("ENEMY_FACTORY", "Create enemy: " + enemyId);

        EnemyData enemyData = enemyRepository.getEnemyData(enemyId);

        if (enemyData == null) {
            throw new GdxRuntimeException("Unknown enemyId: " + enemyId);
        }

        TextureRegion textureRegion = getEnemyTexture(enemyData);

        Entity entity = engine.createEntity();

        Vector2 position = new Vector2(x, y);
        Vector2 size = new Vector2(
            textureRegion.getRegionWidth(),
            textureRegion.getRegionHeight()
        );
        Vector2 scaling = new Vector2(1f, 1f);

        position.scl(GdxGame.UNIT_SCALE);
        size.scl(GdxGame.UNIT_SCALE);

        Transform transform = new Transform(
            position,
            enemyData.getZ(),
            size,
            scaling,
            0f
        );

        entity.add(new Enemy(enemyData.getEnemyId()));
        entity.add(new Graphic(Color.WHITE.cpy(), textureRegion));
        entity.add(transform);
        entity.add(new Facing(FacingDirection.DOWN));
        entity.add(new EnemyAI(position.x,position.y,3f, 6f, 1f));
        entity.add(new Move(1f));
        entity.add(new PathFollow());
        entity.add(new Animation2D(
            enemyData.getAtlasAsset(),
            enemyData.getAtlasKey(),
            AnimationType.IDLE,
            Animation.PlayMode.LOOP,
            1f
        ));




        Body body = createEnemyBody(entity, transform, enemyData);
        entity.add(new Physic(body, transform.getPosition().cpy()));


        engine.addEntity(entity);

        Gdx.app.log("ENEMY_FACTORY", "Enemy created: " + enemyId);

        return entity;
    }

    private Body createEnemyBody(Entity entity, Transform transform, EnemyData enemyData) {
        float bodyWidth = enemyData.getBodyWidth() * GdxGame.UNIT_SCALE;
        float bodyHeight = enemyData.getBodyHeight() * GdxGame.UNIT_SCALE;

        float spriteWidth = transform.getSize().x;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;

        bodyDef.position.set(transform.getPosition());

        Body body = physicsWorld.createBody(bodyDef);
        body.setUserData(entity);

        PolygonShape shape = new PolygonShape();

        float offsetX = spriteWidth / 2f;


        float offsetY = 30f * GdxGame.UNIT_SCALE;

        shape.setAsBox(
            bodyWidth / 2f,
            bodyHeight / 2f,
            new Vector2(offsetX, offsetY),
            0f
        );

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);

        shape.dispose();

        return body;
    }

    private TextureRegion getEnemyTexture(EnemyData enemyData) {
        TextureAtlas atlas = assetService.get(enemyData.getAtlasAsset());

        TextureAtlas.AtlasRegion region = atlas.findRegion(enemyData.getIdleFrame());

        if (region == null) {
            throw new GdxRuntimeException(
                "Enemy texture not found in atlas: " + enemyData.getIdleFrame()
            );
        }

        return region;
    }
}
