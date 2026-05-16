package com.github.vasia228web.enemy;

import com.github.vasia228web.asset.AtlasAsset;

public class EnemyData {

    private final String enemyId;
    private final AtlasAsset atlasAsset;
    private final String idleFrame;
    private final int z;
    private final float bodyWidth;
    private final float bodyHeight;
    private final String atlasKey;

    public EnemyData(String enemyId, AtlasAsset atlasAsset,String atlasKey, String idleFrame, int z ,float bodyWidth, float bodyHeight) {
        this.enemyId = enemyId;
        this.atlasAsset = atlasAsset;
        this.idleFrame = idleFrame;
        this.z = z;
        this.bodyWidth = bodyWidth;
        this.bodyHeight = bodyHeight;
        this.atlasKey = atlasKey;

    }


    public String getEnemyId() {
        return enemyId;
    }

    public AtlasAsset getAtlasAsset() {
        return atlasAsset;
    }

    public String getIdleFrame() {
        return idleFrame;
    }

    public int getZ() {
        return z;
    }

    public float getBodyWidth() {
        return bodyWidth;
    }

    public String getAtlasKey() {
        return atlasKey;
    }

    public float getBodyHeight() {
        return bodyHeight;
    }

}
