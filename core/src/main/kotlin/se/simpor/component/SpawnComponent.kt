package se.simpor.component

import com.badlogic.gdx.math.Vector2
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import ktx.math.vec2

data class SpawnConfig(
    val model: AnimationModel
)

data class SpawnComponent(
    var type: String = "",
    var location: Vector2 = vec2()
) : Component<SpawnComponent> {
    companion object : ComponentType<SpawnComponent>() {
    }
    override fun type(): ComponentType<SpawnComponent> = SpawnComponent

}
