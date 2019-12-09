package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen
import ktx.app.clearScreen
import tibi.buysell.BuySellGame.MyColors.BG

/**
 * Base class for simple UI screens
 */
open class UiScreen(val game: BuySellGame): KtxScreen, InputAdapter() {

    val stage = Stage(FitViewport(800f, 300f), game.batch)

    override fun show() {
        Gdx.input.inputProcessor = this // TODO should be the stage, and the components should handle the events
    }

    override fun render(delta: Float) {
        clearScreen(BG.col.r, BG.col.g, BG.col.b)
        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        game.setScreen<MenuScreen>()
        return true
    }
}
