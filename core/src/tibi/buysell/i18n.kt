package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.I18NBundle
import com.badlogic.gdx.utils.I18NBundle.createBundle
import ktx.i18n.BundleLine

/**
 * I18n enum for KTX  https://github.com/libktx/ktx/tree/master/i18n
 */
enum class I18n : BundleLine {
    PLAY,
    SELL,
    BUY,
    GameOver,
    HighScore,
    NoHighscore,
    Score,
    SellFinish,
    HighScoreCongrats,
    NotBad,
    BetterNextTime,
    Amount,
    CoinsYouHave,
    MoneyLeft;

    override val bundle: I18NBundle
        get() = i18nBundle

    companion object {
        val i18nBundle: I18NBundle by lazy { createBundle(Gdx.files.internal("i18n/texts")) }
                                                          //Locale.FRENCH) }
    }
}
