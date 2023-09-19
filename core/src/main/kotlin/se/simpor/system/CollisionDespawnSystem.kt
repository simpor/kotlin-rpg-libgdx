package se.simpor.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import se.simpor.component.TiledComponent
import se.simpor.event.CollisionDespawnEvent
import se.simpor.event.fire

class CollisionDespawnSystem(
    private val stage: Stage
) : IteratingSystem(family { all(TiledComponent) }) {
    override fun onTickEntity(entity: Entity) {
        // for existing collision tiled entities we check if there are no nearby entities anymore
        // and remove them in that case
        val tiledCmp = entity[TiledComponent]
        if (tiledCmp.nearbyEntities.isEmpty()) {
            stage.fire(CollisionDespawnEvent(tiledCmp.cell))
            entity.remove()
        }
    }
}
