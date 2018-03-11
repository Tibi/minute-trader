package tibi.buysell

import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds


class AndroidLauncher : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this, "ca-app-pub-3949821178729385~4664104094")

        // Do the stuff that initialize() would do for you
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

        val layout = RelativeLayout(this)

        val config = AndroidApplicationConfiguration()
        config.useAccelerometer = false
        config.useCompass = false
        val gameView = initializeForView(BuySellGame(), config)

        layout.addView(gameView)
        layout.addView(createAdView(), createAdLayoutParams())

        setContentView(layout)
    }

    fun createAdView(): AdView {
        val adView = AdView(this)
        adView.adSize = AdSize.BANNER  // TODO bigger banner on the menu
        adView.adUnitId = "ca-app-pub-3949821178729385/2862624180"
        val adRequest = AdRequest.Builder()
                .addTestDevice("D59936D97A67EDB51825A30E54A190EA")  // my Motorola
                .build()
        adView.loadAd(adRequest)
        return adView
    }

    private fun createAdLayoutParams(): ViewGroup.LayoutParams {
        val marginParams = ViewGroup.MarginLayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT)
        marginParams.bottomMargin = 50
        val adParams = RelativeLayout.LayoutParams(marginParams)
        adParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        return adParams
    }

}
