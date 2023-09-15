package se.simpor.system

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import ktx.app.gdxError
import ktx.log.logger
import ktx.collections.map
import se.simpor.component.AnimationComponent
import se.simpor.component.AnimationComponent.Companion.NO_ANIMATION
import se.simpor.component.ImageComponent

class AnimationSystem(
    private val textureAtlas: TextureAtlas,
) : IteratingSystem(
    World.family { all(ImageComponent, AnimationComponent) },
) {
    private val cachedAnimations = mutableMapOf<String, Animation<TextureRegionDrawable>>()
    override fun onTickEntity(entity: Entity) {
        val animationComponent = entity[AnimationComponent]

        if (animationComponent.nextAnimation == NO_ANIMATION) {
            animationComponent.stateTime += deltaTime

        } else {
            animationComponent.animation = animation(animationComponent.nextAnimation)
            animationComponent.stateTime = 0f
            animationComponent.nextAnimation = NO_ANIMATION
        }

        animationComponent.animation.playMode = animationComponent.playMode
        val frame = animationComponent.animation.getKeyFrame(animationComponent.stateTime)
        entity[ImageComponent].image.drawable = frame
    }

    private fun animation(keyPath: String): Animation<TextureRegionDrawable> {
        return cachedAnimations.getOrPut(keyPath) {
            log.debug { "New animation is created for '$keyPath'" }
            val regions = textureAtlas.findRegions(keyPath)
            if (regions.isEmpty) {
                gdxError("There are no texture regions for $keyPath")
            }
            Animation(DEFAULT_FRAME_DURATION, regions.map { TextureRegionDrawable(it) })
        }
    }


    companion object {
        private val log = logger<AnimationSystem>()
        private const val DEFAULT_FRAME_DURATION = 1 / 8f

    }

}
