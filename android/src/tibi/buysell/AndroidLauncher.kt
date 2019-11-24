package tibi.buysell

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication

class AndroidLauncher : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(BuySellGame())
    }

}
