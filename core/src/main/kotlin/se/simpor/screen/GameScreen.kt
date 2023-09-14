package se.simpor.screen

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.World
import com.github.quillraven.fleks.configureWorld
import ktx.app.KtxScreen
import ktx.log.logger
import se.simpor.component.ImageComponent
import se.simpor.system.RenderSystem

class GameScreen : KtxScreen {
    private val stage = Stage(ExtendViewport(16f, 9f))
    private val textureAtlas = TextureAtlas("assets/graphics/characters.atlas")

    //    private val texture = Texture("assets/graphics/characters.png")
    private val world: World = configureWorld(entityCapacity = 1000) {
        injectables {
            add(stage)
        }
        systems {
            add(RenderSystem(stage))
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

        world.entity {
            it += ImageComponent().apply {
                image = Image(TextureRegion(textureAtlas.findRegion("player/idle"), 0, 0, 48, 48)).apply {
                    setPosition(1f, 1f)
                    setSize(1f, 1f)
                    setScaling(Scaling.fill)
                }
            }
        }
        world.entity {
            it += ImageComponent().apply {
                image = Image(TextureRegion(textureAtlas.findRegion("slime/idle"), 0, 0, 32, 32)).apply {
                    setPosition(1f, 2f)
                    setSize(2f, 1f)
                    setScaling(Scaling.fill)
                }
            }

        }

    }

    override fun resize(width: Int, height: Int) {
        with(stage) {
            stage.viewport.update(width, height, true)
        }
    }

    override fun render(delta: Float) {
        world.update(delta)
    }

    override fun dispose() {
        stage.dispose()
        textureAtlas.dispose()
        world.dispose()
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
