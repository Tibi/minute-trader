package tibi.buysell

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.actors.onClick


class UI(val model: Model, viewport: Viewport) : Stage(viewport) {

    val skin = createBasicSkin()

    val buyButton = TextButton("BUY", skin).apply {
        setPosition(10f, 50f)
        onClick { model.buy() }
    }

    val sellButton = TextButton("SELL", skin).apply {
        setPosition(400f, 50f)
        onClick { model.sell() }
    }

    init {
        addActor(buyButton)
        addActor(sellButton)
    }

    fun createBasicSkin(): Skin {
        //Create a font
        val font = BitmapFont()
        val skin = Skin()
        skin.add("default", font)

        //Create a texture
        val pixmap = Pixmap(80, 30, Pixmap.Format.RGB888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        skin.add("background", Texture(pixmap))

        //Create a button style
        val textButtonStyle = TextButton.TextButtonStyle()
        textButtonStyle.up = skin.newDrawable("background", Color.GRAY)
        textButtonStyle.down = skin.newDrawable("background", Color.DARK_GRAY)
        textButtonStyle.checked = skin.newDrawable("background", Color.DARK_GRAY)
        textButtonStyle.over = skin.newDrawable("background", Color.LIGHT_GRAY)
        textButtonStyle.font = skin.getFont("default")
        skin.add("default", textButtonStyle)
        return skin
    }
}