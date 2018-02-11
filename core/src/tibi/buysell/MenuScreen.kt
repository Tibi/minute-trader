package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.app.clearScreen
import tibi.buysell.BuySellGame.Duration.ONE
import tibi.buysell.BuySellGame.Duration.THREE


class MenuScreen(val game: BuySellGame) : KtxScreen {
    val ui = MenuStage(game)

    override fun show() {
        Gdx.input.inputProcessor = ui
        game.highScores.forEach { duration, score ->
            if (score > 0) {
                ui.labels[duration]?.setText("High Score: %,d".format(score))
            }
        }
    }

    override fun render(delta: Float) {
        clearScreen(.9f, .95f, 1f, 1f)
        ui.act()
        ui.draw()
    }

    override fun resize(width: Int, height: Int) {
        ui.viewport.update(width, height, true)
    }
}


class MenuStage(val game: BuySellGame) : Stage(ScreenViewport(), game.batch) {

    val buttons = mapOf(ONE to button(ONE), THREE to button(THREE))
    val labels = mapOf(ONE to Label("", game.skin), THREE to Label("", game.skin))

    init {
        val table = Table()
        listOf(ONE, THREE).forEach {
            table.add(buttons[it]).minWidth(200f).center().pad(30f)
            table.add(labels[it]).row()
        }
        table.setFillParent(true)
        addActor(table)
    }

    fun button(duration: BuySellGame.Duration) =
            TextButton("${duration.description} Challenge", game.skin, "green").apply {
                onClick { game.play(duration) }
            }

}