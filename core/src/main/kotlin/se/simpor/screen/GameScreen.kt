package se.simpor.screen

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.configureWorld
import ktx.app.KtxScreen
import ktx.log.logger
import se.simpor.component.AnimationComponent
import se.simpor.component.AnimationModel
import se.simpor.component.AnimationType
import se.simpor.component.ImageComponent
import se.simpor.event.MapChangedEvent
import se.simpor.event.fire
import se.simpor.system.AnimationSystem
import se.simpor.system.EntitySpawnSystem
import se.simpor.system.RenderSystem

class GameScreen : KtxScreen {
    private val stage = Stage(ExtendViewport(16f, 9f))
    private val textureAtlas = TextureAtlas("assets/graphics/characters.atlas")
    private lateinit var currentMap: TiledMap

    private val world: World = configureWorld(entityCapacity = 1000) {
        injectables {
            add(stage)
            add(textureAtlas)
        }
        systems {
            add(RenderSystem())
            add(AnimationSystem())
            add(EntitySpawnSystem())
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

        world.systems.forEach { system -> if (system is EventListener) stage.addListener(system) }

        currentMap = TmxMapLoader().load("maps/demo2.tmx")
        stage.fire(MapChangedEvent(currentMap))
    }

    override fun resize(width: Int, height: Int) {
        with(stage) {
            viewport.update(width, height, true)
        }
    }

    override fun render(delta: Float) {
        world.update(delta)
    }

    override fun dispose() {
        stage.dispose()
        textureAtlas.dispose()
        world.dispose()
        currentMap.dispose()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
