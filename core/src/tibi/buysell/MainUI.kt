package tibi.buysell

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onClick


class MainUI(val screen: PlayScreen, skin: Skin) : Stage() {

    val buyButton = TextButton("BUY", skin, "green").apply {
        setPosition(10f, 30f)
        onClick { screen.model.buy() }
    }

    val sellButton = TextButton("SELL", skin, "red").apply {
        setPosition(10f, 130f)
        onClick { screen.model.sell() }
    }

    val pauseButton = TextButton("Pause", skin).apply {
        setPosition(10f, 230f)
        onClick { screen.paused = !screen.paused }
    }

    init {
        addActor(buyButton)
        addActor(sellButton)
        addActor(pauseButton)
    }

}