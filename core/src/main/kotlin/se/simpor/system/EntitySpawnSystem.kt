package se.simpor.system

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.World.Companion.inject
import ktx.app.gdxError
import ktx.log.logger
import ktx.math.vec2
import ktx.tiled.layer
import ktx.tiled.type
import ktx.tiled.x
import ktx.tiled.y
import se.simpor.MysticWoodGame.Companion.UNIT_SCALE
import se.simpor.component.*
import se.simpor.event.MapChangedEvent
import se.simpor.screen.GameScreen

class EntitySpawnSystem(
    private val atlas: TextureAtlas = inject()
) : IteratingSystem(
    World.family { all(SpawnComponent) },
    enabled = true
), EventListener {

    private val cachedConfigs = mutableMapOf<String, SpawnConfig>()
    private val cachedSize = mutableMapOf<AnimationModel, Vector2>()

    companion object {
        private val log = logger<GameScreen>()
    }

    private fun spawnConfig(type: String) = cachedConfigs.getOrPut(type) {
        when (type.uppercase()) {
            "PLAYER" -> SpawnConfig(AnimationModel.PLAYER)
            "SLIME" -> SpawnConfig(AnimationModel.SLIME)
            "CHEST" -> SpawnConfig(AnimationModel.SLIME)
            else -> gdxError("Type $type does not have a SpawnConfig")
        }
    }

    private fun size(model: AnimationModel) = cachedSize.getOrPut(model) {
        val regions = atlas.findRegions("${model.atlasKey}/${AnimationType.IDLE.atlasKey}")
        if (regions.isEmpty) gdxError("There are no regions for the idle animation of model $model")

        val firstFrame = regions.first()
        vec2(firstFrame.originalWidth * UNIT_SCALE, firstFrame.originalHeight * UNIT_SCALE)
    }

    override fun handle(event: Event): Boolean {
        log.info { "Spawning entities defined in map" }
        when (event) {
            is MapChangedEvent -> {

                val entityLayer = event.map.layer("entities")
                entityLayer.objects.forEach { mapObject ->
                    val type = mapObject.name ?: gdxError("Mapobject ${mapObject.type} does not have a type")
                    log.info { "Spawning $type" }
                    val spawnComponent = SpawnComponent().apply {
                        this.type = type
                        this.location.set(mapObject.x * UNIT_SCALE, mapObject.y * UNIT_SCALE)
                    }
                    world.entity { it += spawnComponent }
                    with(spawnComponent) {
                        val config = spawnConfig(this.type)
                        val relativeSize = size(config.model)
                        world.entity {
                            log.info {"Spawning an entity: ${config.model}"}
                            it += ImageComponent().apply {
                                image = Image().apply {
                                    setPosition(location.x, location.y)
                                    setSize(relativeSize.x, relativeSize.y)
                                    setScaling(com.badlogic.gdx.utils.Scaling.fill)
                                }
                            }
                            it += AnimationComponent().apply {
                                nextAnimation(config.model, se.simpor.component.AnimationType.IDLE)
                            }
                        }
                    }

                }
                return true
            }
        }
        return false

    }

    override fun onTickEntity(entity: Entity) {

    }
}
