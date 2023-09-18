package se.simpor.component

import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType

class CollisionComponent : Component<CollisionComponent> {
    override fun type() = CollisionComponent

    companion object : ComponentType<CollisionComponent>()
}
