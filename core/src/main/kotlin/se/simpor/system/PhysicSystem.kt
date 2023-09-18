package se.simpor.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World.Companion.family
import com.github.quillraven.fleks.collection.compareEntityBy
import ktx.app.gdxError
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2
import se.simpor.component.ImageComponent
import se.simpor.component.PhysicComponent

class PhysicSystem(
    private val physicWorld: World,
) : IteratingSystem(
    family { all(ImageComponent, PhysicComponent) },
    comparator = compareEntityBy(ImageComponent),
    interval = Fixed(1 / 60f)
), EventListener, ContactListener {
    companion object {
        private val log = logger<PhysicSystem>()
    }

    init {
        physicWorld.setContactListener(this)
    }

    override fun onUpdate() {
        if (physicWorld.autoClearForces) {
            physicWorld.autoClearForces = false
            gdxError("Setting autoClearForces to false")
        }
        super.onUpdate()
        physicWorld.clearForces()
    }

    override fun onTick() {
        super.onTick()
        physicWorld.step(deltaTime, 6, 2)
    }

    override fun onTickEntity(entity: Entity) {
        val physicComponent = entity[PhysicComponent]
        val imageComponent = entity[ImageComponent]
        val position = physicComponent.body.position
        physicComponent.prevPos.set(physicComponent.body.position)

        if (!physicComponent.impulse.isZero) {
            log.info { "Moving due to impulse: ${physicComponent.impulse.x}, ${physicComponent.impulse.y}" }
            physicComponent.body.applyLinearImpulse(physicComponent.impulse, physicComponent.body.worldCenter, true)
            physicComponent.impulse.setZero()
        }

        //  entity[ImageComponent].image.toFront()
    }

    override fun onAlphaEntity(entity: Entity, alpha: Float) {
        val imageCmp = entity[ImageComponent]
        val physicComponent = entity[PhysicComponent]

        imageCmp.image.run {
            val (prevX, prevY) = physicComponent.prevPos
            val (bodyX, bodyY) = physicComponent.body.position

            setPosition(
                MathUtils.lerp(prevX, bodyX, alpha) - width * 0.5f,
                MathUtils.lerp(prevY, bodyY, alpha) - height * 0.5f
            )
        }
    }

    override fun handle(event: Event?): Boolean {
        return true
    }

    override fun beginContact(contact: Contact?) {

    }

    override fun endContact(contact: Contact?) {

    }

    private fun Fixture.isDynamicBody() = this.body.type == BodyDef.BodyType.DynamicBody
    private fun Fixture.isStaticBody() = this.body.type == BodyDef.BodyType.StaticBody
    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        contact.isEnabled = (contact.fixtureA.isStaticBody() && contact.fixtureB.isDynamicBody()) ||
            (contact.fixtureB.isStaticBody() && contact.fixtureA.isDynamicBody())
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) = Unit
}
