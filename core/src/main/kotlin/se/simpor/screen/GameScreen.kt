package se.simpor.screen

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.configureWorld
import ktx.app.KtxScreen
import ktx.box2d.createWorld
import ktx.log.logger
import ktx.math.vec2
import se.simpor.component.ImageComponent
import se.simpor.event.MapChangedEvent
import se.simpor.event.fire
import se.simpor.input.PlayerKeyboardInputProcessor
import se.simpor.system.*

class GameScreen : KtxScreen {
    private val stage = Stage(ExtendViewport(16f, 9f))
    private val textureAtlas = TextureAtlas("assets/graphics/characters.atlas")
    private lateinit var currentMap: TiledMap
    private val physicWorld = createWorld(gravity = vec2()).apply {
        autoClearForces = false
    }

    private val entityWorld: World = configureWorld(entityCapacity = 1000) {
        injectables {
            add(stage)
            add(textureAtlas)
            add(physicWorld)
        }
        systems {
            add(EntitySpawnSystem(textureAtlas, physicWorld))
            add(MoveSystem())
            add(PhysicSystem(physicWorld))
            add(AnimationSystem())
            add(RenderSystem())
            add(DebugSystem(physicWorld, stage))
        }

        onAddEntity { entity ->
            if (entity has ImageComponent) {
                stage.addActor(entity[ImageComponent].image)
            }
        }

        onRemoveEntity { entity ->
            if (entity has ImageComponent) {
                stage.root.removeActor(entity[ImageComponent].image)
            }
        }

    }


    override fun show() {
        log.debug { "GameScreen get shown" }

        entityWorld.systems.forEach { system -> if (system is EventListener) stage.addListener(system) }

        currentMap = TmxMapLoader().load("maps/demo2.tmx")
        stage.fire(MapChangedEvent(currentMap))

       PlayerKeyboardInputProcessor(world = entityWorld)
    }

    override fun resize(width: Int, height: Int) {
        with(stage) {
            viewport.update(width, height, true)
        }
    }

    override fun render(delta: Float) {
        entityWorld.update(delta.coerceAtMost(0.25f))
    }

    override fun dispose() {
        stage.dispose()
        textureAtlas.dispose()
        entityWorld.dispose()
        currentMap.dispose()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
