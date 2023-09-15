package se.simpor.component

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.quillraven.fleks.Component
import com.github.quillraven.fleks.ComponentType


enum class AnimationType {
    IDLE, RUN, ATTACK, DEATH, OPEN;

    val atlasKey = this.toString().lowercase()
}

enum class AnimationModel {
    PLAYER, SLIME, CHEST, UNDEFINED;
    val atlasKey = this.toString().lowercase()
}

class AnimationComponent(
    var model: AnimationModel = AnimationModel.UNDEFINED,
    var stateTime: Float = 0f,
    var playMode: Animation.PlayMode = Animation.PlayMode.LOOP

) : Component<AnimationComponent> {
    lateinit var animation: Animation<TextureRegionDrawable>
    var nextAnimation = ""
    companion object : ComponentType<AnimationComponent>() {
        val NO_ANIMATION = ""
    }



    fun nextAnimation(model:AnimationModel, type: AnimationType) {
        this.model = model
        nextAnimation = "${model.atlasKey}/${type.atlasKey}"
    }

    override fun type(): ComponentType<AnimationComponent> = AnimationComponent
}
