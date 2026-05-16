package com.github.vasia228web.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.github.vasia228web.GdxGame;

public class PathfindingGrid {

    private final int width;
    private final int height;

    private final float tileWidthWorld;
    private final float tileHeightWorld;

    private final boolean[][] walkable;

    public PathfindingGrid(int width, int height, float tileWidthPx, float tileHeightPx) {
        this.width = width;
        this.height = height;

        this.tileWidthWorld = tileWidthPx * GdxGame.UNIT_SCALE;
        this.tileHeightWorld = tileHeightPx * GdxGame.UNIT_SCALE;

        this.walkable = new boolean[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                walkable[x][y] = true;
            }
        }
    }

    public boolean isInside(int tileX, int tileY) {
        return tileX >= 0
            && tileY >= 0
            && tileX < width
            && tileY < height;
    }

    public boolean isWalkable(int tileX, int tileY) {
        if (!isInside(tileX, tileY)) {
            return false;
        }

        return walkable[tileX][tileY];
    }

    public void setWalkable(int tileX, int tileY, boolean value) {
        if (!isInside(tileX, tileY)) {
            return;
        }

        walkable[tileX][tileY] = value;
    }

    public void blockTile(int tileX, int tileY) {
        setWalkable(tileX, tileY, false);
    }

    public void unblockTile(int tileX, int tileY) {
        setWalkable(tileX, tileY, true);
    }

    public int worldToTileX(float worldX) {
        return (int) Math.floor(worldX / tileWidthWorld);
    }

    public int worldToTileY(float worldY) {
        return (int) Math.floor(worldY / tileHeightWorld);
    }

    public float tileToWorldCenterX(int tileX) {
        return tileX * tileWidthWorld + tileWidthWorld / 2f;
    }

    public float tileToWorldCenterY(int tileY) {
        return tileY * tileHeightWorld + tileHeightWorld / 2f;
    }

    public Vector2 tileToWorldCenter(int tileX, int tileY) {
        return new Vector2(
            tileToWorldCenterX(tileX),
            tileToWorldCenterY(tileY)
        );
    }

    public void blockWorldRectangle(float worldX, float worldY, float worldWidth, float worldHeight) {
        int minTileX = worldToTileX(worldX);
        int minTileY = worldToTileY(worldY);

        int maxTileX = worldToTileX(worldX + worldWidth - 0.0001f);
        int maxTileY = worldToTileY(worldY + worldHeight - 0.0001f);

        for (int x = minTileX; x <= maxTileX; x++) {
            for (int y = minTileY; y <= maxTileY; y++) {
                blockTile(x, y);
            }
        }
    }

    public void blockPixelRectangle(float pixelX, float pixelY, float pixelWidth, float pixelHeight) {
        blockWorldRectangle(
            pixelX * GdxGame.UNIT_SCALE,
            pixelY * GdxGame.UNIT_SCALE,
            pixelWidth * GdxGame.UNIT_SCALE,
            pixelHeight * GdxGame.UNIT_SCALE
        );
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getTileWidthWorld() {
        return tileWidthWorld;
    }

    public float getTileHeightWorld() {
        return tileHeightWorld;
    }
}
