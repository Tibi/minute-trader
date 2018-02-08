package tibi.buysell

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.app.KtxGame
import ktx.app.KtxScreen


class BuySellGame : KtxGame<KtxScreen>() {

    val skin by lazy { createSkin() }

    override fun create() {
        addScreen(MenuScreen(this))
        addScreen(PlayScreen(this))

        setScreen<MenuScreen>()
    }


    fun play(duration: Int) {
        getScreen<PlayScreen>().duration = duration.toFloat()
        setScreen<PlayScreen>()
    }

    fun gameFinished(model: Model) {

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
            up = skin.newDrawable("background", Color.GRAY)
            down = skin.newDrawable("background", Color.DARK_GRAY)
            checked = skin.newDrawable("background", Color.DARK_GRAY)
            over = skin.newDrawable("background", Color.LIGHT_GRAY)
            font = skin.getFont("default")
        }
        skin.add("default", textButtonStyle)

        val greenButtonStyle = TextButton.TextButtonStyle().apply {
            up = skin.newDrawable("background", Color.GREEN)
            down = skin.newDrawable("background", Color.GREEN)
            checked = skin.newDrawable("background", Color.GREEN)
            over = skin.newDrawable("background", Color.GREEN)
            font = skin.getFont("default")
        }
        skin.add("green", greenButtonStyle)

        val redButtonStyle = TextButton.TextButtonStyle().apply {
            up = skin.newDrawable("background", Color.RED)
            down = skin.newDrawable("background", Color.RED)
            checked = skin.newDrawable("background", Color.RED)
            over = skin.newDrawable("background", Color.RED)
            font = skin.getFont("default")
        }
        skin.add("red", redButtonStyle)
    }

    override fun dispose() {
        super.dispose()
        skin.dispose()
    }
}
