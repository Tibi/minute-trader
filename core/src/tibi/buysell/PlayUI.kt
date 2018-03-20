package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.actors.onClick
import ktx.i18n.get
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.table
import tibi.buysell.BuySellGame.Duration.ONE
import tibi.buysell.BuySellGame.MyColors.*


class PlayUI(val screen: PlayScreen, batch: SpriteBatch) : Stage(ScreenViewport(), batch) {

    val game = screen.game
    val model = game.model
    val skin = game.skin
    val txt = game.txt

    val qtyLabel = Label("", skin, "big")
    val balanceLabel = Label("", skin, "big")

    val sellButton = TextButton(txt["SELL"], skin).apply {
        onClick { if (!screen.paused) model.sell() }
        pad(30f)
    }

    val buyButton = TextButton(txt["BUY"], skin).apply {
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
            add(sellButton).fill().padRight(30f)
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
        balanceLabel.setText(txt["amount", model.moneyLeft.toInt()])
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
            contentTable.add(Label(txt["GameOver"], skin, "big")).row()
            val score = model.moneyLeft.toInt()
            contentTable.add(txt["Score", score]).padTop(50f).row()
            when {
                score > game.highScores.getInteger(ONE.name) -> text(txt["highScoreCongrats"])
                score > START_AMOUNT -> text(txt["notBad"])
                else -> text(txt["betterNextTime"])
            }
            contentTable.row()
            if (model.qty > 0) {
                text(txt["notSold"])
            }
            val clickListener = object : ClickListener() {
                // If clicked arount the center, hide the dialog.
              override fun clicked(event: InputEvent, x: Float, y: Float) {
                    println("x $x, y $y")
                  if (x > screen.screenWidth * 0.05 && x < screen.screenWidth * 0.55
                      && y > screen.screenHeight * 0.05 && y < screen.screenHeight * 0.55) {
                      hide()
                  }
              }
            }
            this.addListener(clickListener)
        }.show(this)
    }
}


/** Converts dimensions relative to screen size */
fun r(a: Int) = a * Gdx.graphics.height / 480
fun r(a: Float) = r((a * 100).toInt()) / 100f
