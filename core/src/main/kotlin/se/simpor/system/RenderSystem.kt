package se.simpor.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.*
import com.github.quillraven.fleks.Family
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.collection.compareEntity
import com.github.quillraven.fleks.collection.compareEntityBy
import ktx.graphics.use
import ktx.tiled.forEachLayer
import se.simpor.component.ImageComponent
import se.simpor.event.MapChangedEvent

class RenderSystem(
    private val stage: Stage
) : IteratingSystem(
    family { all(ImageComponent) },
    comparator = compareEntityBy(ImageComponent)
), EventListener {

    private val backgroundLayer = mutableListOf<TiledMapTileLayer>()
    private val foregroundLayer = mutableListOf<TiledMapTileLayer>()
    private val mapRenderer = OrthogonalTiledMapRenderer(null, 1 / 16f, stage.batch)
    private val ortCamera = stage.camera as OrthographicCamera

    override fun onTickEntity(entity: Entity) {
        entity[ImageComponent].image.toFront()
    }

    override fun onTick() {
        super.onTick()

        with(stage) {
            viewport.apply()
            AnimatedTiledMapTile.updateAnimationBaseTime()
            mapRenderer.setView(ortCamera)
            if (backgroundLayer.isNotEmpty()) {
                stage.batch.use(ortCamera.combined) {
                    backgroundLayer.forEach { mapRenderer.renderTileLayer(it) }
                }
            }


            act(deltaTime)
            draw()

            if (foregroundLayer.isNotEmpty()) {
                stage.batch.use(ortCamera.combined) {
                    foregroundLayer.forEach { mapRenderer.renderTileLayer(it) }
                }
            }
        }
    }

    override fun handle(event: Event): Boolean {
        when (event) {
            is MapChangedEvent -> {
                event.map.forEachLayer<TiledMapTileLayer> { layer ->
                    if (layer.name.startsWith("fgd_")) {
                        foregroundLayer.add(layer)
                    } else {
                        backgroundLayer.add(layer)
                    }
                }
                return true
            }
        }
        return false

    }
}
