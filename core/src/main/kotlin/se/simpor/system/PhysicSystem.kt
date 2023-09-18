package se.simpor.system

import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.collection.compareEntityBy
import ktx.app.gdxError
import ktx.log.logger
import se.simpor.component.ImageComponent
import se.simpor.component.PhysicComponent

class PhysicSystem(
    private val physicWorld: World,
) : IteratingSystem(
    family { all(ImageComponent, PhysicComponent) },
    comparator = compareEntityBy(ImageComponent),
    interval = Fixed(1 / 60f)
), EventListener {
    companion object {
        private val log = logger<EntitySpawnSystem>()
    }
    override fun onUpdate() {
        if (physicWorld.autoClearForces) {
            gdxError("Setting autoClearForces to false")
            physicWorld.autoClearForces = false
        }
        super.onUpdate()
        physicWorld.clearForces()
    }

    override fun onTick() {
        super.onTick()
        physicWorld.step(deltaTime, 6, 2)
    }

    override fun onTickEntity(entity: Entity) {
        val physicComponent = entity[PhysicComponent]
        val imageComponent = entity[ImageComponent]
        val position = physicComponent.body.position
        imageComponent.image.run {
            setPosition(position.x - width * 0.5f, position.y - height * 0.5f)
        }
        entity[ImageComponent].image.toFront()
    }

    override fun handle(event: Event?): Boolean {
        return true
    }

}
