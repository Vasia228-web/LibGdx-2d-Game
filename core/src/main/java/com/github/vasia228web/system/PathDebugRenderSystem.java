package com.github.vasia228web.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;
import com.github.vasia228web.component.Enemy;
import com.github.vasia228web.component.PathFollow;
import com.github.vasia228web.component.Physic;
import com.github.vasia228web.component.Transform;
import com.github.vasia228web.pathfinding.AStarPathfinder;
import com.github.vasia228web.pathfinding.PathfindingGrid;

public class PathDebugRenderSystem extends IteratingSystem implements Disposable {

    private static final float TILE_INSET_RATIO = 0.12f;
    private static final Color ENEMY_TILE_FILL = new Color(0.18f, 0.72f, 1f, 0.22f);
    private static final Color PATH_TILE_FILL = new Color(0.92f, 0.45f, 0.12f, 0.28f);
    private static final Color CURRENT_TILE_FILL = new Color(1f, 0.72f, 0.14f, 0.5f);
    private static final Color FINAL_TILE_FILL = new Color(0.34f, 0.88f, 0.42f, 0.42f);

    private static final Color ENEMY_TILE_OUTLINE = new Color(0.42f, 0.85f, 1f, 0.95f);
    private static final Color PATH_TILE_OUTLINE = new Color(0.94f, 0.58f, 0.2f, 0.85f);
    private static final Color CURRENT_TILE_OUTLINE = new Color(1f, 0.94f, 0.32f, 1f);
    private static final Color FINAL_TILE_OUTLINE = new Color(0.52f, 1f, 0.56f, 1f);
    private static final Color CURRENT_FINAL_TILE_OUTLINE = new Color(0.88f, 1f, 0.46f, 1f);

    private final ShapeRenderer shapeRenderer;
    private final Camera camera;
    private PathfindingGrid grid;
    private RenderPass renderPass = RenderPass.FILLED;

    public PathDebugRenderSystem(Camera camera) {
        super(Family.all(Enemy.class, Transform.class, PathFollow.class, Physic.class).get());
        this.camera = camera;
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void update(float deltaTime) {
        if (grid == null) {
            return;
        }

        shapeRenderer.setProjectionMatrix(camera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);

        renderPass = RenderPass.FILLED;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        super.update(deltaTime);
        shapeRenderer.end();

        renderPass = RenderPass.OUTLINE;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        super.update(deltaTime);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PathFollow pathFollow = PathFollow.MAPPER.get(entity);
        Body body = Physic.MAPPER.get(entity).getBody();

        if (pathFollow == null || !pathFollow.hasPath()) {
            return;
        }

        if (body == null) {
            return;
        }

        if (renderPass == RenderPass.FILLED) {
            renderFilledPath(pathFollow, body);
            return;
        }

        renderPathOutlines(pathFollow, body);
    }

    public void setPathfinder(AStarPathfinder pathfinder) {
        this.grid = pathfinder != null
            ? pathfinder.getGrid()
            : null;
    }

    private void renderFilledPath(PathFollow pathFollow, Body body) {
        Vector2 bodyCenter = body.getWorldCenter();
        drawTile(worldToTileX(bodyCenter.x), worldToTileY(bodyCenter.y), ENEMY_TILE_FILL);

        for (int i = pathFollow.currentIndex; i < pathFollow.path.size; i++) {
            Vector2 waypoint = pathFollow.path.get(i);
            drawTile(worldToTileX(waypoint.x), worldToTileY(waypoint.y), PATH_TILE_FILL);
        }

        Vector2 currentWaypoint = pathFollow.path.get(pathFollow.currentIndex);
        Vector2 finalWaypoint = pathFollow.path.peek();

        drawTile(worldToTileX(finalWaypoint.x), worldToTileY(finalWaypoint.y), FINAL_TILE_FILL);
        drawTile(worldToTileX(currentWaypoint.x), worldToTileY(currentWaypoint.y), CURRENT_TILE_FILL);
    }

    private void renderPathOutlines(PathFollow pathFollow, Body body) {
        Vector2 bodyCenter = body.getWorldCenter();
        drawTileOutline(worldToTileX(bodyCenter.x), worldToTileY(bodyCenter.y), ENEMY_TILE_OUTLINE);

        for (int i = pathFollow.currentIndex; i < pathFollow.path.size; i++) {
            Vector2 waypoint = pathFollow.path.get(i);
            drawTileOutline(worldToTileX(waypoint.x), worldToTileY(waypoint.y), PATH_TILE_OUTLINE);
        }

        Vector2 currentWaypoint = pathFollow.path.get(pathFollow.currentIndex);
        Vector2 finalWaypoint = pathFollow.path.peek();

        int currentTileX = worldToTileX(currentWaypoint.x);
        int currentTileY = worldToTileY(currentWaypoint.y);
        int finalTileX = worldToTileX(finalWaypoint.x);
        int finalTileY = worldToTileY(finalWaypoint.y);

        if (currentTileX == finalTileX && currentTileY == finalTileY) {
            drawTileOutline(currentTileX, currentTileY, CURRENT_FINAL_TILE_OUTLINE);
            return;
        }

        drawTileOutline(finalTileX, finalTileY, FINAL_TILE_OUTLINE);
        drawTileOutline(currentTileX, currentTileY, CURRENT_TILE_OUTLINE);
    }

    private int worldToTileX(float worldX) {
        return grid.worldToTileX(worldX);
    }

    private int worldToTileY(float worldY) {
        return grid.worldToTileY(worldY);
    }

    private void drawTile(int tileX, int tileY, Color color) {
        drawTileRect(tileX, tileY, color);
    }

    private void drawTileOutline(int tileX, int tileY, Color color) {
        drawTileRect(tileX, tileY, color);
    }

    private void drawTileRect(int tileX, int tileY, Color color) {
        if (!grid.isInside(tileX, tileY)) {
            return;
        }

        float tileWidth = grid.getTileWidthWorld();
        float tileHeight = grid.getTileHeightWorld();
        float insetX = tileWidth * TILE_INSET_RATIO;
        float insetY = tileHeight * TILE_INSET_RATIO;
        float worldX = grid.tileToWorldCenterX(tileX) - tileWidth * 0.5f + insetX;
        float worldY = grid.tileToWorldCenterY(tileY) - tileHeight * 0.5f + insetY;

        shapeRenderer.setColor(color);
        shapeRenderer.rect(
            worldX,
            worldY,
            tileWidth - insetX * 2f,
            tileHeight - insetY * 2f
        );
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }

    private enum RenderPass {
        FILLED,
        OUTLINE
    }
}
