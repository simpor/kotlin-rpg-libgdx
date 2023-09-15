package se.simpor.system

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
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
import se.simpor.MysticWoodGame
import se.simpor.MysticWoodGame.Companion.UNIT_SCALE
import se.simpor.component.AnimationModel
import se.simpor.component.AnimationType
import se.simpor.component.SpawnComponent
import se.simpor.component.SpawnConfig
import se.simpor.event.MapChangedEvent

class EntitySpawnSystem(
    private val atlas: TextureAtlas = inject()
) : IteratingSystem(
    World.family { all(SpawnComponent) },
    enabled = true
), EventListener {

    private val cachedSize = mutableMapOf<AnimationModel, Vector2>()
    private val cachedConfigs = mutableMapOf<String, SpawnConfig>()


    companion object {
        private val log = logger<EntitySpawnSystem>()
    }

    private fun size(model: AnimationModel) = cachedSize.getOrPut(model) {
        val regions = atlas.findRegions("${model.atlasKey}/${AnimationType.IDLE.atlasKey}")
        if (regions.isEmpty) gdxError("There are no regions for the idle animation of model $model")

        val firstFrame = regions.first()
        vec2(
            firstFrame.originalWidth * MysticWoodGame.UNIT_SCALE,
            firstFrame.originalHeight * MysticWoodGame.UNIT_SCALE
        )
    }

    private fun spawnConfig(type: String) = cachedConfigs.getOrPut(type) {
        when (type.uppercase()) {
            "PLAYER" -> SpawnConfig(AnimationModel.PLAYER)
            "SLIME" -> SpawnConfig(AnimationModel.SLIME)
            "CHEST" -> SpawnConfig(AnimationModel.SLIME)
            else -> gdxError("Type $type does not have a SpawnConfig")
        }
    }

    override fun handle(event: Event): Boolean {
        log.info { "Spawning entities defined in map" }
        when (event) {
            is MapChangedEvent -> {
                val entityLayer = event.map.layer("entities")
                entityLayer.objects.forEach { mapObject ->
                    val type = mapObject.name ?: gdxError("Mapobject ${mapObject.type} does not have a type")
                    log.info { "Spawning $type" }
                    val spawnComponent = SpawnComponent(
                        type = type,
                        animationModel = spawnConfig(type).model,
                        relativeSize = size(spawnConfig(type).model),
                        location = vec2(mapObject.x * UNIT_SCALE, mapObject.y * UNIT_SCALE)
                    )
                    world.entity { it += spawnComponent }
                }
                return true
            }
        }
        return false

    }

    override fun onTickEntity(entity: Entity) {

    }
}
