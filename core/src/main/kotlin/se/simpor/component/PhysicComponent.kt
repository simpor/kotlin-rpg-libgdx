package se.simpor.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import ktx.box2d.body
import ktx.box2d.box
import ktx.math.vec2
import com.badlogic.gdx.physics.box2d.World as PhysicWorld

class PhysicComponent : Component<PhysicComponent> {
    val impulse: Vector2 = vec2()
    lateinit var body: Body
    val prevPos = vec2()


    override fun type() = PhysicComponent
    override fun World.onAdd(entity: Entity) {
        body.userData = entity
    }

    override fun World.onRemove(entity: Entity) {
        body.world.destroyBody(body)
        body.userData = null
    }

    companion object : ComponentType<PhysicComponent>() {
        fun createPhysicBody(physicWorld: PhysicWorld, image: Image, spawnConfig: SpawnConfig): Body {
            val x = image.x
            val y = image.y
            val width = image.width
            val height = image.height
            val bodyType = spawnConfig.bodyType


            val w = width * spawnConfig.scalePhysic.x
            val h = height * spawnConfig.scalePhysic.y

            return physicWorld.body(bodyType) {
                position.set(x + width * 0.5f, y + height * 0.5f)
                fixedRotation = true
                allowSleep = false

                box(w, h, spawnConfig.physicOffset) {
                 //   isSensor = bodyType != BodyDef.BodyType.StaticBody
                }
            }

        }
    }
}
