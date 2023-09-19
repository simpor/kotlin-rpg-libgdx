package se.simpor.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import ktx.box2d.query
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2
import se.simpor.component.*
import se.simpor.system.EntitySpawnSystem.Companion.HIT_BOX_SENSOR

class AttackSystem(
    private val phWorld: World,
    private val stage: Stage,
) : IteratingSystem(family { all(AttackComponent, PhysicComponent, ImageComponent) }) {

    override fun onTickEntity(entity: Entity) {
        val attackCmp = entity[AttackComponent]
        if (attackCmp.state == AttackState.READY && !attackCmp.doAttack) {
            // no intention to attack -> do nothing
            return
        }

        if (attackCmp.doAttack && attackCmp.state == AttackState.PREPARE) {
            // attack intention and ready to attack -> start attack
            attackCmp.doAttack = false
            attackCmp.state = AttackState.ATTACKING
            attackCmp.delay = attackCmp.maxDelay
            return
        }
        log.info { "Entering attack system" }

        attackCmp.delay -= deltaTime
        if (attackCmp.delay <= 0f && attackCmp.state == AttackState.ATTACKING) {
            // deal damage to nearby enemies
            log.info { "deal damage to nearby enemies" }

            attackCmp.state = AttackState.DEAL_DAMAGE

//            entity.getOrNull(AnimationComponent)?.let { aniCmp ->
//                stage.fire(EntityAttackEvent(aniCmp.atlasKey))
//            }

            val image = entity[ImageComponent].image
            val physicCmp = entity[PhysicComponent]

            val attackLeft = image.flipX
            val (x, y) = physicCmp.body.position
            val (offX, offY) = physicCmp.offset
            val (w, h) = physicCmp.size
            val halfW = w * 0.5f
            val halfH = h * 0.5f

            if (attackLeft) {
                AABB_RECT.set(
                    offX + x - halfW - attackCmp.extraRange, offY + y - halfH, offX + x + halfW, offY + y + halfH
                )
            } else {
                AABB_RECT.set(
                    offX + x - halfW, offY + y - halfH, offX + x + halfW + attackCmp.extraRange, offY + y + halfH
                )
            }

            phWorld.query(AABB_RECT.x, AABB_RECT.y, AABB_RECT.width, AABB_RECT.height) { fixture ->
                if (fixture.userData != HIT_BOX_SENSOR) {
                    // we are only interested if we detect hit-boxes of other entities
                    return@query true
                }

                val fixtureEntity = fixture.body.userData as Entity
                if (fixtureEntity == entity) {
                    // ignore the entity itself that is attacking
                    return@query true
                }

                val isAttackerPlayer = entity has PlayerComponent
                if (isAttackerPlayer && fixtureEntity has PlayerComponent) {
                    // player does not damage other player entities
                    return@query true
                } else if (!isAttackerPlayer && fixtureEntity hasNo PlayerComponent) {
                    // non-player entities do not damage other non-player entities
                    return@query true
                }

                // fixtureEntity refers to another entity that gets hit by the attack
                fixtureEntity.configure {
                    it.getOrNull(LifeComponent)?.let { lifeCmp ->
                        lifeCmp.takeDamage += attackCmp.damage * MathUtils.random(0.9f, 1.1f)
                    }
                    if (isAttackerPlayer) {
                        // player can open chests
//                        it.getOrNull(LootComponent)?.let { lootCmp ->
//                            lootCmp.interactEntity = entity
//                        }
                    }
                }
                return@query true
            }

            attackCmp.state = AttackState.READY
        }

//        val isDone = entity.getOrNull(AnimationComponent)?.isAnimationFinished() ?: true
//        if (isDone) {
       // attackCmp.state = AttackState.READY
//        }
    }

    companion object {
        val AABB_RECT = Rectangle()
        private val log = logger<AttackSystem>()
    }
}
