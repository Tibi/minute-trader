package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.I18NBundle
import ktx.app.KtxGame
import ktx.app.KtxScreen
import tibi.buysell.BuySellGame.Duration.ONE
import java.util.*


class BuySellGame : KtxGame<KtxScreen>() {

    val model = Model()
    val batch by lazy { SpriteBatch() }
    val logo: TextureRegion by lazy { skin.atlas.findRegion("icon-big") }
    val title: TextureRegion by lazy { skin.atlas.findRegion("title") }
    val highScores by lazy { Gdx.app.getPreferences("Minute Trader High Scores")!! }
    val txt by lazy { I18NBundle.createBundle(Gdx.files.internal("i18n/texts"), Locale.FRANCE) }

    enum class Duration(val minutes: Float, val description: String) {
        ONE(1f, "1 Minute"),
        THREE(3f, "3 Minutes")
    }

    var lastDuration = ONE

    val skin by lazy { Skin(Gdx.files.internal("skin/skin.json")) }


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
        DARK_TEXT("#7f56bb"),
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

    override fun dispose() {
        super.dispose()
        skin.dispose()
    }
}
