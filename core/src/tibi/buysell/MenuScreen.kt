package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys.ENTER
import com.badlogic.gdx.Input.Keys.SPACE
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.i18n.get
import tibi.buysell.BuySellGame.Duration.ONE
import tibi.buysell.BuySellGame.Duration.THREE
import tibi.buysell.BuySellGame.MyColors.*

class MenuScreen(val game: BuySellGame) : KtxScreen {
    val ui = MenuStage(game)
    val txt = game.txt

    override fun show() {
        Gdx.input.inputProcessor = ui
        Gdx.input.isCatchBackKey = false  // let Back exit the app
        listOf(ONE, THREE).forEach { duration ->
            val score = game.highScores.getInteger(duration.name)
            if (score > 0) {
                ui.labels[duration]?.setText(txt["highScore", score])
            }
        }
    }

    override fun render(delta: Float) {
        clearScreen(BG.col.r, BG.col.g, BG.col.b)
        ui.act()
        ui.draw()
    }

    override fun resize(width: Int, height: Int) {
        ui.viewport.update(width, height, true)
    }
}


class MenuStage(val game: BuySellGame) : Stage(ScreenViewport(), game.batch) {

    val durationsToShow = listOf(ONE)
    val buttons = durationsToShow.associate { it to button(it) }
    val labels  = durationsToShow.associate {
        it to Label(game.txt["noHighscore"], game.skin, "small", DARK_TEXT.col) }

    init {
        addActor(Table().apply {
//            debug()
            top()
            pad(20f)
            setFillParent(true)
            add(Image(game.logo).apply { setScaling(Scaling.fit) }).top()//.expand()
            add(Table().apply {
//                debug()
                top()
                padTop(40f)
                add(Image(game.title).apply { setScaling(Scaling.fit) }).colspan(2).row()
                add().height(r(100f)).row()//
                durationsToShow.forEach { dur ->
                    add(buttons[dur]).left()
                    add(labels[dur]).right()
                }
            }).top()
        })
    }

    fun button(duration: BuySellGame.Duration) =
            TextButton(game.txt["start"], game.skin).apply {
                onClick { game.play(duration) }
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