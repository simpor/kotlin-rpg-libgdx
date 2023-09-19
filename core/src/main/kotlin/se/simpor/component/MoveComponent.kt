package se.simpor.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType


data class MoveComponent(
    var sin: Float = 0f,
    var cos: Float = 0f,
    var speed: Float = 0f
) : Component<MoveComponent> {
    override fun type() = MoveComponent

    companion object : ComponentType<MoveComponent>()
}
