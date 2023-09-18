package se.simpor.system

import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import ktx.log.logger
import ktx.tiled.height
import ktx.tiled.width
import se.simpor.component.ImageComponent
import se.simpor.component.PlayerComponent
import se.simpor.event.MapChangedEvent

class CameraSystem(
    stage: Stage
) : IteratingSystem(
    family { all(PlayerComponent, ImageComponent) }
), EventListener {
    companion object {
        private val log = logger<CameraSystem>()
    }

    private val camera = stage.camera
    private var maxHeight = 0f
    private var maxWidth = 0f

    override fun onTickEntity(entity: Entity) {
        val imageComponent = entity[ImageComponent]
        val playerComponent = entity[PlayerComponent]

        with(imageComponent) {

            val viewWidth = camera.viewportWidth * 0.5f
            val viewHeight = camera.viewportHeight * 0.5f

            camera.position.set(
                image.x.coerceIn(viewWidth, maxWidth - viewWidth),
                image.y.coerceIn(viewHeight, maxHeight - viewHeight),
                camera.position.z
            )
        }

    }

    override fun handle(event: Event?): Boolean {
        if (event is MapChangedEvent) {
            maxWidth = event.map.width.toFloat()
            maxHeight = event.map.height.toFloat()
            return true
        }
        return false
    }
}
