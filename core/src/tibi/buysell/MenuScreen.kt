package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys.ENTER
import com.badlogic.gdx.Input.Keys.SPACE
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.i18n.get
import ktx.scene2d.table
import tibi.buysell.BuySellGame.Duration.ONE
import tibi.buysell.BuySellGame.Duration.THREE
import tibi.buysell.BuySellGame.MyColors.*

class MenuScreen(val game: BuySellGame) : KtxScreen {
    val stage = MenuStage(game)
    val txt = game.txt

    override fun show() {
        Gdx.input.inputProcessor = stage
        Gdx.input.setCatchKey(Input.Keys.BACK, false)  // let Back exit the app  TODO check if still needed
        listOf(ONE, THREE).forEach { duration ->
            val score = game.highScores.getInteger(duration.name)
            if (score > 0) {
                stage.labels[duration]?.setText(txt["highScore", score])
            }
        }
    }

    override fun render(delta: Float) {
        clearScreen(BG.col.r, BG.col.g, BG.col.b)
        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
}


class MenuStage(val game: BuySellGame) : Stage(FitViewport(1213f, 780f), game.batch) {

    val durationsToShow = listOf(ONE, THREE)
    val buttons = durationsToShow.associateWith { button(it) }
    val labels  = durationsToShow.associateWith { Label(game.txt["noHighscore"],
            game.skin, "small", DARK_TEXT.col) }

    init {
        addActor(table {
            // debug()
            top()
            pad(20f)
            setFillParent(true)
            add(Image(game.logo).apply { setScaling(Scaling.fit) }).top()//.expand()
            add(Table().apply {
                // debug()
                top()
                padTop(40f)
                columnDefaults(0).uniform().fillX()
                add(Image(game.title).apply { setScaling(Scaling.fit) }).colspan(2).row()
                add().height(100f).row()
                durationsToShow.forEach { dur ->
                    row().spaceBottom(20f)
                    add(buttons[dur]).left()
                    add(labels[dur]).right()
                }
                row()
                add(button("HELP") { game.help() })
            }).top()
        })
    }

    fun button(duration: BuySellGame.Duration) =
        button(game.txt["play"] + " " + duration.description) { game.play(duration) }

    fun button(text: String, action: () -> Unit) =
        TextButton(text, game.skin).apply {
            onClick(action)
            color = GREEN_BUTTON.col
            label.color = DARK_TEXT.col
            pad(30f)
        }

    override fun keyDown(keyCode: Int): Boolean {
        when (keyCode) {
            SPACE, ENTER -> game.play(ONE)
            else -> return false
        }
        return true
    }
}