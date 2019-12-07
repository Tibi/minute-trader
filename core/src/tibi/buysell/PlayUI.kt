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
import ktx.i18n.get
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.table
import tibi.buysell.BuySellGame.Duration.ONE
import tibi.buysell.BuySellGame.MyColors.*


class PlayUI(val screen: PlayScreen) : Stage(ScreenViewport(), screen.batch) {

    val game = screen.game
    val model = game.model
    val skin = game.skin
    val txt = game.txt

    val qtyLabel = Label("", skin, "big")
    val balanceLabel = Label("", skin, "big")

    val sellButton = TextButton(txt["SELL"], skin).apply { pad(30f) }
    val buyButton = TextButton(txt["BUY"], skin).apply { pad(30f) }

    var tutoDialog: Dialog? = null
    var tutoDialogComp: Label = qtyLabel

    init {
        Scene2DSkin.defaultSkin = skin
        fun pos(dia: Dialog, button: TextButton) {
            dia.setPosition(button.x + button.width,
                            button.y + (button.height - dia.height) / 2)
        }
        sellButton.onClick {
            if (screen.paused) return@onClick
            if (!model.canSell() && !game.prefs.getBoolean("tutoSellDone")) {
                screen.paused = true
                val dia = dialog(txt["allSold"]) {
                    screen.paused = false
                    game.prefs.putBoolean("tutoSellDone", true)
                    game.prefs.flush()
                }
                dia.show(this@PlayUI)
                pos(dia, sellButton)
            }
            if (model.time > game.lastDuration.minutes * 60 - 3) {
                model.sellAll()
            } else {
                model.sell()
            }
        }
        buyButton.onClick {
            if (screen.paused) return@onClick
            if (!model.canBuy() && !game.prefs.getBoolean("tutoBuyDone")) {
                screen.paused = true
                val dia = dialog(txt["noMoney"]) {
                    screen.paused = false
                    game.prefs.putBoolean("tutoBuyDone", true)
                    game.prefs.flush()
                }
                dia.show(this@PlayUI)
                pos(dia, buyButton)
            }
            model.buy()
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
        balanceLabel.setText(txt["amount", model.moneyLeft.toInt()])
        tutoDialog?.setPosition(tutoDialogComp.right + 20, tutoDialogComp.y)
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
        dialog(txt["GameOver"]) { game.gameFinished() }.apply {
            background.minWidth = screen.screenWidth * .6f
            background.minHeight = screen.screenHeight * .6f
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
            show(this@PlayUI)
        }
    }

    fun tuto() {
        if (game.prefs.getBoolean("tutoDone")) return
        screen.paused = true
        tutoDialogComp = qtyLabel
        tutoDialog = dialog("") {
            tuto2()
        }.apply {
            contentTable.add(txt["tuto1"])
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
            contentTable.add(txt["tuto2"])
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
