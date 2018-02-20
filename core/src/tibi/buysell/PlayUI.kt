package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.actors.onClick
import tibi.buysell.BuySellGame.MyColors.*


class PlayUI(val game: BuySellGame, batch: SpriteBatch) : Stage(ScreenViewport(), batch) {

    val buyButton = TextButton("BUY", game.skin).apply {
        setPosition(10f, r(30f))
        height = r(100f)
        onClick { game.model.buy() }
    }

    val sellButton = TextButton("SELL", game.skin).apply {
        setPosition(10f, r(200f))
        height = r(100f)
        onClick { game.model.sell() }
    }

    init {
        addActor(buyButton)
        addActor(sellButton)
    }

    override fun act(delta: Float) {
        super.act(delta)
        buyButton.color = if (game.model.canBuy()) GREEN_BUTTON.col else DISABLED_BUTTON.col
        sellButton.color = if (game.model.canSell()) RED_BUTTON.col else DISABLED_BUTTON.col
    }

}


/** Converts dimensions relative to screen size */
fun r(a: Int) = a * Gdx.graphics.height / 480
fun r(a: Float) = r((a * 100).toInt()) / 100f
