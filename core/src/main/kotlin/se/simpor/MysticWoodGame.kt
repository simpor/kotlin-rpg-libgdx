package se.simpor

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import ktx.app.KtxGame
import ktx.app.KtxScreen
import se.simpor.screen.GameScreen

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms. */
class MysticWoodGame : KtxGame<KtxScreen>() {

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        addScreen(GameScreen())
        setScreen<GameScreen>()
    }
}
