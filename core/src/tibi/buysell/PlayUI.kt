package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
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
    val skin = game.skin

    val qtyLabel = Label("", skin, "big")
    val balanceLabel = Label("", skin, "big")

    val sellButton = TextButton("SELL", skin).apply {
        onClick { if (!screen.paused) model.sell() }
        pad(30f)
    }

    val buyButton = TextButton("BUY", skin).apply {
        onClick { if (!screen.paused) model.buy() }
        pad(30f)
    }

    init {
        Scene2DSkin.defaultSkin = skin
        addActor(table {
//            debug()
            setFillParent(true)
            pad(30f)
            left()
            add(sellButton).left().padRight(30f)
            add(qtyLabel).right().row()
            add().padBottom(80f).row()
            add(buyButton).fill().padRight(30f)
            add(balanceLabel).right().row()
            add().expandY().row()
        })
    }

    override fun act(delta: Float) {
        super.act(delta)
        buyButton.color = if (model.canBuy()) RED_BUTTON.col else DISABLED_BUTTON.col
        sellButton.color = if (model.canSell()) GREEN_BUTTON.col else DISABLED_BUTTON.col
        qtyLabel.setText("${model.qty}")
        balanceLabel.setText("%,d $".format(model.moneyLeft.toInt()))
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

    fun gameOver() {
        val dialog = object : Dialog("Game Over!", skin)  {
            override fun remove(): Boolean {
                game.gameFinished()
                return super.remove()
            }
        }
        dialog.text("Score: ${model.moneyLeft}")
        dialog.button(TextButton("OK", skin).apply { color = GREEN_BUTTON.col })
        dialog.show(this)
    }
}


/** Converts dimensions relative to screen size */
fun r(a: Int) = a * Gdx.graphics.height / 480
fun r(a: Float) = r((a * 100).toInt()) / 100f
