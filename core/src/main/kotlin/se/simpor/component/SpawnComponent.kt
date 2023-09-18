package se.simpor.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import ktx.log.logger
import ktx.math.vec2

data class SpawnConfig(
    val model: AnimationModel
)

data class SpawnComponent(
    var type: String = "",
    var animationModel: AnimationModel = AnimationModel.UNDEFINED,
    var relativeSize: Vector2 = vec2(),
    var location: Vector2 = vec2()
) : Component<SpawnComponent> {

    companion object : ComponentType<SpawnComponent>()

    override fun type(): ComponentType<SpawnComponent> = SpawnComponent

}
