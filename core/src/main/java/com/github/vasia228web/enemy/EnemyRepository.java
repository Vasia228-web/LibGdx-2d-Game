package com.github.vasia228web.enemy;
import com.github.vasia228web.asset.AtlasAsset;
import java.util.HashMap;
import java.util.Map;

public class EnemyRepository {

    private final Map<String, EnemyData> enemies;

    public EnemyRepository() {
        this.enemies = new HashMap<>();

        //SUMMER

        enemies.put("plant_1", new EnemyData(
            "plant_1",
            AtlasAsset.OBJECTS,
            "enemies/plant_1",
            "enemies/plant_1/idle_down",
            1,
            13f,
            8f,
            //DAMAGE
            1,
            1.5f,
            //RANGE ATTACK
            18f,
            14f,
            10f
        ));
        enemies.put("orc_1", new EnemyData(
            "orc_1",
            AtlasAsset.OBJECTS,
            "enemies/orc_1",
            "enemies/orc_1/idle_down",
            1,
            13f,
            8f,
            1,
            1.5f,
            18f,
            14f,
            10f
        ));
        enemies.put("vampire_1", new EnemyData(
            "vampire_1",
            AtlasAsset.OBJECTS,
            "enemies/vampire_1",
            "enemies/vampire_1/idle_down",
            1,
            13f,
            8f,
            1,
            1.5f,

            18f,
            14f,
            10f
        ));
        enemies.put("slime_blue", new EnemyData(
            "slime_blue",
            AtlasAsset.OBJECTS,
            "enemies/slime_blue",
            "enemies/slime_blue/idle_down",
            1,
            13f,
            8f,
            1,
            1.5f,

            18f,
            14f,
            10f
        ));

        //DESERT

        enemies.put("slime_fire", new EnemyData(
            "slime_fire",
            AtlasAsset.OBJECTS,
            "enemies/slime_fire",
            "enemies/slime_fire/idle_down",
            1,
            13f,
            8f,
            1,
            1.5f,

            18f,
            14f,
            10f
        ));
        enemies.put("orc_2", new EnemyData(
            "orc_2",
            AtlasAsset.OBJECTS,
            "enemies/orc_2",
            "enemies/orc_2/idle_down",
            1,
            13f,
            8f,
            1,
            1.5f,

            18f,
            14f,
            10f
        ));
        enemies.put("vampire_3", new EnemyData(
            "vampire_3",
            AtlasAsset.OBJECTS,
            "enemies/vampire_3",
            "enemies/vampire_3/idle_down",
            1,
            13f,
            8f,
            1,
            1.5f,

            18f,
            14f,
            10f
        ));
        enemies.put("plant_3", new EnemyData(
            "plant_3",
            AtlasAsset.OBJECTS,
            "enemies/plant_3",
            "enemies/plant_3/idle_down",
            1,
            13f,
            8f,
            1,
            1.5f,

            18f,
            14f,
            10f
        ));

        //AUTUM

        enemies.put("plant_2", new EnemyData(
            "plant_2",
            AtlasAsset.OBJECTS,
            "enemies/plant_2",
            "enemies/plant_2/idle_down",
            1,
            13f,
            8f,
            1,
            1.5f,

            18f,
            14f,
            10f
        ));

        enemies.put("orc_3", new EnemyData(
            "orc_3",
            AtlasAsset.OBJECTS,
            "enemies/orc_3",
            "enemies/orc_3/idle_down",
            1,
            13f,
            8f,
            1,
            1.5f,

            18f,
            14f,
            10f
        ));

        enemies.put("slime_angry", new EnemyData(
            "slime_angry",
            AtlasAsset.OBJECTS,
            "enemies/slime_angry",
            "enemies/slime_angry/idle_down",
            1,
            13f,
            8f,
            1,
            1.5f,

            18f,
            14f,
            10f
        ));

        enemies.put("vampire_2", new EnemyData(
            "vampire_2",
            AtlasAsset.OBJECTS,
            "enemies/vampire_2",
            "enemies/vampire_2/idle_down",
            1,
            13f,
            8f,
            1,
            1.5f,

            18f,
            14f,
            10f
        ));

    }

    public EnemyData getEnemyData(String enemyId) {
        return enemies.get(enemyId);
    }

}
