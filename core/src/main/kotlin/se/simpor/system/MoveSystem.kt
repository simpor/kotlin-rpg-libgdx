package se.simpor.system

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.collection.compareEntityBy
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2
import se.simpor.component.ImageComponent
import se.simpor.component.MoveComponent
import se.simpor.component.PhysicComponent

class MoveSystem : IteratingSystem(
    family { all(ImageComponent, PhysicComponent) },
    comparator = compareEntityBy(ImageComponent),
    interval = Fixed(1 / 60f)
), EventListener {
    companion object {
        private val log = logger<MoveSystem>()
    }

    override fun onTickEntity(entity: Entity) {
        val moveComponent = entity[MoveComponent]
        val physicComponent = entity[PhysicComponent]
        val mass = physicComponent.body.mass
        val (velX, velY) = physicComponent.body.linearVelocity
        if ((moveComponent.cos == 0f && moveComponent.sin == 0f)) {
            // no direction for movement or entity is rooted
            if (!physicComponent.body.linearVelocity.isZero) {
                // entity is moving -> stop it
                val mass = physicComponent.body.mass
                val (velX, velY) = physicComponent.body.linearVelocity
                physicComponent.impulse.set(
                    mass * (0f - velX),
                    mass * (0f - velY)
                )
            }
        }
        physicComponent.impulse.set(
            mass * (moveComponent.speed * moveComponent.cos - velX),
            mass * (moveComponent.speed * moveComponent.sin - velY),
        )
    }

    override fun handle(event: Event?): Boolean {
        return true
    }
}
