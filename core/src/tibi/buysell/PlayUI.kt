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
import tibi.buysell.BuySellGame.Duration.ONE
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
            setFillParent(true)
            pad(30f)
            top()
            left()
            add(sellButton).left().padRight(30f)
            add(qtyLabel).right().row()
            add().padBottom(80f).row()
            add(buyButton).fill().padRight(30f)
            add(balanceLabel).right().row()
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
            Input.Keys.X -> gameOver()
            Input.Keys.P -> screen.paused = screen.paused.not()
            Input.Keys.ESCAPE -> game.setScreen<MenuScreen>()
            else -> return super.keyDown(key)
        }
        return true
    }

    fun gameOver() {
        screen.paused = true
        object : Dialog("", skin)  {
            override fun remove(): Boolean {
                game.gameFinished()
                return super.remove()
            }
        }.apply {
            background.minWidth = screen.screenWidth * .6f
            background.minHeight = screen.screenHeight * .6f
            removeActor(titleTable)
            contentTable.top()
            contentTable.defaults().pad(20f)
            contentTable.add(Label("Time's up!", skin, "big")).row()
            val score = model.moneyLeft.toInt()
            contentTable.add("Score: " + "%,d $".format(score)).padTop(50f).row()
            when {
                score > game.highScores.getInteger(ONE.name) -> text("That's a high score, congratulations!")
                score > START_AMOUNT -> text("No high score but not bad!")
                else -> text("You'll do better next time!")
            }
            contentTable.row()
            if (model.qty > 0) {
                text("You didn't sell all!")
            }
            onClick { hide() }
        }.show(this)
    }
}


/** Converts dimensions relative to screen size */
fun r(a: Int) = a * Gdx.graphics.height / 480
fun r(a: Float) = r((a * 100).toInt()) / 100f
