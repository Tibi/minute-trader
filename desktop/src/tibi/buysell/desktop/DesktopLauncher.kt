package tibi.buysell.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import tibi.buysell.BuySellGame

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.samples = 4
        // for android: config.numSamples = 2
        // see http://www.badlogicgames.com/wordpress/?p=2071
        LwjglApplication(BuySellGame(), config)
    }
}
