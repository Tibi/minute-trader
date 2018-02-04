package tibi.buysell.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import tibi.buysell.BuySellGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 4;
		// for android: config.numSamples = 2
		// see http://www.badlogicgames.com/wordpress/?p=2071
		new LwjglApplication(new BuySellGame(), config);
	}
}
