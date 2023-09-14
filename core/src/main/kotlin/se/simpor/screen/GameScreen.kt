package se.simpor.screen

import ktx.app.KtxScreen
import ktx.log.logger

class GameScreen : KtxScreen {

    override fun show() {
        log.debug { "GameScreen get shown" }
    }

    companion object {
        private val log = logger<GameScreen>()
    }
}
