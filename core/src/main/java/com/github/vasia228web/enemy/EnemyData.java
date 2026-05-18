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
    private final int damage;
    private final float attackCooldown;
    private final float attackHitboxWidth;
    private final float attackHitboxHeight;
    private final float attackHitboxOffset;


    public EnemyData(String enemyId, AtlasAsset atlasAsset,String atlasKey,
         String idleFrame, int z ,float bodyWidth,
         float bodyHeight, int damage, float attackCooldown,
         float attackHitboxWidth, float attackHitboxHeight, float attackHitboxOffset) {

        this.enemyId = enemyId;
        this.atlasAsset = atlasAsset;
        this.idleFrame = idleFrame;
        this.z = z;
        this.bodyWidth = bodyWidth;
        this.bodyHeight = bodyHeight;
        this.atlasKey = atlasKey;
        this.damage = damage;
        this.attackCooldown = attackCooldown;
        this.attackHitboxHeight = attackHitboxHeight;
        this.attackHitboxWidth = attackHitboxWidth;
        this.attackHitboxOffset = attackHitboxOffset;

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

    public int getDamage() {
        return damage;
    }

    public float getAttackCooldown() {
        return attackCooldown;
    }

    public float getAttackHitboxWidth() {
        return attackHitboxWidth;
    }

    public float getAttackHitboxHeight() {
        return attackHitboxHeight;
    }

    public float getAttackHitboxOffset() {
        return attackHitboxOffset;
    }

}
