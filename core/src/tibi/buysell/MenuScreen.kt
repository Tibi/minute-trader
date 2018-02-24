package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys.ENTER
import com.badlogic.gdx.Input.Keys.SPACE
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.app.clearScreen
import tibi.buysell.BuySellGame.Duration.ONE
import tibi.buysell.BuySellGame.Duration.THREE
import tibi.buysell.BuySellGame.MyColors.BG
import tibi.buysell.BuySellGame.MyColors.DARK_TEXT


class MenuScreen(val game: BuySellGame) : KtxScreen {
    val ui = MenuStage(game)

    override fun show() {
        Gdx.input.inputProcessor = ui
        listOf(ONE, THREE).forEach { duration ->
            val score = game.highScores.getInteger(duration.toString())
            if (score > 0) {
                ui.labels[duration]?.setText("High Score: %,d $".format(score))
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
    val labels  = durationsToShow.associate { it to Label("no highscore", game.skin, "small", DARK_TEXT.col) }

    init {
        val table = Table(game.skin).top()
//        table.debug()
        table.setFillParent(true)
        val drawable = TextureRegionDrawable(TextureRegion(game.logo))
        val image = Image(drawable, Scaling.none)
        table.add(image).expand()
        table.add("Minute Trader", "title", Color.WHITE).colspan(2)
        table.add().expand().row()
        durationsToShow.forEach { dur ->
            table.add().expandY()
            table.add(buttons[dur]).minWidth(r(230f)).left()
            table.add(labels[dur]).right()
            table.add().row()
        }
        table.add().expandY().row()
        addActor(table)
    }

    fun button(duration: BuySellGame.Duration) =
            TextButton("START"/*duration.description*/, game.skin, "green").apply {
                onClick { game.play(duration) }
                height = r(100f)
            }

    override fun keyDown(keyCode: Int): Boolean {
        when (keyCode) {
            SPACE, ENTER -> game.play(ONE)
            else -> return false
        }
        return true
    }
}