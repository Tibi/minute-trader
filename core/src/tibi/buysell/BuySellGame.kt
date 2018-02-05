package tibi.buysell

import ktx.app.KtxGame
import ktx.app.KtxScreen


class BuySellGame : KtxGame<KtxScreen>() {

//    lateinit var batch: SpriteBatch
//    lateinit var shape: ShapeRenderer
//    lateinit var font: BitmapFont

    override fun create() {
//        batch = SpriteBatch()
//        shape = ShapeRenderer()
//        font = BitmapFont()
        addScreen(MainScreen())
        setScreen<MainScreen>()
    }

//    override fun dispose() {
//        batch.dispose()
//        font.dispose()
//        shape.dispose()
//    }
}
