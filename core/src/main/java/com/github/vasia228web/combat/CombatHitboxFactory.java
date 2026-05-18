package com.github.vasia228web.combat;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.github.vasia228web.component.EnemyAttack;
import com.github.vasia228web.component.Facing;
import com.github.vasia228web.component.Physic;

public class CombatHitboxFactory {

    public Rectangle createEnemyAttackBox(Entity enemy) {
        EnemyAttack enemyAttack = EnemyAttack.MAPPER.get(enemy);
        Facing facing = Facing.MAPPER.get(enemy);

        Vector2 enemyCenter = createBodyCenter(enemy);

        float width = enemyAttack.hitboxWidth;
        float height = enemyAttack.hitboxHeight;
        float offset = enemyAttack.hitboxOffset;

        float x = enemyCenter.x;
        float y = enemyCenter.y;

        switch (facing.getDirection()) {
            case UP -> {
                x = enemyCenter.x - width / 2f;
                y = enemyCenter.y + offset;
            }
            case DOWN -> {
                x = enemyCenter.x - width / 2f;
                y = enemyCenter.y - offset - height;
            }
            case LEFT -> {
                x = enemyCenter.x - offset - width;
                y = enemyCenter.y - height / 2f;
            }
            case RIGHT -> {
                x = enemyCenter.x + offset;
                y = enemyCenter.y - height / 2f;
            }
        }

        return new Rectangle(x, y, width, height);
    }

    public Vector2 createBodyCenter(Entity entity) {
        Rectangle bodyBox = createBodyBox(entity);
        return new Vector2(
            bodyBox.x + bodyBox.width * 0.5f,
            bodyBox.y + bodyBox.height * 0.5f
        );
    }

    public Rectangle createBodyBox(Entity entity) {
        Body body = Physic.MAPPER.get(entity).getBody();

        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        Vector2 localPoint = new Vector2();

        for (Fixture fixture : body.getFixtureList()) {
            Shape shape = fixture.getShape();

            switch (shape.getType()) {
                case Circle -> {
                    CircleShape circle = (CircleShape) shape;
                    Vector2 center = body.getWorldPoint(circle.getPosition());
                    float radius = circle.getRadius();

                    minX = Math.min(minX, center.x - radius);
                    minY = Math.min(minY, center.y - radius);
                    maxX = Math.max(maxX, center.x + radius);
                    maxY = Math.max(maxY, center.y + radius);
                }

                case Polygon -> {
                    PolygonShape polygon = (PolygonShape) shape;

                    for (int i = 0; i < polygon.getVertexCount(); i++) {
                        polygon.getVertex(i, localPoint);
                        Vector2 worldPoint = body.getWorldPoint(localPoint);

                        minX = Math.min(minX, worldPoint.x);
                        minY = Math.min(minY, worldPoint.y);
                        maxX = Math.max(maxX, worldPoint.x);
                        maxY = Math.max(maxY, worldPoint.y);
                    }
                }

                default -> {
                }
            }
        }

        if (!Float.isFinite(minX)) {
            Vector2 center = body.getWorldCenter();
            return new Rectangle(center.x - 0.25f, center.y - 0.25f, 0.5f, 0.5f);
        }

        return new Rectangle(
            minX,
            minY,
            maxX - minX,
            maxY - minY
        );
    }
}
