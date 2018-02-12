package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.copy
import tibi.buysell.BuySellGame.Duration.ONE
import tibi.buysell.BuySellGame.Duration.THREE
import tibi.buysell.BuySellGame.MyColors.*


class BuySellGame : KtxGame<KtxScreen>() {

    val model = Model()
    lateinit var batch: SpriteBatch

    enum class Duration(val minutes: Float, val description: String) {
        ONE(1f, "One Minute"),
        THREE(3f, "Three Minutes")
    }

    val highScores = mutableMapOf(ONE to 0, THREE to 0)
    var lastDuration = ONE

    val skin by lazy { createSkin() }

    override fun create() {
        batch = SpriteBatch()

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

    // http://paletton.com/#uid=73a1g0kbRt14+E48dwffUpTkImm
    enum class MyColors(colorStr: String) {
        BG("#FFFBD7"),
        TEXT_BG("#B3A9C5"),
        BRIGHT_TEXT("#B3A9C5"),
        DARK_TEXT("#4D3279"),
        GREEN_BUTTON("#297461"),
        GREEN_BUTTON_DOWN("#7CA69C"),
        RED_BUTTON("#B26A3F"),
        RED_BUTTON_DOWN("#FFD6BE"),
        RED("#FF0000"),
        DRAW("#654D8D"),
        DRAW_LIGHT("#9586AE"),

        TRANSPARENT("#00000000")
        ;
        val col: Color = Color.valueOf(colorStr)
    }


    private fun createSkin() = Skin().also { skin ->

        //Create a font
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Medium.ttf"))
        val param = FreeTypeFontGenerator.FreeTypeFontParameter()
        param.size = 18
        skin.add("default", generator.generateFont(param))
        param.color = DARK_TEXT.col
        param.size = 80
//        param.borderWidth = 2f
//        param.borderColor = Color.BLACK
//        param.shadowOffsetX = 3
//        param.shadowOffsetY = 3
        skin.add("title", generator.generateFont(param))

        //Create a texture
        val pixmap = createRoundedRectangle(200, 60, 4, Color.WHITE)
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

        val greenButtonStyle = TextButton.TextButtonStyle().apply {
            up = skin.newDrawable("background", GREEN_BUTTON.col)
            down = skin.newDrawable("background", GREEN_BUTTON_DOWN.col)
            checked = skin.newDrawable("background", GREEN_BUTTON.col)
            over = skin.newDrawable("background", GREEN_BUTTON.col)
            font = skin.getFont("default")
        }
        skin.add("green", greenButtonStyle)

        val redButtonStyle = TextButton.TextButtonStyle().apply {
            up = skin.newDrawable("background", RED_BUTTON.col)
            down = skin.newDrawable("background", RED_BUTTON_DOWN.col)
            checked = skin.newDrawable("background", RED_BUTTON.col)
            over = skin.newDrawable("background", RED_BUTTON.col)
            font = skin.getFont("default")
        }
        skin.add("red", redButtonStyle)

        val labelStyle = Label.LabelStyle().apply {
            font = skin.getFont("default")
        }
        skin.add("default", labelStyle)
    }

    fun createRoundedRectangle(width: Int, height: Int, cornerRadius: Int, color: Color): Pixmap {

        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        val ret = Pixmap(width, height, Pixmap.Format.RGBA8888)

        pixmap.setColor(color)

        pixmap.fillCircle(cornerRadius, cornerRadius, cornerRadius)
        pixmap.fillCircle(width - cornerRadius - 1, cornerRadius, cornerRadius)
        pixmap.fillCircle(cornerRadius, height - cornerRadius - 1, cornerRadius)
        pixmap.fillCircle(width - cornerRadius - 1, height - cornerRadius - 1, cornerRadius)

        pixmap.fillRectangle(cornerRadius, 0, width - cornerRadius * 2, height)
        pixmap.fillRectangle(0, cornerRadius, width, height - cornerRadius * 2)

        ret.setColor(color)
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (pixmap.getPixel(x, y) != 0) ret.drawPixel(x, y)
            }
        }
        pixmap.dispose()

        return ret
    }

    override fun dispose() {
        super.dispose()
        skin.dispose()
    }
}
