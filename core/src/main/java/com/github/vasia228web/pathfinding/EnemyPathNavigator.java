package com.github.vasia228web.pathfinding;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.github.vasia228web.component.Animation2D;
import com.github.vasia228web.component.Animation2D.AnimationType;
import com.github.vasia228web.component.Move;
import com.github.vasia228web.component.PathFollow;
import com.github.vasia228web.component.Physic;

public class EnemyPathNavigator {

    private static final float MIN_APPROACH_DISTANCE = 0.45f;
    private static final float BODY_MARGIN = 0.10f;

    private AStarPathfinder pathfinder;

    public void setPathfinder(AStarPathfinder pathfinder) {
        this.pathfinder = pathfinder;
    }

    public void chase(Entity enemy, Entity player, float deltaTime) {
        Move move = Move.MAPPER.get(enemy);
        Animation2D animation2D = Animation2D.MAPPER.get(enemy);
        PathFollow pathFollow = PathFollow.MAPPER.get(enemy);

        Body enemyBody = Physic.MAPPER.get(enemy).getBody();
        Body playerBody = Physic.MAPPER.get(player).getBody();

        BodyBounds enemyBounds = getBodyBounds(enemyBody);
        BodyBounds playerBounds = getBodyBounds(playerBody);

        Vector2 enemyPosition = enemyBounds.getCenter();
        Vector2 playerPosition = playerBounds.getCenter();


        if (hasLineOfSight(enemyBody, enemyPosition, playerPosition)) {
            pathFollow.clear();
            moveTowards(move, animation2D, enemyPosition, playerPosition);
            return;
        }


        if (pathfinder == null) {
            finishChaseWithoutPath(
                move,
                animation2D,
                enemyPosition,
                playerPosition,
                enemyBody
            );
            return;
        }

        pathFollow.repathTimer += deltaTime;

        boolean needNewPath =
            !pathFollow.hasPath()
                || pathFollow.repathTimer >= pathFollow.repathInterval;

        if (needNewPath) {
            pathFollow.repathTimer = 0f;

            Array<Vector2> newPath = pathfinder.findPathWorld(
                enemyPosition,
                playerPosition
            );

            if (newPath != null && newPath.size > 0) {
                pathFollow.path = newPath;
                pathFollow.currentIndex = resolveInitialPathIndex(enemyPosition, newPath);
            } else {
                pathFollow.clear();
            }
        }

        if (!pathFollow.hasPath()) {
            finishChaseWithoutPath(
                move,
                animation2D,
                enemyPosition,
                playerPosition,
                enemyBody
            );
            return;
        }

        while (pathFollow.hasPath()) {
            Vector2 targetPoint = pathFollow.path.get(pathFollow.currentIndex);

            float distanceToPoint = enemyPosition.dst(targetPoint);

            if (distanceToPoint > pathFollow.waypointReachDistance) {
                break;
            }

            pathFollow.currentIndex++;
        }

        if (!pathFollow.hasPath()) {
            finishChaseWithoutPath(
                move,
                animation2D,
                enemyPosition,
                playerPosition,
                enemyBody
            );
            return;
        }

        Vector2 targetPoint = pathFollow.path.get(pathFollow.currentIndex);

        moveTowards(move, animation2D, enemyPosition, targetPoint);
    }

    public void stop(Entity enemy) {
        Move move = Move.MAPPER.get(enemy);
        Animation2D animation2D = Animation2D.MAPPER.get(enemy);
        PathFollow pathFollow = PathFollow.MAPPER.get(enemy);

        pathFollow.clear();
        stopMovement(move, animation2D);
    }

    private int resolveInitialPathIndex(Vector2 enemyPosition, Array<Vector2> path) {
        if (pathfinder == null || path.size < 2) {
            return 0;
        }

        PathfindingGrid grid = pathfinder.getGrid();

        Vector2 firstPoint = path.first();

        int enemyTileX = grid.worldToTileX(enemyPosition.x);
        int enemyTileY = grid.worldToTileY(enemyPosition.y);

        int firstTileX = grid.worldToTileX(firstPoint.x);
        int firstTileY = grid.worldToTileY(firstPoint.y);

        if (enemyTileX == firstTileX && enemyTileY == firstTileY) {
            return 1;
        }

        return 0;
    }

    private void finishChaseWithoutPath(
        Move move,
        Animation2D animation2D,
        Vector2 enemyPosition,
        Vector2 approachTarget,
        Body enemyBody
    ) {
        if (!hasLineOfSight(enemyBody, enemyPosition, approachTarget)) {
            stopMovement(move, animation2D);
            return;
        }

        moveTowards(move, animation2D, enemyPosition, approachTarget);
    }

    private void moveTowards(
        Move move,
        Animation2D animation2D,
        Vector2 from,
        Vector2 target
    ) {
        Vector2 direction = new Vector2(target).sub(from);
        setMovementDirection(move, animation2D, direction);
    }

    private void setMovementDirection(
        Move move,
        Animation2D animation2D,
        Vector2 direction
    ) {
        if (direction.len2() > 0.0001f) {
            direction.nor();

            animation2D.setType(AnimationType.WALK);
        } else {
            direction.setZero();
            animation2D.setType(AnimationType.IDLE);
        }

        move.getDirection().set(direction);
    }

    private void stopMovement(Move move, Animation2D animation2D) {
        move.getDirection().setZero();
        animation2D.setType(AnimationType.IDLE);
    }

    private float getDesiredDistance(BodyBounds enemyBounds, BodyBounds playerBounds) {
        float playerRadius = playerBounds.getRadius();
        float enemyRadius = enemyBounds.getRadius();

        return Math.max(
            MIN_APPROACH_DISTANCE,
            playerRadius + enemyRadius + BODY_MARGIN
        );
    }

    private Vector2 getApproachTarget(
        Vector2 enemyPosition,
        Vector2 playerPosition,
        float desiredDistance
    ) {
        Vector2 direction = new Vector2(enemyPosition).sub(playerPosition);

        if (direction.len2() <= 0.0001f) {
            direction.set(1f, 0f);
        } else {
            direction.nor();
        }

        return new Vector2(playerPosition).mulAdd(direction, desiredDistance);
    }

    private BodyBounds getBodyBounds(Body body) {
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

                    Vector2 worldCenter = body.getWorldPoint(circle.getPosition());
                    float radius = circle.getRadius();

                    minX = Math.min(minX, worldCenter.x - radius);
                    minY = Math.min(minY, worldCenter.y - radius);
                    maxX = Math.max(maxX, worldCenter.x + radius);
                    maxY = Math.max(maxY, worldCenter.y + radius);
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

                case Edge -> {
                    EdgeShape edge = (EdgeShape) shape;

                    edge.getVertex1(localPoint);
                    Vector2 worldPoint1 = body.getWorldPoint(localPoint);

                    minX = Math.min(minX, worldPoint1.x);
                    minY = Math.min(minY, worldPoint1.y);
                    maxX = Math.max(maxX, worldPoint1.x);
                    maxY = Math.max(maxY, worldPoint1.y);

                    edge.getVertex2(localPoint);
                    Vector2 worldPoint2 = body.getWorldPoint(localPoint);

                    minX = Math.min(minX, worldPoint2.x);
                    minY = Math.min(minY, worldPoint2.y);
                    maxX = Math.max(maxX, worldPoint2.x);
                    maxY = Math.max(maxY, worldPoint2.y);
                }

                case Chain -> {
                    ChainShape chain = (ChainShape) shape;

                    for (int i = 0; i < chain.getVertexCount(); i++) {
                        chain.getVertex(i, localPoint);

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

        if (
            !Float.isFinite(minX)
                || !Float.isFinite(minY)
                || !Float.isFinite(maxX)
                || !Float.isFinite(maxY)
        ) {
            Vector2 fallbackCenter = body.getWorldCenter();

            return new BodyBounds(
                fallbackCenter.x,
                fallbackCenter.y,
                fallbackCenter.x,
                fallbackCenter.y
            );
        }

        return new BodyBounds(minX, minY, maxX, maxY);
    }

    private boolean hasLineOfSight(
        Body sourceBody,
        Vector2 from,
        Vector2 to
    ) {
        final boolean[] blocked = {false};

        World world = sourceBody.getWorld();

        world.rayCast((fixture, point, normal, fraction) -> {
            if (!isBlockingFixture(fixture)) {
                return -1f;
            }

            blocked[0] = true;
            return 0f;
        }, from, to);

        return !blocked[0];
    }

    private boolean isBlockingFixture(Fixture fixture) {
        if (fixture == null || fixture.isSensor()) {
            return false;
        }

        Body body = fixture.getBody();

        return body != null && body.getType() == BodyDef.BodyType.StaticBody;
    }

    private static class BodyBounds {

        private final float minX;
        private final float minY;
        private final float maxX;
        private final float maxY;

        private BodyBounds(
            float minX,
            float minY,
            float maxX,
            float maxY
        ) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        private float getRadius() {
            float width = Math.abs(maxX - minX);
            float height = Math.abs(maxY - minY);

            return Math.max(width, height) * 0.5f;
        }

        private Vector2 getCenter() {
            return new Vector2(
                (minX + maxX) * 0.5f,
                (minY + maxY) * 0.5f
            );
        }
    }
}
