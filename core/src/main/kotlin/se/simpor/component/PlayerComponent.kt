package se.simpor.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType


class PlayerComponent : Component<PlayerComponent> {
    override fun type() = PlayerComponent

    companion object : ComponentType<PlayerComponent>()
}
