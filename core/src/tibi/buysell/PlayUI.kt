package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys.*
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.actors.onClick
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.table
import tibi.buysell.BuySellGame.Duration.ONE
import tibi.buysell.BuySellGame.MyColors.*
import tibi.buysell.I18n.*


class PlayUI(val screen: PlayScreen) : Stage(ScreenViewport(), screen.batch) {

    val game = screen.game
    val model = game.model
    val skin = game.skin

    val qtyLabel = Label("", skin, "big")
    val balanceLabel = Label("", skin, "big")

    val sellButton = TextButton(SELL.nls, skin).apply { pad(30f); onClick { model.sell() }}
    val buyButton  = TextButton(BUY.nls,  skin).apply { pad(30f); onClick { model.buy()  }}

    var tutoDialog: Dialog? = null
    var tutoDialogComp: Label = qtyLabel

    init {
        Scene2DSkin.defaultSkin = skin
        fun pos(dia: Dialog, button: TextButton) {
            dia.setPosition(button.x + button.width,
                            button.y + (button.height - dia.height) / 2)
        }

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
        qtyLabel.setText(model.qty)
        balanceLabel.setText(Amount.nls(model.moneyLeft.toInt()))
        val dialog = tutoDialog
        dialog?.setPosition(tutoDialogComp.right + 20,
                            tutoDialogComp.y + (tutoDialogComp.height - dialog.height) / 2)
    }

    override fun keyDown(key: Int): Boolean {
        when (key) {
            B -> model.buy()
            S -> model.sell()
            X -> gameOver()
            SPACE -> screen.paused = screen.paused.not()
            ESCAPE, BACK -> game.setScreen<MenuScreen>()
            else -> return super.keyDown(key)
        }
        return true
    }

    fun gameOver() {
        screen.paused = true
        dialog(GameOver.nls) { game.gameFinished() }.apply {
            background.minWidth = screen.screenWidth * .6f
            background.minHeight = screen.screenHeight * .6f
            val score = model.moneyLeft.toInt()
            contentTable.add(Score.nls(score)).padTop(50f).row()
            when {
                score > game.highScores.getInteger(ONE.name) -> text(HighScoreCongrats.nls)
                score > START_AMOUNT -> text(NotBad.nls)
                else -> text(BetterNextTime.nls)
            }
            contentTable.row()
            show(this@PlayUI)
        }
    }

    fun tuto() {
        if (game.prefs.getBoolean("tutoDone")) return
        screen.paused = true
        tutoDialogComp = qtyLabel
        tutoDialog = dialog("") { tuto2() }.apply {
            contentTable.add(CoinsYouHave.nls)
            show(this@PlayUI)
        }
    }

    fun tuto2() {
        tutoDialogComp = balanceLabel
        tutoDialog = dialog("") {
            tutoDialog = null
            screen.paused = false
            game.prefs.putBoolean("tutoDone", true)
        }.apply {
            contentTable.add(MoneyLeft.nls)
            show(this@PlayUI)
        }
    }

    /** Displays a dialog */
    fun dialog(title: String, onClose: () -> Unit) = object : Dialog("", skin) {
        override fun remove(): Boolean {
            onClose()
            return super.remove()
        }
    }.apply {
        contentTable.top()
        contentTable.defaults().pad(20f)
        if (title.isNotEmpty()) {
            contentTable.add(Label(title, skin, "big")).row()
        }
        val clickListener = object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                hide()
            }
        }
        addListener(clickListener)
    }
}



/** Converts dimensions relative to screen size */
fun r(a: Int) = a * Gdx.graphics.height / 480
fun r(a: Float) = r((a * 100).toInt()) / 100f
