package com.github.vasia228web.pathfinding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class PathfindingGridBuilder {

    private static final String BACKGROUND_LAYER = "background";
    private static final float QUERY_EPSILON = 0.001f;
    private static final float AGENT_HALF_WIDTH_WORLD = 0.42f;
    private static final float AGENT_HALF_HEIGHT_WORLD = 0.28f;
    private static final int AGENT_SAMPLE_COLUMNS = 5;
    private static final int AGENT_SAMPLE_ROWS = 5;

    private final World physicsWorld;

    public PathfindingGridBuilder(World physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    public PathfindingGrid build(TiledMap tiledMap) {
        TiledMapTileLayer backgroundLayer = getBackgroundLayer(tiledMap);

        PathfindingGrid grid = new PathfindingGrid(
            backgroundLayer.getWidth(),
            backgroundLayer.getHeight(),
            backgroundLayer.getTileWidth(),
            backgroundLayer.getTileHeight()
        );

        Gdx.app.log("PATH_GRID", "Grid created: "
            + backgroundLayer.getWidth()
            + "x"
            + backgroundLayer.getHeight()
        );

        fillFromPhysics(grid);

        return grid;
    }

    private TiledMapTileLayer getBackgroundLayer(TiledMap tiledMap) {
        MapLayer layer = tiledMap.getLayers().get(BACKGROUND_LAYER);

        if (!(layer instanceof TiledMapTileLayer)) {
            throw new RuntimeException("Layer '" + BACKGROUND_LAYER + "' not found or is not TileLayer");
        }

        return (TiledMapTileLayer) layer;
    }

    private void fillFromPhysics(PathfindingGrid grid) {
        Array<BlockingFixture> blockingFixtures = collectBlockingFixtures();
        int blockedCount = 0;

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                if (!isTileWalkableForAgent(grid, x, y, blockingFixtures)) {
                    grid.blockTile(x, y);
                    blockedCount++;
                }
            }
        }

        Gdx.app.log("PATH_GRID", "Blocking fixtures collected: " + blockingFixtures.size);
        Gdx.app.log("PATH_GRID", "Blocked from physics world: " + blockedCount);
    }

    private boolean isTileWalkableForAgent(PathfindingGrid grid, int tileX, int tileY, Array<BlockingFixture> blockingFixtures) {
        float centerX = grid.tileToWorldCenterX(tileX);
        float centerY = grid.tileToWorldCenterY(tileY);

        float halfWidth = Math.min(AGENT_HALF_WIDTH_WORLD, grid.getTileWidthWorld() * 0.49f);
        float halfHeight = Math.min(AGENT_HALF_HEIGHT_WORLD, grid.getTileHeightWorld() * 0.49f);

        float minX = centerX - halfWidth;
        float maxX = centerX + halfWidth;
        float minY = centerY - halfHeight;
        float maxY = centerY + halfHeight;

        for (BlockingFixture blockingFixture : blockingFixtures) {
            if (!blockingFixture.overlaps(minX, minY, maxX, maxY)) {
                continue;
            }

            for (int sampleX = 0; sampleX < AGENT_SAMPLE_COLUMNS; sampleX++) {
                float progressX = AGENT_SAMPLE_COLUMNS == 1
                    ? 0.5f
                    : (float) sampleX / (AGENT_SAMPLE_COLUMNS - 1);

                float worldX = minX + (maxX - minX) * progressX;

                for (int sampleY = 0; sampleY < AGENT_SAMPLE_ROWS; sampleY++) {
                    float progressY = AGENT_SAMPLE_ROWS == 1
                        ? 0.5f
                        : (float) sampleY / (AGENT_SAMPLE_ROWS - 1);

                    float worldY = minY + (maxY - minY) * progressY;

                    if (isBlockingAtPoint(blockingFixture, worldX, worldY)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private Array<BlockingFixture> collectBlockingFixtures() {
        Array<BlockingFixture> blockingFixtures = new Array<>();
        Array<Body> bodies = new Array<>();
        physicsWorld.getBodies(bodies);

        for (Body body : bodies) {
            if (body == null || body.getType() != BodyDef.BodyType.StaticBody) {
                continue;
            }

            for (Fixture fixture : body.getFixtureList()) {
                if (!isBlockingFixture(fixture)) {
                    continue;
                }

                BlockingFixture blockingFixture = createBlockingFixture(fixture);

                if (blockingFixture != null) {
                    blockingFixtures.add(blockingFixture);
                }
            }
        }

        return blockingFixtures;
    }

    private boolean isBlockingAtPoint(BlockingFixture blockingFixture, float worldX, float worldY) {
        if (!blockingFixture.contains(worldX, worldY)) {
            return false;
        }

        return containsPoint(blockingFixture.fixture, worldX, worldY);
    }

    private boolean containsPoint(Fixture fixture, float worldX, float worldY) {
        Shape shape = fixture.getShape();

        if (shape == null) {
            return false;
        }

        if (!(shape instanceof ChainShape)) {
            return fixture.testPoint(worldX, worldY);
        }

        // Tiled polygon obstacles become closed ChainShape loops, so we treat them as filled contours.
        return isInsideChainLoop(fixture.getBody(), (ChainShape) shape, worldX, worldY);
    }

    private boolean isBlockingFixture(Fixture fixture) {
        if (fixture == null || fixture.isSensor()) {
            return false;
        }

        Body body = fixture.getBody();

        return body != null && body.getType() == BodyDef.BodyType.StaticBody;
    }

    private BlockingFixture createBlockingFixture(Fixture fixture) {
        Shape shape = fixture.getShape();

        if (shape == null) {
            return null;
        }

        if (shape instanceof CircleShape) {
            return createCircleBlockingFixture(fixture, (CircleShape) shape);
        }

        if (shape instanceof PolygonShape) {
            return createPolygonBlockingFixture(fixture, (PolygonShape) shape);
        }

        if (shape instanceof ChainShape) {
            return createChainBlockingFixture(fixture, (ChainShape) shape);
        }

        if (shape instanceof EdgeShape) {
            return createEdgeBlockingFixture(fixture, (EdgeShape) shape);
        }

        return null;
    }

    private BlockingFixture createCircleBlockingFixture(Fixture fixture, CircleShape shape) {
        Vector2 worldCenter = fixture.getBody().getWorldPoint(shape.getPosition());
        float radius = shape.getRadius();

        return new BlockingFixture(
            fixture,
            worldCenter.x - radius,
            worldCenter.y - radius,
            worldCenter.x + radius,
            worldCenter.y + radius
        );
    }

    private BlockingFixture createPolygonBlockingFixture(Fixture fixture, PolygonShape shape) {
        return createVertexBlockingFixture(fixture, shape.getVertexCount(), (index, vertex) -> shape.getVertex(index, vertex));
    }

    private BlockingFixture createChainBlockingFixture(Fixture fixture, ChainShape shape) {
        return createVertexBlockingFixture(fixture, shape.getVertexCount(), (index, vertex) -> shape.getVertex(index, vertex));
    }

    private BlockingFixture createEdgeBlockingFixture(Fixture fixture, EdgeShape shape) {
        return createVertexBlockingFixture(fixture, 2, (index, vertex) -> {
            if (index == 0) {
                shape.getVertex1(vertex);
            } else {
                shape.getVertex2(vertex);
            }
        });
    }

    private BlockingFixture createVertexBlockingFixture(Fixture fixture, int vertexCount, VertexReader vertexReader) {
        if (vertexCount <= 0) {
            return null;
        }

        Vector2 localVertex = new Vector2();
        Vector2 worldVertex = new Vector2();

        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        for (int index = 0; index < vertexCount; index++) {
            vertexReader.read(index, localVertex);
            worldVertex.set(fixture.getBody().getWorldPoint(localVertex));

            minX = Math.min(minX, worldVertex.x);
            minY = Math.min(minY, worldVertex.y);
            maxX = Math.max(maxX, worldVertex.x);
            maxY = Math.max(maxY, worldVertex.y);
        }

        return new BlockingFixture(fixture, minX, minY, maxX, maxY);
    }

    private boolean isInsideChainLoop(Body body, ChainShape shape, float worldX, float worldY) {
        int vertexCount = shape.getVertexCount();

        if (vertexCount < 3) {
            return false;
        }

        Vector2 localVertex = new Vector2();
        Vector2 worldVertex = new Vector2();

        shape.getVertex(vertexCount - 1, localVertex);
        worldVertex.set(body.getWorldPoint(localVertex));

        float previousX = worldVertex.x;
        float previousY = worldVertex.y;
        boolean inside = false;

        for (int index = 0; index < vertexCount; index++) {
            shape.getVertex(index, localVertex);
            worldVertex.set(body.getWorldPoint(localVertex));

            float currentX = worldVertex.x;
            float currentY = worldVertex.y;

            if (isPointOnSegment(previousX, previousY, currentX, currentY, worldX, worldY)) {
                return true;
            }

            if (doesCrossRay(previousX, previousY, currentX, currentY, worldX, worldY)) {
                inside = !inside;
            }

            previousX = currentX;
            previousY = currentY;
        }

        return inside;
    }

    private boolean doesCrossRay(float startX, float startY, float endX, float endY, float pointX, float pointY) {
        if ((startY > pointY) == (endY > pointY)) {
            return false;
        }

        float xAtPointY = startX + (pointY - startY) * (endX - startX) / (endY - startY);
        return xAtPointY >= pointX - QUERY_EPSILON;
    }

    private boolean isPointOnSegment(float startX, float startY, float endX, float endY, float pointX, float pointY) {
        float cross = (pointX - startX) * (endY - startY) - (pointY - startY) * (endX - startX);

        if (Math.abs(cross) > QUERY_EPSILON) {
            return false;
        }

        float dot = (pointX - startX) * (endX - startX) + (pointY - startY) * (endY - startY);

        if (dot < -QUERY_EPSILON) {
            return false;
        }

        float squaredLength = (endX - startX) * (endX - startX) + (endY - startY) * (endY - startY);
        return dot <= squaredLength + QUERY_EPSILON;
    }

    private interface VertexReader {

        void read(int index, Vector2 vertex);
    }

    private static final class BlockingFixture {

        private final Fixture fixture;
        private final float minX;
        private final float minY;
        private final float maxX;
        private final float maxY;

        private BlockingFixture(Fixture fixture, float minX, float minY, float maxX, float maxY) {
            this.fixture = fixture;
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        private boolean overlaps(float otherMinX, float otherMinY, float otherMaxX, float otherMaxY) {
            return maxX >= otherMinX - QUERY_EPSILON
                && maxY >= otherMinY - QUERY_EPSILON
                && minX <= otherMaxX + QUERY_EPSILON
                && minY <= otherMaxY + QUERY_EPSILON;
        }

        private boolean contains(float worldX, float worldY) {
            return worldX >= minX - QUERY_EPSILON
                && worldX <= maxX + QUERY_EPSILON
                && worldY >= minY - QUERY_EPSILON
                && worldY <= maxY + QUERY_EPSILON;
        }
    }
}
