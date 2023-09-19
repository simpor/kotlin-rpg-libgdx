package se.simpor.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys.*
import com.github.quillraven.fleks.World
import ktx.app.KtxInputAdapter
import se.simpor.component.AttackComponent
import se.simpor.component.MoveComponent
import se.simpor.component.PlayerComponent

class PlayerKeyboardInputProcessor(
    private val world: World,
) : KtxInputAdapter {
    init {
        Gdx.input.inputProcessor = this
    }

    private val playerEntities = world.family { all(PlayerComponent) }
    private var playerSin: Float = 0f
    private var playerCos: Float = 0f

    private fun Int.isMovementKey(): Boolean {
        return this == UP || this == DOWN || this == RIGHT || this == LEFT
    }

    private fun isPressed(keycode: Int): Boolean = Gdx.input.isKeyPressed(keycode)

    override fun keyDown(keycode: Int): Boolean {

        if (keycode.isMovementKey()) {
            when (keycode) {
                UP -> playerSin = 1f
                DOWN -> playerSin = -1f
                RIGHT -> playerCos = 1f
                LEFT -> playerCos = -1f
            }
        } else if (keycode == SPACE) {
            playerEntities.forEach {
                it[AttackComponent].doAttack = true
                it[AttackComponent].startAttack()
            }

            return true
        }
            updatePlayerMovement()
        return false
    }

    private fun updatePlayerMovement() {
        playerEntities.forEach { player ->
            with(player[MoveComponent]) {
                sin = playerSin
                cos = playerCos
            }
        }
    }

    override fun keyUp(keycode: Int): Boolean {
        if (keycode.isMovementKey()) {
            when (keycode) {
                UP -> playerSin = if (isPressed(DOWN)) -1f else 0f
                DOWN -> playerSin = if (isPressed(UP)) 1f else 0f
                RIGHT -> playerCos = if (isPressed(LEFT)) -1f else 0f
                LEFT -> playerCos = if (isPressed(RIGHT)) 1f else 0f
            }
        }
        updatePlayerMovement()
        return false
    }
}
