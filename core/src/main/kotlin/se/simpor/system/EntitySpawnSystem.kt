package se.simpor.system

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import ktx.app.gdxError
import ktx.log.logger
import ktx.math.vec2
import ktx.tiled.layer
import ktx.tiled.type
import ktx.tiled.x
import ktx.tiled.y
import se.simpor.MysticWoodGame
import se.simpor.MysticWoodGame.Companion.UNIT_SCALE
import se.simpor.component.*
import se.simpor.component.SpawnConfig.Companion.DEFAULT_SPEED
import se.simpor.event.MapChangedEvent
import com.badlogic.gdx.physics.box2d.World as PhysicWorld

class EntitySpawnSystem(
    private val atlas: TextureAtlas,
    private val physicWorld: PhysicWorld
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
            "PLAYER" -> SpawnConfig(
                AnimationModel.PLAYER,
                scalePhysic = vec2(0.3f, 0.3f),
                physicOffset = vec2(0f, -10f * UNIT_SCALE)
            )

            "SLIME" -> SpawnConfig(
                AnimationModel.SLIME,
                scalePhysic = vec2(0.3f, 0.3f),
                physicOffset = vec2(0f, -2f * UNIT_SCALE)
            )

            "CHEST" -> SpawnConfig(
                AnimationModel.CHEST,
                bodyType = BodyDef.BodyType.StaticBody,
                scaleSpeed = 0f
            )

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

        with(entity[SpawnComponent]) {
            world.entity {
                log.info { "Spawning an entity: $animationModel" }
                it += ImageComponent().apply {
                    image = Image().apply {
                        setPosition(location.x, location.y)
                        setSize(relativeSize.x, relativeSize.y)
                        setScaling(com.badlogic.gdx.utils.Scaling.fill)
                    }
                }
                it += AnimationComponent().apply {
                    nextAnimation(animationModel, AnimationType.IDLE)
                }
                it += PhysicComponent().apply {
                    body = PhysicComponent.createPhysicBody(
                        physicWorld,
                        it[ImageComponent].image,
                        spawnConfig(type)
                    )
                }
                val scaleSpeed = spawnConfig(type).scaleSpeed
                if (scaleSpeed > 0) {
                    it += MoveComponent().apply {
                        speed = DEFAULT_SPEED * scaleSpeed
                    }
                }
                if (type == "PLAYER") {
                    it += PlayerComponent()
                }
            }
        }
        entity.remove() // removing the entity so we dont try and spawn it again
    }
}
