package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onClick
import ktx.app.KtxScreen


class MenuScreen(val game: BuySellGame) : KtxScreen {
    val ui = MenuStage(game)

    override fun show() {
        Gdx.input.inputProcessor = ui
    }

    override fun render(delta: Float) {
        ui.act()
        ui.draw()
    }
}

class MenuStage(val game: BuySellGame) : Stage() {

    val oneButton = TextButton("1 Minute Challenge", game.skin, "green").apply {
        setPosition(10f, 30f)
        onClick { game.play(60) }
    }

    val fiveButton = TextButton("5 Minutes Challenge", game.skin, "green").apply {
        setPosition(10f, 130f)
        onClick { game.play(5*60) }
    }

    init {
        addActor(oneButton)
        addActor(fiveButton)
    }
}