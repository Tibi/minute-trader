package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.actors.onClick
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.table
import tibi.buysell.BuySellGame.MyColors.*


class PlayUI(val screen: PlayScreen, batch: SpriteBatch) : Stage(ScreenViewport(), batch) {

    val game = screen.game
    val model = game.model

    val qtyLabel = Label("", game.skin).apply {
        color = DARK_TEXT.col
    }
    val balanceLabel = Label("", game.skin).apply {
        color = DARK_TEXT.col
    }

    val sellButton = TextButton("SELL", game.skin).apply {
        onClick { game.model.sell() }
    }

    val buyButton = TextButton("BUY", game.skin).apply {
        onClick { game.model.buy() }
    }

    init {
        Scene2DSkin.defaultSkin = game.skin
        addActor(table {
//            debug()
            setFillParent(true)
            pad(30f)
            left()
            add(sellButton)
            add(qtyLabel).right().row()
            add().padBottom(80f).row()
            add(buyButton)
            add(balanceLabel).right().row()
            add().expandY().row()
        })
    }

    override fun act(delta: Float) {
        super.act(delta)
        buyButton.color = if (model.canBuy()) GREEN_BUTTON.col else DISABLED_BUTTON.col
        sellButton.color = if (model.canSell()) RED_BUTTON.col else DISABLED_BUTTON.col
        qtyLabel.setText("${model.qty} Owned" )
        balanceLabel.setText("%,d $ Left".format(model.moneyLeft.toInt()))
    }

    override fun keyDown(key: Int): Boolean {
        when (key) {
            Input.Keys.B -> model.buy()
            Input.Keys.S -> model.sell()
            Input.Keys.P -> screen.paused = screen.paused.not()
            Input.Keys.ESCAPE -> game.setScreen<MenuScreen>()
            else -> return super.keyDown(key)
        }
        return true
    }

}


/** Converts dimensions relative to screen size */
fun r(a: Int) = a * Gdx.graphics.height / 480
fun r(a: Float) = r((a * 100).toInt()) / 100f
