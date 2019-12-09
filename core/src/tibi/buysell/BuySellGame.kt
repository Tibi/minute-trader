package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.scene2d.Scene2DSkin
import tibi.buysell.BuySellGame.Duration.ONE


class BuySellGame : KtxGame<KtxScreen>() {

    val model = Model()
    val batch by lazy { SpriteBatch() }
    val logo: TextureRegion by lazy { skin.atlas.findRegion("icon-big") }
    val title: TextureRegion by lazy { skin.atlas.findRegion("title") }
    val highScores by lazy { Gdx.app.getPreferences("Minute Trader High Scores")!! }
    val prefs by lazy { Gdx.app.getPreferences("Minute Trader Preferences")!! }
    val DREAM_LO_KEY = "UMgyw1KYek2U-ivo7_BXcA1qVnI7jAUkGdRo1LzTZizA"
    val leaderboard: Leaderboard by lazy { DreamLoLeaderboard(DREAM_LO_KEY) }

    enum class Duration(val minutes: Float, val description: String) {
        ONE(1f, "1 min"),
        THREE(3f, "3 min")
    }

    var lastDuration = ONE

    val skin by lazy { Skin(Gdx.files.internal("skin/skin.json")) }


    override fun create() {
        Scene2DSkin.defaultSkin = skin
        addScreen(MenuScreen(this))
        addScreen(PlayScreen(this))
        addScreen(HelpScreen(this))
        addScreen(ScoreScreen(this))
        setScreen<MenuScreen>()
//        play(ONE)
    }

    fun help() {
        setScreen<HelpScreen>()
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
        DISABLED_BUTTON("#e0e0e0"),
        RED("#FF0000"),
        CURVE("#0081f2"),
        AXIS_MAIN("#808080"),
        AXIS_LIGHT("#e0e0e0"),

        TRANSPARENT("#00000000")
        ;
        // TODO use colors defined in the skin
        val col: Color = Color.valueOf(colorStr)
    }

    override fun dispose() {
        super.dispose()
        skin.dispose()
    }
}
