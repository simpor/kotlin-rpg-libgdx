package se.simpor.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.quillraven.fleks.IntervalSystem
import com.github.quillraven.fleks.World.Companion.inject
import ktx.log.logger

class DebugSystem(
    private val physicWorld: World,
    private val stage: Stage,
) : IntervalSystem(enabled = true) {
    private val physicRenderer by lazy { Box2DDebugRenderer() }
    private val profiler by lazy { GLProfiler(Gdx.graphics).apply { enable() } }
    private val shapeRenderer by lazy { ShapeRenderer() }
    private val camera = stage.camera
    companion object {
        private val log = logger<EntitySpawnSystem>()
    }
    override fun onTick() {
        stage.isDebugAll = true
        Gdx.graphics.setTitle(
            buildString {
                append("FPS:${Gdx.graphics.framesPerSecond},")
                append("DrawCalls:${profiler.drawCalls},")
                append("Binds:${profiler.textureBindings},")
                append("Entities:${world.numEntities}")
            }
        )
        physicRenderer.render(physicWorld, camera.combined)

    }

    override fun onDispose() {
        if (enabled) {
            physicRenderer.dispose()
        }
    }
}
