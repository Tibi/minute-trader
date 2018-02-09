package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.app.clearScreen
import tibi.buysell.BuySellGame.Duration.FIVE
import tibi.buysell.BuySellGame.Duration.ONE


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
}

class MenuStage(val game: BuySellGame) : Stage() {

    val buttons = mapOf(ONE to button(ONE), FIVE to button(FIVE))
    val labels = mapOf(ONE to Label("", game.skin), FIVE to Label("", game.skin))

    init {
        val table = Table()
        listOf(ONE, FIVE).forEach {
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