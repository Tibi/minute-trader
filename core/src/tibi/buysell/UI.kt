package tibi.buysell

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onClick


class UI(val model: Model) : Stage() {

    val skin = createBasicSkin()

    val buyButton = TextButton("BUY", skin).apply {
        setPosition(10f, 30f)
        onClick { model.buy() }
    }

    val sellButton = TextButton("SELL", skin).apply {
        setPosition(10f, 150f)
        onClick { model.sell() }
    }

    init {
        addActor(buyButton)
        addActor(sellButton)
    }


    fun createBasicSkin() = Skin().also { skin ->

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
    }

    override fun dispose() {
        super.dispose()
        skin.dispose()
    }
}