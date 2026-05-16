package com.github.vasia228web.pathfinding;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AStarPathfinder {

    private final PathfindingGrid grid;
    private static final int LOCAL_WALKABLE_SEARCH_RADIUS = 8;

    private static final int[][] DIRECTIONS = {
        {1, 0},   // right
        {-1, 0},  // left
        {0, 1},   // up
        {0, -1}   // down
    };

    public AStarPathfinder(PathfindingGrid grid) {
        this.grid = grid;
    }
    public PathfindingGrid getGrid() {
        return grid;
    }

    public Array<Vector2> findPathWorld(Vector2 startWorld, Vector2 targetWorld) {
        int startTileX = grid.worldToTileX(startWorld.x);
        int startTileY = grid.worldToTileY(startWorld.y);

        int targetTileX = grid.worldToTileX(targetWorld.x);
        int targetTileY = grid.worldToTileY(targetWorld.y);

        Array<GridPoint2> tilePath = findPath(
            startTileX,
            startTileY,
            targetTileX,
            targetTileY
        );

        Array<Vector2> worldPath = new Array<>();

        for (GridPoint2 tile : tilePath) {
            worldPath.add(grid.tileToWorldCenter(tile.x, tile.y));
        }

        return worldPath;
    }

    public Array<GridPoint2> findPath(int startX, int startY, int targetX, int targetY) {
        Array<GridPoint2> emptyPath = new Array<>();

        GridPoint2 resolvedStart = resolveWalkableTile(startX, startY);
        GridPoint2 resolvedTarget = resolveWalkableTile(targetX, targetY);

        if (resolvedStart == null || resolvedTarget == null) {
            return emptyPath;
        }

        startX = resolvedStart.x;
        startY = resolvedStart.y;

        targetX = resolvedTarget.x;
        targetY = resolvedTarget.y;

        if (startX == targetX && startY == targetY) {
            emptyPath.add(new GridPoint2(startX, startY));
            return emptyPath;
        }

        Node[][] nodes = new Node[grid.getWidth()][grid.getHeight()];
        boolean[][] closed = new boolean[grid.getWidth()][grid.getHeight()];
        float[][] bestGCost = new float[grid.getWidth()][grid.getHeight()];

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                bestGCost[x][y] = Float.MAX_VALUE;
            }
        }

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(Node::getFCost));

        Node startNode = getNode(nodes, startX, startY);
        startNode.gCost = 0f;
        startNode.hCost = heuristic(startX, startY, targetX, targetY);

        bestGCost[startX][startY] = 0f;
        open.add(startNode);

        while (!open.isEmpty()) {
            Node current = open.poll();

            if (closed[current.x][current.y]) {
                continue;
            }

            closed[current.x][current.y] = true;

            if (current.x == targetX && current.y == targetY) {
                return buildPath(current);
            }

            for (int[] direction : DIRECTIONS) {
                int nextX = current.x + direction[0];
                int nextY = current.y + direction[1];

                if (!grid.isWalkable(nextX, nextY)) {
                    continue;
                }

                if (closed[nextX][nextY]) {
                    continue;
                }

                float newGCost = current.gCost + 1f;

                if (newGCost < bestGCost[nextX][nextY]) {
                    Node nextNode = getNode(nodes, nextX, nextY);

                    nextNode.gCost = newGCost;
                    nextNode.hCost = heuristic(nextX, nextY, targetX, targetY);
                    nextNode.parent = current;

                    bestGCost[nextX][nextY] = newGCost;

                    open.add(nextNode);
                }
            }
        }

        return emptyPath;
    }

    private Node getNode(Node[][] nodes, int x, int y) {
        if (nodes[x][y] == null) {
            nodes[x][y] = new Node(x, y);
        }

        return nodes[x][y];
    }

    private float heuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private Array<GridPoint2> buildPath(Node targetNode) {
        Array<GridPoint2> path = new Array<>();

        Node current = targetNode;

        while (current != null) {
            path.insert(0, new GridPoint2(current.x, current.y));
            current = current.parent;
        }

        return path;
    }

    private GridPoint2 resolveWalkableTile(int tileX, int tileY) {
        if (grid.isWalkable(tileX, tileY)) {
            return new GridPoint2(tileX, tileY);
        }

        GridPoint2 localClosest = findClosestWalkableTile(
            tileX,
            tileY,
            LOCAL_WALKABLE_SEARCH_RADIUS
        );

        if (localClosest != null) {
            return localClosest;
        }

        return findClosestWalkableTile(
            tileX,
            tileY,
            Math.max(grid.getWidth(), grid.getHeight())
        );
    }

    private GridPoint2 findClosestWalkableTile(int centerX, int centerY, int maxRadius) {
        int minTileX = Math.max(0, centerX - maxRadius);
        int maxTileX = Math.min(grid.getWidth() - 1, centerX + maxRadius);
        int minTileY = Math.max(0, centerY - maxRadius);
        int maxTileY = Math.min(grid.getHeight() - 1, centerY + maxRadius);

        GridPoint2 bestTile = null;
        int bestDistanceSquared = Integer.MAX_VALUE;
        int bestManhattanDistance = Integer.MAX_VALUE;

        for (int x = minTileX; x <= maxTileX; x++) {
            for (int y = minTileY; y <= maxTileY; y++) {
                if (!grid.isWalkable(x, y)) {
                    continue;
                }

                int deltaX = x - centerX;
                int deltaY = y - centerY;
                int distanceSquared = deltaX * deltaX + deltaY * deltaY;
                int manhattanDistance = Math.abs(deltaX) + Math.abs(deltaY);

                if (distanceSquared < bestDistanceSquared
                    || (
                        distanceSquared == bestDistanceSquared
                            && manhattanDistance < bestManhattanDistance
                    )) {
                    bestTile = new GridPoint2(x, y);
                    bestDistanceSquared = distanceSquared;
                    bestManhattanDistance = manhattanDistance;
                }
            }
        }

        return bestTile;
    }

    private static class Node {
        int x;
        int y;

        float gCost;
        float hCost;

        Node parent;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        float getFCost() {
            return gCost + hCost;
        }
    }
}
