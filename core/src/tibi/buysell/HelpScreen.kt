package tibi.buysell

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import ktx.scene2d.table

class HelpScreen(val game: BuySellGame) : KtxScreen {

    val stage = HelpStage(game)

    override fun show() {
        Gdx.input.inputProcessor = stage
        Scene2DSkin.defaultSkin = game.skin
        stage.addActor(table {
            setFillParent(true)
            label("The goal of the game is to make money"); row()
            label("by buying and selling coins."); row()
            label("The red line is you average buy price."); row()
            label("If you sell above it, you make money."); row()
            row().height(100f)
            label("Buy low, sell high!")
        })

    }

    override fun render(delta: Float) {
        clearScreen(
            BuySellGame.MyColors.BG.col.r,
            BuySellGame.MyColors.BG.col.g,
            BuySellGame.MyColors.BG.col.b
        )
        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
}

class HelpStage(val game: BuySellGame) : Stage(FitViewport(800f, 300f), game.batch) {
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        game.setScreen<MenuScreen>()
        return true
    }
}
