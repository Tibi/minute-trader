package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.actors.onClick
import tibi.buysell.BuySellGame.MyColors.*


class PlayUI(val game: BuySellGame, batch: SpriteBatch) : Stage(ScreenViewport(), batch) {

    val balanceLabel = Label("", game.skin).apply {
        setPosition(10f, 400f)
        color = DARK_TEXT.col
        height = 100f
    }

    val sellButton = TextButton("SELL", game.skin).apply {
        setPosition(10f, r(200f))
        height = r(100f)
        onClick { game.model.sell() }
    }

    val buyButton = TextButton("BUY", game.skin).apply {
        setPosition(10f, r(30f))
        height = r(100f)
        onClick { game.model.buy() }
    }

    init {
        val table = Table(game.skin).top()
//        table.debug()
        table.setFillParent(true)
        table.pad(30f).left()
        table.add(balanceLabel).expandY().top().row()
        table.add(sellButton).left().padBottom(50f).row()
        table.add(buyButton).left()
        addActor(table)
    }

    override fun act(delta: Float) {
        super.act(delta)
        buyButton.color = if (game.model.canBuy()) GREEN_BUTTON.col else DISABLED_BUTTON.col
        sellButton.color = if (game.model.canSell()) RED_BUTTON.col else DISABLED_BUTTON.col
        val model = game.model
        balanceLabel.setText("${model.qty} Owned\n" + "%,d $ Left".format(model.moneyLeft.toInt()))
    }

}


/** Converts dimensions relative to screen size */
fun r(a: Int) = a * Gdx.graphics.height / 480
fun r(a: Float) = r((a * 100).toInt()) / 100f
