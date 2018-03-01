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
import tibi.buysell.BuySellGame.Duration.ONE
import tibi.buysell.BuySellGame.MyColors.DARK_TEXT
import tibi.buysell.BuySellGame.MyColors.WHITE


class BuySellGame : KtxGame<KtxScreen>() {

    val model = Model()
    val batch by lazy { SpriteBatch() }
    val logo by lazy { Texture("icon128.png") }
    val highScores by lazy { Gdx.app.getPreferences("Minute Trader High Scores")!! }

    enum class Duration(val minutes: Float, val description: String) {
        ONE(1f, "1 Minute"),
        THREE(3f, "3 Minutes")
    }

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
        val lastScore = highScores.getInteger(lastDuration.toString())
        val score = model.moneyLeft.toInt()
        if (score > lastScore) {
            highScores.putInteger(lastDuration.toString(), score)
            highScores.flush()
        }
        setScreen<MenuScreen>()
    }


    // http://paletton.com/#uid=73a1g0kbRt14+E48dwffUpTkImm
    enum class MyColors(colorStr: String) {
        BG("#FFFFFF"),
        WHITE("#FFFFFF"),
        TEXT_BG("#EEEEEE99"),
        BRIGHT_TEXT("#B3A9C5"),
        DARK_TEXT("#4D3279"),
        GREEN_BUTTON("#58ed7f"),
        GREEN_BUTTON_DOWN("#8ef3a8"),
        GREEN_BG("#C5FFC5"),
        RED_BUTTON("#ff9595"),
        RED_BUTTON_DOWN("#ffcece"),
        RED_BG("#FFE5E5"),
        DISABLED_BUTTON("#AAAAAA"),
        RED("#FF0000"),
        CURVE("#0081f2"),
        AXIS_MAIN("#808080"),
        AXIS_LIGHT("#e0e0e0"),

        TRANSPARENT("#00000000")
        ;
        val col: Color = Color.valueOf(colorStr)
    }


    private fun createSkin() = Skin().also { skin ->

        // Fonts
        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Medium.ttf"))
        val param = FreeTypeFontGenerator.FreeTypeFontParameter()
        param.size = r(20)
        skin.add("small", generator.generateFont(param))
        param.size = r(40)
        skin.add("big", generator.generateFont(param))
        param.color = DARK_TEXT.col
        param.size = r(80)
        param.borderWidth = 2f
        param.borderColor = DARK_TEXT.col.cpy().add(.2f, .2f, .2f, 0f)
//        param.shadowOffsetX = 3
//        param.shadowOffsetY = 3
        skin.add("title", generator.generateFont(param))

        // Buttons background
        // Try to make all buttons the same size in mm on screen
        // or use  Gdx.graphics.getDensity() ?
        val pixmap = createRoundedRectangle(200, Gdx.graphics.ppiY.toInt(), 4, Color.WHITE)
        skin.add("background", Texture(pixmap))

        // Button style
        val defaultButtonStyle = TextButton.TextButtonStyle().apply {
            up = skin.newDrawable("background", WHITE.col)
            down = skin.newDrawable("background", WHITE.col)
            checked = skin.newDrawable("background", WHITE.col)
            over = skin.newDrawable("background", WHITE.col)
            font = skin.getFont("big")
        }
        skin.add("default", defaultButtonStyle)

        val labelStyle = Label.LabelStyle().apply {
            font = skin.getFont("big")
            fontColor = DARK_TEXT.col
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
