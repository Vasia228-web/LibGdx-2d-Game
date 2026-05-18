package com.github.vasia228web.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.vasia228web.combat.CombatHitboxFactory;
import com.github.vasia228web.component.*;
import com.github.vasia228web.component.Animation2D.AnimationType;
import com.github.vasia228web.component.Facing.FacingDirection;
import com.github.vasia228web.input.Controller;
import com.github.vasia228web.pathfinding.AStarPathfinder;
import com.github.vasia228web.pathfinding.EnemyPathNavigator;

public class EnemyAISystem extends IteratingSystem {

    private static final float BODY_CONTACT_MARGIN = 0.02f;

    private final ImmutableArray<Entity> players;
    private final EnemyPathNavigator pathNavigator;
    private final CombatHitboxFactory combatHitboxFactory;

    public EnemyAISystem(Engine engine) {
        super(Family.all(
            Enemy.class,
            EnemyAI.class,
            EnemyAttack.class,
            Move.class,
            Transform.class,
            Facing.class,
            Animation2D.class,
            PathFollow.class,
            Physic.class
        ).get());

        this.players = engine.getEntitiesFor(
            Family.all(Controller.class, Transform.class, Physic.class).get()
        );

        this.pathNavigator = new EnemyPathNavigator();
        this.combatHitboxFactory = new CombatHitboxFactory();
    }

    @Override
    protected void processEntity(Entity enemy, float deltaTime) {
        if (players.size() == 0) {
            patrol(enemy);
            return;
        }

        Entity player = players.first();

        EnemyAI enemyAI = EnemyAI.MAPPER.get(enemy);
        EnemyAttack enemyAttack = EnemyAttack.MAPPER.get(enemy);

        updateCooldown(enemyAttack, deltaTime);

        Vector2 enemyCenter = combatHitboxFactory.createBodyCenter(enemy);
        Vector2 playerCenter = combatHitboxFactory.createBodyCenter(player);

        float distanceToPlayer = enemyCenter.dst(playerCenter);

        boolean playerInsideAttackBox = isPlayerInMeleeRange(enemy, player);
        boolean enemyIsAttacking = enemyAttack.attacking;

        if (enemyIsAttacking || playerInsideAttackBox) {
            enemyAI.state = EnemyAI.State.ATTACK;
            attack(enemy, player);
        } else if (distanceToPlayer <= enemyAI.aggroRange) {
            enemyAI.state = EnemyAI.State.CHASE;
            chase(enemy, player, deltaTime);
        } else {
            enemyAI.state = EnemyAI.State.PATROL;
            patrol(enemy);
        }
    }

    private void updateCooldown(EnemyAttack enemyAttack, float deltaTime) {
        if (enemyAttack.cooldownTimer <= 0f) {
            return;
        }

        enemyAttack.cooldownTimer -= deltaTime;

        if (enemyAttack.cooldownTimer < 0f) {
            enemyAttack.cooldownTimer = 0f;
        }
    }

    private boolean isPlayerInMeleeRange(Entity enemy, Entity player) {
        faceTarget(enemy, player);

        Rectangle attackBox = combatHitboxFactory.createEnemyAttackBox(enemy);
        Rectangle enemyBox = combatHitboxFactory.createBodyBox(enemy);
        Rectangle playerBox = combatHitboxFactory.createBodyBox(player);

        return attackBox.overlaps(playerBox)
            || rectanglesOverlapWithMargin(enemyBox, playerBox, BODY_CONTACT_MARGIN);
    }

    private void patrol(Entity enemy) {
        EnemyAI enemyAI = EnemyAI.MAPPER.get(enemy);
        Transform transform = Transform.MAPPER.get(enemy);
        Move move = Move.MAPPER.get(enemy);
        Animation2D animation2D = Animation2D.MAPPER.get(enemy);
        PathFollow pathFollow = PathFollow.MAPPER.get(enemy);

        pathFollow.clear();

        float currentX = transform.getPosition().x;

        if (currentX > enemyAI.spawnX + enemyAI.patrolRadius) {
            enemyAI.patrolDirection = -1;
        } else if (currentX < enemyAI.spawnX - enemyAI.patrolRadius) {
            enemyAI.patrolDirection = 1;
        }

        move.getDirection().set(enemyAI.patrolDirection, 0f);

        animation2D.setPlayMode(Animation.PlayMode.LOOP);
        animation2D.setType(AnimationType.IDLE);
    }

    private void chase(Entity enemy, Entity player, float deltaTime) {
        pathNavigator.chase(enemy, player, deltaTime);
    }

    private void attack(Entity enemy, Entity player) {
        stopMovementOnly(enemy);

        faceTarget(enemy, player);

        EnemyAttack enemyAttack = EnemyAttack.MAPPER.get(enemy);
        Animation2D animation2D = Animation2D.MAPPER.get(enemy);
        Health playerHealth = Health.MAPPER.get(player);

        if (enemyAttack.cooldownTimer > 0f && !enemyAttack.attacking) {
            animation2D.setPlayMode(Animation.PlayMode.LOOP);
            animation2D.setType(AnimationType.IDLE);
            return;
        }

        if (!enemyAttack.attacking) {
            enemyAttack.attacking = true;
            enemyAttack.damageDone = false;

            animation2D.setPlayMode(Animation.PlayMode.NORMAL);
            animation2D.setType(AnimationType.ATTACK);


            return;
        }

        animation2D.setPlayMode(Animation.PlayMode.NORMAL);
        animation2D.setType(AnimationType.ATTACK);

        if (!enemyAttack.damageDone && animation2D.getStateTime() >= enemyAttack.damageTime) {
            Rectangle attackBox = combatHitboxFactory.createEnemyAttackBox(enemy);
            Rectangle enemyBox = combatHitboxFactory.createBodyBox(enemy);
            Rectangle playerBox = combatHitboxFactory.createBodyBox(player);

            boolean hit = attackBox.overlaps(playerBox)
                || rectanglesOverlapWithMargin(enemyBox, playerBox, BODY_CONTACT_MARGIN);

            if (hit) {
                if (playerHealth != null) {
                    playerHealth.damage(enemyAttack.damage);

                }
            }

            enemyAttack.damageDone = true;
        }

        if (animation2D.isFinished()) {
            enemyAttack.attacking = false;
            enemyAttack.damageDone = false;
            enemyAttack.cooldownTimer = enemyAttack.cooldown;

            animation2D.setPlayMode(Animation.PlayMode.LOOP);
            animation2D.setType(AnimationType.IDLE);

        }
    }

    private void stopMovementOnly(Entity enemy) {
        Move move = Move.MAPPER.get(enemy);
        PathFollow pathFollow = PathFollow.MAPPER.get(enemy);

        move.getDirection().setZero();
        pathFollow.clear();
    }

    private void faceTarget(Entity enemy, Entity player) {
        Facing facing = Facing.MAPPER.get(enemy);
        Vector2 direction = combatHitboxFactory.createBodyCenter(player)
            .sub(combatHitboxFactory.createBodyCenter(enemy));

        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            if (direction.x > 0f) {
                facing.setDirection(FacingDirection.RIGHT);
            } else {
                facing.setDirection(FacingDirection.LEFT);
            }
        } else {
            if (direction.y > 0f) {
                facing.setDirection(FacingDirection.UP);
            } else {
                facing.setDirection(FacingDirection.DOWN);
            }
        }
    }

    private boolean rectanglesOverlapWithMargin(Rectangle first, Rectangle second, float margin) {
        return first.x < second.x + second.width + margin
            && first.x + first.width > second.x - margin
            && first.y < second.y + second.height + margin
            && first.y + first.height > second.y - margin;
    }

    public void setPathfinder(AStarPathfinder pathfinder) {
        this.pathNavigator.setPathfinder(pathfinder);
    }
}
