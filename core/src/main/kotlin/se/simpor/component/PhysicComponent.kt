package se.simpor.component

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World
import ktx.app.gdxError
import ktx.box2d.body
import ktx.box2d.box
import ktx.box2d.circle
import ktx.box2d.loop
import ktx.math.vec2
import se.simpor.MysticWoodGame.Companion.UNIT_SCALE
import se.simpor.system.CollisionSpawnSystem.Companion.SPAWN_AREA_SIZE
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

                if (bodyType != BodyDef.BodyType.StaticBody) {
                    // collision box
                    box(w, h * 0.4f, spawnConfig.physicOffset)
                }
            }

        }

        fun physicCmpFromShape2D(
            world: PhysicWorld,
            x: Int,
            y: Int,
            shape: Shape2D,
            isPortal: Boolean = false,
        ): PhysicComponent {
            when (shape) {
                is Rectangle -> {
                    val bodyX = x + shape.x * UNIT_SCALE
                    val bodyY = y + shape.y * UNIT_SCALE
                    val bodyW = shape.width * UNIT_SCALE
                    val bodyH = shape.height * UNIT_SCALE

                    return PhysicComponent().apply {
                        body = world.body(BodyDef.BodyType.StaticBody) {
                            position.set(bodyX, bodyY)
                            fixedRotation = true
                            allowSleep = false
                            loop(
                                vec2(0f, 0f),
                                vec2(bodyW, 0f),
                                vec2(bodyW, bodyH),
                                vec2(0f, bodyH),
                            ) {
//                                filter.categoryBits = LightComponent.b2dEnvironment
//                                this.isSensor = isPortal
                            }
                            circle(SPAWN_AREA_SIZE + 2f) {
                                isSensor = true
                            }
//                            if (!isPortal) {
//                                TMP_VEC.set(bodyW * 0.5f, bodyH * 0.5f)
//                                box(SPAWN_AREA_SIZE + 4f, SPAWN_AREA_SIZE + 4f, TMP_VEC) {
//                                    isSensor = true
//                                }
//                            }
                        }
                    }
                }

                else -> gdxError("Shape $shape not supported")
            }
        }
    }
}
