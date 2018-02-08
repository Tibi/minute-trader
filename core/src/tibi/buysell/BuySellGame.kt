package tibi.buysell

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.copy
import tibi.buysell.BuySellGame.Duration.FIVE
import tibi.buysell.BuySellGame.Duration.ONE


class BuySellGame : KtxGame<KtxScreen>() {

    val model = Model()

    enum class Duration(val minutes: Float, val description: String) {
        ONE(1f, "One Minute"),
        FIVE(5f, "Five Minutes")
    }

    val highScores = mutableMapOf(ONE to 0, FIVE to 0)
    var lastDuration = ONE

    val skin by lazy { createSkin() }

    override fun create() {
        addScreen(MenuScreen(this))
        addScreen(PlayScreen(this))

        setScreen<MenuScreen>()
    }


    fun play(duration: Duration) {
        model.clear()
        lastDuration = duration
        getScreen<PlayScreen>().duration = duration.minutes * 60
        setScreen<PlayScreen>()
    }

    fun gameFinished() {
        highScores[lastDuration] = model.moneyLeft.toInt()
        setScreen<MenuScreen>()
    }


    private fun createSkin() = Skin().also { skin ->

        //Create a font
        skin.add("default", BitmapFont())

        //Create a texture
        val pixmap = Pixmap(80, 60, Pixmap.Format.RGB888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        skin.add("background", Texture(pixmap))

        //Create a button style
        val textButtonStyle = TextButton.TextButtonStyle().apply {
            up = skin.newDrawable("background", Color.DARK_GRAY)
            down = skin.newDrawable("background", Color.GRAY)
            checked = skin.newDrawable("background", Color.DARK_GRAY)
            over = skin.newDrawable("background", Color.DARK_GRAY)
            font = skin.getFont("default")
        }
        skin.add("default", textButtonStyle)

        val darkGreen = Color.GREEN.copy(green = 0.5f)

        val greenButtonStyle = TextButton.TextButtonStyle().apply {
            up = skin.newDrawable("background", darkGreen)
            down = skin.newDrawable("background", Color.GREEN)
            checked = skin.newDrawable("background", darkGreen)
            over = skin.newDrawable("background", darkGreen)
            font = skin.getFont("default")
        }
        skin.add("green", greenButtonStyle)

        val darkRed = Color.RED.copy(red = 0.5f)

        val redButtonStyle = TextButton.TextButtonStyle().apply {
            up = skin.newDrawable("background", darkRed)
            down = skin.newDrawable("background", Color.RED)
            checked = skin.newDrawable("background", darkRed)
            over = skin.newDrawable("background", darkRed)
            font = skin.getFont("default")
        }
        skin.add("red", redButtonStyle)

        val labelStyle = Label.LabelStyle().apply {
            font = skin.getFont("default")
        }
        skin.add("default", labelStyle)
    }

    override fun dispose() {
        super.dispose()
        skin.dispose()
    }
}
